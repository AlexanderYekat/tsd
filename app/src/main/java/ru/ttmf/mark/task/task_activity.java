package ru.ttmf.mark.task;

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
import android.widget.AdapterView;
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
import ru.ttmf.mark.network.model.TaskTransformationRequest.OperationType;
import ru.ttmf.mark.network.model.TaskTransformationRequest.TaskRequest;
import ru.ttmf.mark.network.model.TaskTransformationRequest.TaskData;
import ru.ttmf.mark.pallet_transform.pallet_transform_activity;
import ru.ttmf.mark.preference.PreferenceController;

public class task_activity extends ScanActivity implements Observer<Response> {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    @BindView(R.id.ssccs_listview)
    ListView ssccs_listview;

    @BindView(R.id.sgtins_listview)
    ListView sgtins_listview;

    @BindView(R.id.ssccListTitle)
    TextView ssccListTitle;

    @BindView(R.id.sgtinListTitle)
    TextView sgtinListTitle;

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
        InitializeReceiver();
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
            DataMatrixHelpers.splitStr(matrix, code, 29, true);
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
                    if (var.equals(matrix.getSGTIN())) // Проверка, был ли код отсканирован ранее
                    {
                        find = true;
                        toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG); // Если код ранее был корректно отсканирован, запрос не отправляется
                        toast.show();
                        break;
                    }
                }
                if (!find) {
                    SGTIN_list.add(matrix.getSGTIN());
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
        ssccListTitle.setVisibility(View.VISIBLE);
        sgtinListTitle.setVisibility(View.VISIBLE);
        ArrayAdapter<String> sscc_adapter = new ArrayAdapter<String>(this, R.layout.array_adapter_list_item, SSCC_list);
        ArrayAdapter<String> sgtin_adapter = new ArrayAdapter<String>(this, R.layout.array_adapter_list_item, SGTIN_list);
        ssccs_listview.setAdapter(sscc_adapter);
        ssccs_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(task_activity.this);
                dialog.setMessage("Удалить " + ((TextView) view).getText() + " ?");
                dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SSCC_list.remove(position);
                        Show();
                        return;
                    }
                });
                dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                return true;
            }
        });

        sgtins_listview.setAdapter(sgtin_adapter);
        sgtins_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(task_activity.this);
                dialog.setMessage("Удалить "+ ((TextView) view).getText() + " ?");
                dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SGTIN_list.remove(position);
                        Show();
                        return;
                    }
                });
                dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                unregisterReceiver(intentBarcodeDataReceiver);
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_send_changes)
    public void onbtn_send_click()
    {
        if (SGTIN_list.isEmpty() && SSCC_list.isEmpty())
        {
            Toast toast = Toast.makeText(getApplicationContext(),"Нет отсканированных кодов!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
            viewModel.SaveTaskTransformation(new TaskRequest(OperationType.SaveTsdEntity.toString(),
                    new TaskData(PreferenceController.getInstance().getOwnerId(), null, SGTIN_list, SSCC_list))).observe(this,this);
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
