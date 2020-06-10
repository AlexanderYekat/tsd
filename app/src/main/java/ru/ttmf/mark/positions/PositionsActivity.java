package ru.ttmf.mark.positions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MenuItem;
import android.widget.EditText;
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
import ru.ttmf.mark.network.model.Position;
import ru.ttmf.mark.preference.PreferenceController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static ru.ttmf.mark.Helpers.Helpers.isNumeric;
import static ru.ttmf.mark.invoice.InvoiceFragment.*;

public class PositionsActivity extends ScanActivity implements Observer<Response>, PositionsAdapter.OnPositionClickListener {

    private PositionsViewModel viewModel;
    private ProgressDialog progressDialog;
    private Integer totalPositions;
    private Integer scannedPositions = 0;
    private String TTN_TYPE;
    private String Ean;
    private Object savePosition;
    private List<PositionsSaveModel> reverseDirectPosition;
    private AlertDialog.Builder builder;
    private String countBarcodes = "";
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
        Ean = getIntent().getExtras().getString(EAN13);
        //totalPositions = Math.round(Float.parseFloat(getIntent().getExtras().getString(COUNT)));
        reverseDirectPosition = new ArrayList<PositionsSaveModel>();
        if (getIntent().getExtras() != null)
            viewModel.setDataType((DataType) getIntent().getSerializableExtra(DATA_TYPE));

        TTN_TYPE = getIntent().getExtras().getString("TTN_TYPE");
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


        //Всплывающее окно с вводом количества сканируемых штрихкодов///
        /*builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите количество:");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setText("1");
        builder.setView(input);
        // Set up the buttons
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                countBarcodes = input.getText().toString();
            }
        });
        builder.show();*/
        ///////////////////////////////////////////////////////////////
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
                        totalPositionsTextView.setText("(" + scannedPositions + "/" + totalPositions + ")");
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
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);

            if (TTN_TYPE.equals("1")) {
                ReverseScan(positionsAdapter.getItems(), matrix, code);
                savePosition = reverseDirectPosition;
            } else {
                DirectScan(positionsAdapter.getItems(), matrix, code);
                savePosition = reverseDirectPosition;
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Некорректный штрихкод!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
    }


    private void DirectScan(List<Position> posList, DataMatrix matrix, String code) {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Неверный штрихкод!", Toast.LENGTH_SHORT);

        int start_count = scannedPositions;

        for (int i = 0; i < posList.size(); i++) {
            if (positionsAdapter.getItems().get(i).getSgTin().equals(matrix.SGTIN()) || positionsAdapter.getItems().get(i).getSgTin().equals(matrix.SSCC())) {
                scannedPositions++;
                if (matrix.SGTIN() == null) {
                    positionsAdapter.removeItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
                } else {
                    positionsAdapter.removeItem(matrix.SGTIN());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code, 1));
                }
                if (positionsAdapter.getItemCount() == 0) {
                    showSaveDialog(getString(R.string.scan_finish));
                }
            }
        }

        if (start_count == scannedPositions) {
            toast.show();
        }

        updateScannedPositions();
    }

    private void ReverseScan(List<Position> posList, DataMatrix matrix, String code) {
        if (posList.size() > 0) {
            if (checkPosition(posList, matrix)) {
                if (matrix.SGTIN() == null) {
                    positionsAdapter.addItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "",1));
                } else {
                    checkEanSgtin(matrix);
                    positionsAdapter.addItem(matrix.SGTIN());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code,1));
                }
                scannedPositions++;
            } else {
                return;
            }
        } else {
            if (matrix.SSCC() == null) {
                checkEanSgtin(matrix);
                positionsAdapter.addItem(matrix.SGTIN());
                reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code,1));
            } else {
                positionsAdapter.addItem(matrix.SSCC());
                reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "",1 ));
            }
            if (positionsAdapter.getItemCount() == totalPositions) {
                showSaveDialog(getString(R.string.scan_finish));
            }
            scannedPositions++;
        }
        updateScannedPositions();
    }

    private boolean checkPosition(List<Position> posList, DataMatrix matrix) {
        Boolean check = false;
        for (Position s : posList) {
            if (s.getSgTin().equals(matrix.SGTIN()) || s.getSgTin().equals(matrix.SSCC())) {
                check = false;
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Штрих-код уже был просканирован!", Toast.LENGTH_SHORT);
                toast.show();
                break;
            } else {
                check = true;
            }
        }
        return check;
    }

    private void checkEanSgtin(DataMatrix matrix) {
        if (Ean != null) {
            if (!Ean.trim().isEmpty() && Ean.length() == 13 && isNumeric(Ean) && Ean.equals(matrix.EAN())) {
                return;
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "EAN-13 не совпадает!", Toast.LENGTH_LONG);
                toast.show();
                errorSgtinEanDialog("Удалить просканированную позицию?", matrix.SGTIN());
            }
        }
    }

    private void updateScannedPositions() {
        totalPositionsTextView.setText("(" + scannedPositions + "/" + totalPositions + ")");
    }

    private List<String> getPositions(List<Position> positions) {
        List<String> res = new ArrayList<>();
        if (TTN_TYPE.equals("1")) {
            for (Position position : positionsAdapter.getItems()) {
                res.add(position.getSgTin());
            }
        } else {
            positions.removeAll(positionsAdapter.getItems());
            for (Position position : positions) {
                res.add(position.getSgTin());
            }
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
                savePosition)
                .observe(this, this);
    }

    private void errorSgtinEanDialog(String message, String sgtin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            positionsAdapter.removeItem(sgtin);
            scannedPositions--;
            updateScannedPositions();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
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