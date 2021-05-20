package ru.ttmf.mark.Verification;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;


public class VerificationActivity extends ScanActivity {

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    private AlertDialog alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.verification_name));
        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }
        });
        registerReceiver(intentBarcodeDataReceiver, intentFilter);

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

    public void onDecodeFail() {
    Toast toast = Toast.makeText(getApplicationContext(),"Код не отсканирован",Toast.LENGTH_LONG);
    toast.show();
    }

    public void onDecodeComplete(int symbology, int length, String barcode) {
        successfulScan(barcode);
    }

    private void successfulScan(String code) {
        DataMatrix matrix = new DataMatrix();
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);
            if (matrix.SSCC() != null) {
                showAlertDialog(R.layout.scan_success_alert_dialog, "Проверка SSCC пройдена успешно");
            }
            else if(matrix.SGTIN()!= null)
            {
                showAlertDialog(R.layout.scan_success_alert_dialog, "Проверка SGTIN пройдена успешно");
            }
            else {
                showAlertDialog(R.layout.scan_fail_alert_dialog,"Проверка кода не пройдена");
            }
        } catch (Exception ex) {
            showAlertDialog(R.layout.scan_fail_alert_dialog,"Проверка кода не пройдена");
            return;
        }
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
    private void showAlertDialog (@LayoutRes int alertlayout, String dialogText)
    {
        if (alert!=null && alert.isShowing())
        {
            alert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LinearLayout view = (LinearLayout) getLayoutInflater()
                .inflate(alertlayout,null);
        builder.setView(view);
        alert = builder.create();
        TextView textview = (TextView) view.findViewById(R.id.txt_dia);
        textview.setText(dialogText);
        alert = builder.create();
        alert.show();
    }

}
