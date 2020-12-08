package ru.ttmf.mark.consumption_positions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.media.MediaPlayer;
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
import ru.ttmf.mark.network.model.SgtinInfo;
import ru.ttmf.mark.network.model.SsccInfo;
import ru.ttmf.mark.positions.PositionsSaveModel;
import ru.ttmf.mark.preference.PreferenceController;

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
    private List<PositionsSaveModel> scanPositions;
    private Integer totalCount;
    private Integer scannedCount;
    private Integer startCount;

    @BindView(R.id.positions)
    RecyclerView rvPositions;

    @BindView(R.id.scanned)
    TextView scanned;

    @BindView(R.id.seria)
    TextView seria;

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
        String seriaText = getIntent().getExtras().getString(SERIA);
        String id = getIntent().getExtras().getString(ID);
        totalCount = Math.round(Float.parseFloat(getIntent().getExtras().getString(COUNT)));
        if (getIntent().getExtras() != null)
            viewModel.setDataType((DataType) getIntent().getSerializableExtra(DATA_TYPE));

        viewModel.searchPositions(PreferenceController.getInstance().getToken(), id).observe(this, this);
        scanned.setText(getString(R.string.cipher, cipherText));
        seria.setText(getString(R.string.seria, seriaText));
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
                    case GetSgtinInfo:
                        hideProgressDialog();
                        List<SgtinInfo> sgtinInfoList = (List<SgtinInfo>) response.getObject();
                        //viewModel.setSgtinInfo(sgtinInfoList);
                        if (sgtinInfoList.size() > 0) {
                            showErrorBarcodeInfo("Номер документа: " + sgtinInfoList.get(0).getTtnId() + "\n" +
                                    "Дата документа: " + sgtinInfoList.get(0).getTtnDate() + "\n" +
                                    "Наименование склада: " + sgtinInfoList.get(0).getSkladName() + "\n" +
                                    "PartyId: " + sgtinInfoList.get(0).getParty() + "\n" +
                                    "Шифр: " + sgtinInfoList.get(0).getShifr() + "\n" +
                                    "SGTIN: " + sgtinInfoList.get(0).getSgtin() + "\n" +
                                    "SSCC: " + sgtinInfoList.get(0).getSscc() + "\n" +
                                    "Статус скаинирования: " + sgtinInfoList.get(0).getTsdNaim() + "\n" +
                                    "Статус акцептования: " + sgtinInfoList.get(0).getAcceptNaim() + "\n" +
                                    "Статус товара: " + sgtinInfoList.get(0).getOstNaim() + "\n" +
                                    "Тип акцептования: " + sgtinInfoList.get(0).getMarkAcceptTypeNaim() + "\n");
                        }
                        break;
                    case GetSsccInfo:
                        hideProgressDialog();
                        List<SsccInfo> ssccInfoList = (List<SsccInfo>) response.getObject();
                        if (ssccInfoList.size() > 0) {
                            showErrorBarcodeInfo("Номер документа: " + ssccInfoList.get(0).getUnpDocId() + "\n" +
                                    "SSCC: " + ssccInfoList.get(0).getSscc() + "\n" +
                                    "Дата документа: " + ssccInfoList.get(0).getUnpDate() + "\n" +
                                    "Вид операции: " + ssccInfoList.get(0).getAction() + "\n" +
                                    "Статус операции: " + ssccInfoList.get(0).getRezult() + "\n");
                        }
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

                boolean find = false;

                for (String s : viewModel.getValidPositions()) {
                    if (s.equals(matrix.SGTIN()) || s.equals(matrix.SSCC())) {
                        find = true;
                        break;
                    }
                }

                if (find == true) {
                    Scan(positionsAdapter.getItems(), matrix, code);
                } else {
                    ToastMessage("Штрихкод из другой партии!");
                    //GetSgtinSsccInfo(matrix);
                }

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

        playSound(R.raw.s3);

        return;
    }

    private void playSound(int resId) {
        MediaPlayer mp = MediaPlayer.create(this, resId);
        mp.setVolume(1, 1);

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });
        mp.start();
    }

    private void Scan(List<String> posList, DataMatrix matrix, String code) {
        //если количество отсканированнных штрихкодов меньше необходимого количества, то продолжить сканирование
        int old_scannedCount = scannedCount;

        if (scannedCount < totalCount) {
            //если кол-во отсканированных больше 0, то сохранять только уникальные SGTIN-ы
            if (posList.size() > 0) {
                if (checkPosition(posList, matrix)) {
                    if (matrix.SSCC() == null) {
                        //проверка на ean-code
                        checkEanSgtin(matrix);
                        positionsAdapter.addItem(matrix.SGTIN());
                        viewModel.addPositions(matrix.SGTIN());
                        scanPositions.add(new PositionsSaveModel(matrix.SGTIN(), code, 1));
                        scannedCount++;
                    } else {
                        positionsAdapter.addItem(matrix.SSCC());
                        viewModel.addPositions(matrix.SSCC());
                        scanPositions.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
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
                    scanPositions.add(new PositionsSaveModel(matrix.SGTIN(), code, 1));
                    scannedCount++;
                } else {
                    positionsAdapter.addItem(matrix.SSCC());
                    viewModel.addPositions(matrix.SSCC());
                    scanPositions.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
                    scannedCount++;
                }
            }

            if (old_scannedCount < scannedCount) {
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

                playSound(R.raw.s3);

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
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "EAN-13 не совпадает!", Toast.LENGTH_LONG);
                toast.show();

                playSound(R.raw.s3);

                errorSgtinEanDialog("Удалить просканированную позицию?", matrix.SGTIN(), matrix.EAN());
            }
        } else {
            Ean = matrix.EAN();
        }
    }

    private void errorSgtinEanDialog(String message, String sgtin, String ean) {
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
            Ean = ean;
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

    protected void showErrorBarcodeInfo(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text);
        builder.setTitle(R.string.barcode_info);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }

    //Получение информации по SGTIN-у или SSCC
    private void GetSgtinSsccInfo(DataMatrix matrix) {
        if (matrix.SSCC() == null) {
            viewModel.getSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN(), 1).observe(this, this);
        } else {
            viewModel.getSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC(), 2).observe(this, this);
        }
    }
}
