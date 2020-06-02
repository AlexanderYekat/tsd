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
import android.util.Base64;
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
import ru.ttmf.mark.positions.ReverseSaveModel;
import ru.ttmf.mark.preference.PreferenceController;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static ru.ttmf.mark.Helpers.Helpers.isNumeric;
import static ru.ttmf.mark.invoice.InvoiceFragment.*;

public class ConsumptionPositionsActivity extends ScanActivity implements Observer<Response> {
    public static final String CODE = "CODE";
    private ConsumptionPositionsViewModel viewModel;
    private ProgressDialog progressDialog;
    private String Ean;
    private List<ReverseSaveModel> scanPositions;
    private Integer totalCount;
    private Integer scannedCount;
    private Integer startCount;

    @BindView(R.id.positions)
    RecyclerView rvPositions;

    @BindView(R.id.scanned)
    TextView scanned;

    TextView totalConsumptionPositionsTextView;
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
        Ean = getIntent().getExtras().getString(EAN13);
        String cipherText = getIntent().getExtras().getString(CIPHER);
        String id = getIntent().getExtras().getString(ID);
        totalCount = Math.round(Float.parseFloat(getIntent().getExtras().getString(COUNT)));
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
        totalConsumptionPositionsTextView = (TextView) findViewById(R.id.consumptionPositions);
        scanPositions = new ArrayList<>();
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
                        scannedCount = data.getPositions().size();
                        startCount = scannedCount;
                        totalConsumptionPositionsTextView.setText("(" + scannedCount + "/" + totalCount + ")");
                        initPositionsRecycler(data.getPositions());
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
        try {
            //преобразование штрихкода в SGTIN или SCCC
            DataMatrixHelpers.splitStr(matrix, code, 29);

            //////////////BASE 64 QRCODE//////////////////////
            //byte[] codeBytes = code.getBytes(Charset.forName("UTF-8"));
            //byte[] code64 = android.util.Base64.encode(codeBytes, Base64.DEFAULT);
            //String code64String = new String(code64);

            //если преобразование успешное, то продолжить
            if (matrix.SGTIN() != null || matrix.SSCC() != null) {
                Scan(positionsAdapter.getItems(), matrix, code);
            } else {
                ToastMessage("Некорректный штрихкод!");
            }
        } catch (Exception ex) {
            ToastMessage("Некорректный штрихкод!");
        }

    }

    private void ToastMessage(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.show();
        return;
    }

    private void Scan(List<String> posList, DataMatrix matrix, String code) {
        //если отсканированнные штрихкоды меньше всего штрихкодов, то продолжить сканирование
        if (scannedCount != totalCount) {
            //если кол-во отсканированных больше 0, то сохранять только уникальные SGTIN-ы
            if (posList.size() > 0) {
                if (checkPosition(posList, matrix)) {
                    if (matrix.SSCC() == null) {
                        //проверка на ean-code
                        checkEanSgtin(matrix);
                        positionsAdapter.addItem(matrix.SGTIN());
                        viewModel.addPositions(matrix.SGTIN());
                        scanPositions.add(new ReverseSaveModel(matrix.SGTIN(), code));
                        scannedCount++;
                    } else {
                        positionsAdapter.addItem(matrix.SSCC());
                        viewModel.addPositions(matrix.SSCC());
                        scanPositions.add(new ReverseSaveModel(matrix.SSCC(), ""));
                        scannedCount++;
                    }
                } else {
                    return;
                }
            } else {
                if (matrix.SSCC() == null) {
                    //проверка на ean-code
                    checkEanSgtin(matrix);
                    positionsAdapter.addItem(matrix.SGTIN());
                    viewModel.addPositions(matrix.SGTIN());
                    scanPositions.add(new ReverseSaveModel(matrix.SGTIN(), code));
                    scannedCount++;
                } else {
                    positionsAdapter.addItem(matrix.SSCC());
                    viewModel.addPositions(matrix.SSCC());
                    scanPositions.add(new ReverseSaveModel(matrix.SSCC(), ""));
                    scannedCount++;
                }
                if (scannedCount == totalCount) {
                    showSaveDialog(getString(R.string.scan_finish));
                }
            }
            updateScannedPositions();
        } else {
            return;
        }
    }


    private boolean checkPosition(List<String> posList, DataMatrix matrix) {
        Boolean check = false;
        for (String s : posList) {
            if (s.equals(matrix.SGTIN()) || s.equals(matrix.SSCC())) {
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
        if (!Ean.trim().isEmpty() && Ean.length() == 13 && isNumeric(Ean)) {
            if (Ean.equals(matrix.EAN())) {
                return;
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "EAN-13 не совпадает!", Toast.LENGTH_LONG);
                toast.show();
                errorSgtinEanDialog("Удалить просканированную позицию?", matrix.SGTIN());
            }
        }
    }

    private void errorSgtinEanDialog(String message, String sgtin) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            positionsAdapter.removeItem(sgtin);
            scannedCount--;
            updateScannedPositions();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }


    private void updateScannedPositions() {
        totalConsumptionPositionsTextView.setText("(" + scannedCount + "/" + totalCount + ")");
    }

    private List<String> getPositions(List<Position> positions) {
        List<String> res = new ArrayList<>();
        for (Position position : positions) {
            res.add(position.getSgTin());
        }
        return res;
    }

    public void onBackPressed() {
        if (scannedCount > startCount) {
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
                scanPositions)
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
