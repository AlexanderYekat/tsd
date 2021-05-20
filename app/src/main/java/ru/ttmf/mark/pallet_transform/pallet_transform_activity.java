package ru.ttmf.mark.pallet_transform;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.QueryType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.PalletTransformRequest.PalletTransformRequest;
import ru.ttmf.mark.network.model.PalletTransformRequest.OperationType;
import ru.ttmf.mark.network.model.PalletTransformRequest.palletData;
import ru.ttmf.mark.preference.PreferenceController;

public class pallet_transform_activity extends ScanActivity implements Observer<Response> {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    @BindView(R.id.listview)
    ListView listview;

    @BindView(R.id.ssccTitle)
    TextView TextViewSSCCTitle;

    @BindView(R.id.scaned_list)
    TextView scaned_list;

    @BindView(R.id.sscc)
    TextView sscc;

    @BindView(R.id.delete)
    RadioButton deleteButton;

    @BindView(R.id.add)
    RadioButton addButton;

    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    private pallet_transform_activity_model viewModel;
    private String currentSSCC = "";
    private ArrayList<String> SGTIN_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pallet_transform);
        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.pallet_transform_name));
        viewModel = ViewModelProviders.of(this)
                .get(pallet_transform_activity_model.class);
        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }

        });
        addButton.setChecked(true);
        registerReceiver(intentBarcodeDataReceiver, intentFilter);
    }
    public void initToolbar(android.support.v7.widget.Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }
    public void onDecodeComplete(int symbology, int length, String barcode) {
        successfulScan(barcode);
    }

    private void successfulScan(String code)
    {
        DataMatrix matrix = new DataMatrix();
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);
            Toast toast;

            if (matrix.SSCC() != null) {
                /*if (currentSSCC==matrix.SSCC()) {
                    return;
                }*/
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setMessage("Сохранить новый SSCC код?");
                dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentSSCC = matrix.SSCC();
                        Show();
                    }
                });
                dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                if(currentSSCC=="")
                {
                    currentSSCC=matrix.SSCC();
                    Show();
                }
                else {
                    if (currentSSCC.equals(matrix.SSCC())) {
                        toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG);
                        toast.show();
                    }
                    else {
                        dialog.show();
                    }
                }
            } else {
                boolean find = false;
                for (String var : SGTIN_list)
                {
                    if (var.equals(matrix.Cis())) // Проверка, был ли код отсканирован ранее
                    {
                        find = true;
                        toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    }
                }
                if (!find) {
                    SGTIN_list.add(matrix.Cis());
                    Show();
                }
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),"Некорректный штрихкод!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
    }

    private void Show()
    {
        TextViewSSCCTitle.setVisibility(View.VISIBLE);
        scaned_list.setVisibility(View.VISIBLE);
        sscc.setVisibility(View.VISIBLE);
        sscc.setText(currentSSCC);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_pallet_transform, SGTIN_list);
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                unregisterReceiver(intentBarcodeDataReceiver);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_send_changes)
    public void onbtn_send_click()
    {
        ArrayList<String> SSCC_list = new ArrayList<String>();
        SSCC_list.add(currentSSCC);
        if(deleteButton.isChecked())
        {
            viewModel.SaveTaskTransformation(new PalletTransformRequest(OperationType.SaveTaskTransformationRemoving.toString(),
                    new palletData(PreferenceController.getInstance().getOwnerId(), "test", SGTIN_list, SSCC_list))).observe(this,this);
        }
        else if(addButton.isChecked())
        {
            viewModel.SaveTaskTransformation(new PalletTransformRequest(OperationType.SaveTaskTransformationAdding.toString(),
                    new palletData(PreferenceController.getInstance().getOwnerId(), "test", SGTIN_list, SSCC_list))).observe(this,this);
        }
        else
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Выберите пункт Добавить или Удалить", Toast.LENGTH_LONG);
            toast.show();
        }
    }
    private void showAlertDialog (@LayoutRes int alertlayout, String dialogText)
    {
        AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout view = (LinearLayout) getLayoutInflater()
                .inflate(alertlayout,null);
        builder.setView(view);
        TextView textview = (TextView) view.findViewById(R.id.txt_dia);
        textview.setText(dialogText);
        alert = builder.create();
        alert.show();
    }

    @Override
    public void onChanged(@Nullable Response response) {
        if (response.getType().equals(QueryType.SaveTaskTransformation)) {
            switch (response.getStatus()) {
                case SUCCESS:
                    showAlertDialog(R.layout.scan_success_alert_dialog, "Операция выполнена");
                    break;
                case ERROR:
                    showAlertDialog(R.layout.scan_fail_alert_dialog, "Операция не выполнена");
                    break;
            }
        }
    }
}
