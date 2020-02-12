package ru.ttmf.mark.positions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.BidiFormatter;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.widget.TextView;

import com.squareup.okhttp.internal.framed.Variant;

import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.Position;
import ru.ttmf.mark.preference.PreferenceController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static ru.ttmf.mark.invoice.InvoiceFragment.*;

public class PositionsActivity extends ScanActivity implements Observer<Response>, PositionsAdapter.OnPositionClickListener {

    private PositionsViewModel viewModel;
    private ProgressDialog progressDialog;
    private Integer totalPositions = 0;
    private Integer scannedPositions = 0;
    @BindView(R.id.positions)
    RecyclerView rvPositions;

    @BindView(R.id.cipher)
    TextView cipher;

    PositionsAdapter positionsAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView totalPositionsTextView;
    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);
        ButterKnife.bind(this);
        initToolbar(toolbar, getString(R.string.positions));
        viewModel = ViewModelProviders.of(this)
                .get(PositionsViewModel.class);
        String cipherText = getIntent().getExtras().getString(CIPHER);
        String id = getIntent().getExtras().getString(ID);

        if (getIntent().getExtras() != null)
            viewModel.setDataType((DataType) getIntent().getSerializableExtra(DATA_TYPE));

        viewModel.loadPositions(PreferenceController.getInstance().getToken(), id).observe(this, this);
        cipher.setText(getString(R.string.cipher, cipherText));
        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }
        });
        registerReceiver(intentBarcodeDataReceiver, intentFilter);
        totalPositionsTextView = (TextView) findViewById(R.id.totalPositions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(intentBarcodeDataReceiver);
    }

    public void initToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public void onChanged(@Nullable Response response) {
        switch (response.getStatus()) {
            case ERROR:
                hideProgressDialog();
                showErrorDialog(response.getError());
                break;
            case LOADING:
                showProgressDialog();
                break;
            case SUCCESS:
                switch (response.getType()) {
                    case GetPositions:
                        hideProgressDialog();
                        List<Position> positionsList = (List<Position>) response.getObject();
                        viewModel.setPositions(positionsList);
                        totalPositions = positionsList.size();
                        totalPositionsTextView.setText("(" + 0 + "/" + totalPositions + ")");
                        initPositionsRecycler(positionsList);
                        break;
                    case SavePositions:
                        hideProgressDialog();
                        finish();
                        break;

                }

                break;
        }
    }

    private void initPositionsRecycler(List<Position> positionList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPositions.setLayoutManager(linearLayoutManager);
        positionsAdapter = new PositionsAdapter(positionList, this);
        rvPositions.setAdapter(positionsAdapter);
    }

    private void successfulScan(String code) {
        DataMatrix matrix = new DataMatrix();
        DataMatrixHelpers.splitStr(matrix, code, 29);

        for (int i = 0; i < positionsAdapter.getItems().size(); i++) {
            if (positionsAdapter.getItems().get(i).getSgTin().equals(matrix.SGTIN()) || positionsAdapter.getItems().get(i).getSgTin().equals(matrix.SSCC())) {
                scannedPositions++;
                totalPositionsTextView.setText("(" + scannedPositions + "/" + totalPositions + ")");

                if ((matrix.SGTIN().equals("nullnull")) && !(matrix.SSCC().equals("null"))) {
                    positionsAdapter.removeItem(matrix.SSCC());
                } else {
                    positionsAdapter.removeItem(matrix.SGTIN());
                }
            }
        }

        if (positionsAdapter.getItemCount() == 0) {
            showSaveDialog(getString(R.string.scan_finish));
        }
    }

    private List<String> getPositions(List<Position> positions) {
        List<String> res = new ArrayList<>();
        positions.removeAll(positionsAdapter.getItems());
        for (Position position : positions) {
            res.add(position.getSgTin());
        }
        return res;
    }

    public void onBackPressed() {
        if (positionsAdapter != null && positionsAdapter.getItems() != null && viewModel.getPositions() != null &&
                positionsAdapter.getItems().size() != viewModel.getPositions().size()) {
            showSaveDialog(getString(R.string.save_scan));
        } else {
            finish();
        }

    }

    private void save() {
        viewModel.savePositions(
                PreferenceController.getInstance().getToken(),
                PreferenceController.getInstance().getUserId(),
                getIntent().getExtras().getString(ID),
                getIntent().getExtras().getString(NAME),
                getPositions(viewModel.getPositions()))
                .observe(this, this);
    }

    private void showSaveDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            save();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
            showDeleteDialog();
        });
        builder.show();
    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_scan);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            finish();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss());
        builder.show();
    }


    @Override
    public void onPositionClick(Position position) {
        successfulScan(position.getSgTin());
    }

    public void onDecodeComplete(int symbology, int length, String barcode) {
        successfulScan(barcode);
    }

    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showErrorDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text);
        builder.setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();

    }
}
