package ru.ttmf.mark.consumption_positions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.Position;
import ru.ttmf.mark.positions.PositionsAdapter;
import ru.ttmf.mark.preference.PreferenceController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static ru.ttmf.mark.invoice.InvoiceFragment.*;

public class ConsumptionPositionsActivity extends ScanActivity implements Observer<Response> {
    public static final String CODE = "CODE";
    private ConsumptionPositionsViewModel viewModel;
    private ProgressDialog progressDialog;

    @BindView(R.id.positions)
    RecyclerView rvPositions;

    @BindView(R.id.scanned)
    TextView scanned;

    ConsumptionPositionsAdapter positionsAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_positions);
        ButterKnife.bind(this);
        initToolbar(toolbar, getString(R.string.positions));
        viewModel = ViewModelProviders.of(this)
                .get(ConsumptionPositionsViewModel.class);

        String cipherText = getIntent().getExtras().getString(CIPHER);
        String id = getIntent().getExtras().getString(ID);

        if (getIntent().getExtras() != null)
            viewModel.setDataType((DataType) getIntent().getSerializableExtra(DATA_TYPE));

        viewModel.searchPositions(PreferenceController.getInstance().getToken(), id).observe(this, this);
        scanned.setText(getString(R.string.cipher, cipherText));

        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }
        });
        registerReceiver(intentBarcodeDataReceiver, intentFilter);
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
                    case GetConsumptionPositions:
                        hideProgressDialog();
                        ConsumptionResponse.Data data = (ConsumptionResponse.Data) response.getObject();
                        viewModel.setValidPositions(data.getValidPositions());
                        viewModel.setScannedPositions(data.getPositions());
                        initPositionsRecycler(viewModel.getScannedPositions().getValue());
                        break;
                    case SavePositions:
                        hideProgressDialog();
                        finish();
                        break;

                }

                break;
        }
    }

    private void initPositionsRecycler(List<String> positionList) {
        if (positionList == null) {
            positionList = new ArrayList<>();
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvPositions.setLayoutManager(linearLayoutManager);
        positionsAdapter = new ConsumptionPositionsAdapter(positionList);
        rvPositions.setAdapter(positionsAdapter);
    }

    private void successfulScan(String code) {

        DataMatrix matrix = new DataMatrix();
        DataMatrixHelpers.splitStr(matrix, code, 29);
        String decoded = matrix.SGTIN();

        List<String> positions = viewModel.getValidPositions();

        if (positions != null && !positions.contains(decoded)) {
            Toast.makeText(this, R.string.wrong_package_specification, Toast.LENGTH_SHORT).show();
            return;
        }

        positions = viewModel.getScannedPositions().getValue();

        if (positions != null && positions.contains(decoded)) {
            Toast.makeText(this, R.string.already_scanned, Toast.LENGTH_SHORT).show();
            return;
        }

        positionsAdapter.addItem(decoded);
        viewModel.addPositions(decoded);
    }

    private List<String> getPositions(List<Position> positions) {
        List<String> res = new ArrayList<>();
        for (Position position : positions) {
            res.add(position.getSgTin());
        }
        return res;
    }

    public void onBackPressed() {
        if (viewModel.getScannedPositions().getValue() != null) {
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
                viewModel.getScannedPositions().getValue())
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
