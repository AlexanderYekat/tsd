package ru.ttmf.mark.task;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import ru.ttmf.mark.network.model.PalletTransformRequest.OperationType;
import ru.ttmf.mark.network.model.PalletTransformRequest.PalletTransformRequest;
import ru.ttmf.mark.network.model.PalletTransformRequest.palletData;
import ru.ttmf.mark.preference.PreferenceController;

public class task_activity extends ScanActivity implements Observer<Response> {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    @BindView(R.id.ssccs_listview)
    ListView ssccs_listview;

    @BindView(R.id.sgtins_listview)
    ListView sgtins_listview;

    @BindView(R.id.ssccTitle)
    TextView ssccTitle;

    @BindView(R.id.scaned_list)
    TextView scaned_list;

    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    private task_activity_model viewModel;
    private ArrayList<String> SSCC_list = new ArrayList<>();
    private ArrayList<String> SGTIN_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.task_name));
        viewModel = ViewModelProviders.of(this)
                .get(task_activity_model.class);
        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }

        });
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
                boolean find = false;
                for (String var : SSCC_list)
                {
                    if (var.equals(matrix.getSSCC())) // Проверка, был ли код отсканирован ранее
                    {
                        find = true;
                        toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG); // Если код ранее был корректно отсканирован, запрос не отправляется
                        toast.show();
                        break;
                    }
                }
                if (!find) {
                    SSCC_list.add(matrix.getSSCC());
                    Show();
                }
            } else {
                boolean find = false;
                for (String var : SGTIN_list)
                {
                    if (var.equals(matrix.Cis())) // Проверка, был ли код отсканирован ранее
                    {
                        find = true;
                        toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG); // Если код ранее был корректно отсканирован, запрос не отправляется
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
        ssccTitle.setVisibility(View.VISIBLE);
        scaned_list.setVisibility(View.VISIBLE);
        ArrayAdapter<String> sscc_adapter = new ArrayAdapter<String>(this, R.layout.list_item_pallet_transform, SSCC_list);
        ArrayAdapter<String> sgtin_adapter = new ArrayAdapter<String>(this, R.layout.list_item_pallet_transform, SGTIN_list);
        ssccs_listview.setAdapter(sscc_adapter);
        sgtins_listview.setAdapter(sgtin_adapter);
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
            viewModel.SaveTaskTransformation(new PalletTransformRequest(OperationType.SaveTsdEntity.toString(),
                    new palletData(PreferenceController.getInstance().getOwnerId(), "test", SGTIN_list, SSCC_list))).observe(this,this);
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
