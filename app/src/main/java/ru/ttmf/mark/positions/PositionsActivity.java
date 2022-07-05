package ru.ttmf.mark.positions;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
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
import ru.ttmf.mark.network.model.DeviceSavePosition;
import ru.ttmf.mark.network.model.Position;
import ru.ttmf.mark.network.model.SgtinInfoP.TTNSgtinInfo;
import ru.ttmf.mark.network.model.SsccInfo;
import ru.ttmf.mark.preference.PreferenceController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

import static ru.ttmf.mark.Helpers.Helpers.ToJson;
import static ru.ttmf.mark.Helpers.Helpers.isNumeric;
import static ru.ttmf.mark.Helpers.Helpers.writeToFile;
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
    private Integer countBarcodes = 0;

    private Integer count;
    private Integer scan_count;


    private UUID scanSessionId;
    private DeviceSavePosition deviceSavePosition;

    @BindView(R.id.positions)
    RecyclerView rvPositions;

    @BindView(R.id.cipher)
    TextView cipher;

    @BindView(R.id.seria)
    TextView seria;

    PositionsAdapter positionsAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    TextView totalPositionsTextView;
    TextView totalCountTextView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_positions);
        ButterKnife.bind(this);
        initToolbar(toolbar, getString(R.string.positions));
        viewModel = ViewModelProviders.of(this)
                .get(PositionsViewModel.class);
        String cipherText = getIntent().getExtras().getString(CIPHER);
        String seriaText = getIntent().getExtras().getString(SERIA);
        String id = getIntent().getExtras().getString(ID);
        Ean = getIntent().getExtras().getString(EAN13);
        count = Math.round(Float.parseFloat(getIntent().getExtras().getString(COUNT)));
        scan_count = Math.round(Float.parseFloat(getIntent().getExtras().getString(SCAN_COUNT)));
        //totalPositions = Math.round(Float.parseFloat(getIntent().getExtras().getString(COUNT)));
        reverseDirectPosition = new ArrayList<PositionsSaveModel>();
        if (getIntent().getExtras() != null)
            viewModel.setDataType((DataType) getIntent().getSerializableExtra(DATA_TYPE));

        TTN_TYPE = getIntent().getExtras().getString("TTN_TYPE");
        viewModel.loadPositions(PreferenceController.getInstance().getToken(), id).observe(this, this);
        cipher.setText(getString(R.string.cipher, cipherText));
        seria.setText(getString(R.string.seria, seriaText));
        InitializeReceiver();
        registerReceiver(intentBarcodeDataReceiver, intentFilter);
        totalPositionsTextView = (TextView) findViewById(R.id.totalPositions);
        totalCountTextView = findViewById(R.id.totalCount);

        initScanSession(id);

        /*//Всплывающее окно с вводом количества сканируемых штрихкодов///
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите количество:");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        countBarcodes = 1;
        input.setText(countBarcodes.toString());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                countBarcodes =  Integer.parseInt(input.getText().toString());
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
                String errorText = response.getError().trim();
                if (errorText == null || errorText.length() == 0) {
                    errorText = "Сервер не доступен, обратитесь в тех. поддержку";
                }
                //showErrorDialog(response.getError());
                showErrorDialog(errorText);
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
                        updateScannedPositions();
                        initPositionsRecycler(positionsList);
                        break;
                    case SavePositions:
                        hideProgressDialog();
                        finish();
                        break;
                    case GetTTNSgtinInfo:
                        hideProgressDialog();
                        List<TTNSgtinInfo> sgtinInfoList = (List<TTNSgtinInfo>) response.getObject();
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
                    case GetTTNSsccInfo:
                        hideProgressDialog();
                        List<SsccInfo> ssccInfoList = (List<SsccInfo>) response.getObject();
                        if (ssccInfoList.size() > 0) {
                            showErrorBarcodeInfo("Номер документа: " + ssccInfoList.get(0).getUnpDocId() + "\n" +
                                    "SSCC: " + ssccInfoList.get(0).getSscc() + "\n" +
                                    "Дата документа: " + ssccInfoList.get(0).getUnpDate() + "\n" +
                                    "Вид операции: " + ssccInfoList.get(0).getAction() + "\n" +
                                    "Статус операции: " + ssccInfoList.get(0).getRezult() + "\n");
                            break;
                        }
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

    public void initScanSession(String ttnsId) {
        scanSessionId = UUID.randomUUID();

        deviceSavePosition = new DeviceSavePosition();
        deviceSavePosition.setUuid(scanSessionId);
        deviceSavePosition.setTtnsId(Long.parseLong(ttnsId));
        deviceSavePosition.setSaved(false);

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        deviceSavePosition.setScanDate(formatter.format(date));
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

            //сохранение отсканированных позиций на устройстве (перезапись json)
            localSaveScanPositions(reverseDirectPosition, false);
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Некорректный штрихкод!", Toast.LENGTH_SHORT);
            toast.show();

            playSound(R.raw.s3);

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
                    int index = -1;
                    for (Position item : posList) {
                        if (item.getSgTin().equals(matrix.SSCC())) {
                            index = posList.indexOf(item);
                        }
                    }
                    String quant = posList.get(index).getQuant();
                    if (!quant.equals("")) {
                        if ((scan_count + Integer.parseInt(quant)) > count) {
                            toast.show();
                            scannedPositions--;
                            return;
                        }
                        scan_count += Integer.parseInt(quant);
                    }
                    positionsAdapter.removeItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
                } else {
                    int index = -1;
                    for (Position item : posList) {
                        if (item.getSgTin().equals(matrix.SGTIN())) {
                            index = posList.indexOf(item);
                        }
                    }
                    String quant = posList.get(index).getQuant();
                    if (!quant.equals("")) {
                        if ((scan_count + Integer.parseInt(quant)) > count) {
                            toast.show();
                            scannedPositions--;
                            return;
                        }
                        scan_count += Integer.parseInt(quant);
                    }
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
            playSound(R.raw.s3);
            //GetSgtinSsccInfo(matrix);
        }

        updateScannedPositions();
    }
    /*
    private void DirectScan(List<Position> posList, DataMatrix matrix, String code) {

        Toast toast = Toast.makeText(getApplicationContext(),
                "Неверный штрихкод!", Toast.LENGTH_SHORT);

        int start_count = scannedPositions;
        String sgtin;
        for (int i = 0; i < posList.size(); i++) {
            if (positionsAdapter.getItems().get(i).getSgTin().length()!=27) {
                sgtin = DataMatrixHelpers.replaceGS(code);
            }
            else {
                sgtin = matrix.SGTIN();
            }
            if (positionsAdapter.getItems().get(i).getSgTin().equals(sgtin) || positionsAdapter.getItems().get(i).getSgTin().equals(matrix.SSCC())) {
                scannedPositions++;
                if (matrix.SGTIN() == null) {
                    positionsAdapter.removeItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
                } else {
                    positionsAdapter.removeItem(sgtin);
                    //reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code, 1));
                    reverseDirectPosition.add(new PositionsSaveModel(DataMatrixHelpers.removeGSandTail(code), code, 1));
                }
                if (positionsAdapter.getItemCount() == 0) {
                    showSaveDialog(getString(R.string.scan_finish));
                }
            }
        }

        if (start_count == scannedPositions) {
            toast.show();
            //playSound(R.raw.s3);
            //GetSgtinSsccInfo(matrix);
        }

        updateScannedPositions();
    }

     */

    private void ReverseScan(List<Position> posList, DataMatrix matrix, String code) {
        if (posList.size() > 0) {
            if (checkPosition(posList, matrix)) {
                if (matrix.SGTIN() == null) {
                    positionsAdapter.addItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "", 1));
                } else {
                    checkEanSgtin(matrix);

                    countBarcodes = 1;
                    //Всплывающее окно с вводом количества сканируемых штрихкодов///
                    /*builder = new AlertDialog.Builder(this);
                    builder.setTitle("Введите количество:");

                    final EditText input = new EditText(this);

                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    input.setText(countBarcodes.toString());
                    builder.setView(input);

                    builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            countBarcodes =  Integer.parseInt(input.getText().toString());
                        }
                    });

                    builder.show();*/
                    ///////////////////////////////////////////////////////////////

                    positionsAdapter.addItem(matrix.SGTIN());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code, countBarcodes));
                }
                scannedPositions++;
            } else {
                return;
            }
        } else {
            if (matrix.SSCC() == null) {
                if (checkEanSgtin(matrix) == false) {
                    countBarcodes = 1;
                    //Всплывающее окно с вводом количества сканируемых штрихкодов///
                /*builder = new AlertDialog.Builder(this);
                builder.setTitle("Введите количество:");

                final EditText input = new EditText(this);

                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                input.setText(countBarcodes.toString());
                builder.setView(input);

                builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        countBarcodes =  Integer.parseInt(input.getText().toString());
                    }
                });

                builder.show();*/
                    ///////////////////////////////////////////////////////////////

                    if (matrix.SGTIN().length() == 27) {
                        positionsAdapter.addItem(matrix.SGTIN());
                        reverseDirectPosition.add(new PositionsSaveModel(matrix.SGTIN(), code, 1));
                    }
                }
            } else {
                if (matrix.SSCC().length() == 18) {
                    positionsAdapter.addItem(matrix.SSCC());
                    reverseDirectPosition.add(new PositionsSaveModel(matrix.SSCC(), "", countBarcodes));
                }
            }
            if (positionsAdapter.getItemCount() == totalPositions) {
                showSaveDialog(getString(R.string.scan_finish));
            }
            scannedPositions++;
        }
        updateScannedPositions();
    }

    /**
     * @param posList      Список отсканированных штрихкодов
     * @param dbSaveStatus Статус сохранения штрихкодов в БД (true проставляется только в методе save())
     */
    private void localSaveScanPositions(List<PositionsSaveModel> posList, boolean dbSaveStatus) {

        if (dbSaveStatus) {
            deviceSavePosition.setSaved(true);
        }

        List<String> newList = new ArrayList<>();
        //Преобразование List<Object> в List<String>
        for (PositionsSaveModel pos :
                posList) {
            newList.add(pos.Sgtin);
        }

        deviceSavePosition.setSgtinSscc(newList);
        if (TTN_TYPE.equals("1")) {
            writeToFile("Обратный приход (ttnsId: " + deviceSavePosition.getTtnsId() + ") " + deviceSavePosition.getScanDate() + ".txt", ToJson(deviceSavePosition));
        } else {
            writeToFile("Прямой приход (ttnsId: " + deviceSavePosition.getTtnsId() + ") " + deviceSavePosition.getScanDate() + ".txt", ToJson(deviceSavePosition));
        }
    }

    private boolean checkPosition(List<Position> posList, DataMatrix matrix) {
        Boolean check = false;
        for (Position s : posList) {
            if (s.getSgTin().equals(matrix.SGTIN()) || s.getSgTin().equals(matrix.SSCC())) {
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

    private boolean checkEanSgtin(DataMatrix matrix) {
        if (Ean != null) {
            if (!Ean.trim().isEmpty() && Ean.length() == 13 && isNumeric(Ean) && Ean.equals(matrix.EAN())) {
                return false;
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "EAN-13 не совпадает!", Toast.LENGTH_LONG);
                toast.show();
                playSound(R.raw.s3);
                AtomicReference<Boolean> rez;
                rez = errorSgtinEanDialog("Удалить просканированную позицию?", matrix.SGTIN(), matrix.EAN());

                return rez.get();
            }
        }
        return false;
    }

    private void updateScannedPositions() {
        if (TTN_TYPE.equals("1")) {
            totalPositionsTextView.setText("(" + scannedPositions + "/" + totalPositions + ")");
        }
        else {
            totalCountTextView.setText("(" + scannedPositions + "/" + totalPositions + ")");
            totalPositionsTextView.setText("(" + scan_count + "/" + count + " уп)");
        }
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


        //если удалось сохранить отсканированные позиции, то статус сохранения в бд в локальноном файле - true
        localSaveScanPositions(reverseDirectPosition, true);
    }

    private AtomicReference<Boolean> errorSgtinEanDialog(String message, String sgtin, String ean) {
        AtomicReference<Boolean> rez = new AtomicReference<>(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            rez.set(true);
            dialog.dismiss();
            positionsAdapter.removeItem(sgtin);
            scannedPositions--;
            updateScannedPositions();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            rez.set(false);
            dialog.dismiss();
            Ean = ean;
        });
        builder.show();

        return rez;
    }

    private void showSaveDialog(String message) {
        if (!TTN_TYPE.equals("1")) {
            if (scan_count > count) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Одна или несколько транспортных упаковок не принадлежат данной позиции!");
                builder.setTitle(R.string.error);
                builder.setPositiveButton(R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                });
                builder.show();
                return;
            }
        }
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
            viewModel.getTTNSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN()).observe(this, this);
        } else {
            viewModel.getTTNSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC()).observe(this, this);
        }
    }
}