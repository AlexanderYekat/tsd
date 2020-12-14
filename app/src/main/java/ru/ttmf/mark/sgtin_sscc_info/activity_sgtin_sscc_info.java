package ru.ttmf.mark.sgtin_sscc_info;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.RippleDrawable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.SgtinInfoP.PVSgtinInfo;
import ru.ttmf.mark.network.model.SgtinInfoP.TTNSgtinInfo;
import ru.ttmf.mark.network.model.SgtinInfoP.UNPSgtinInfo;
import ru.ttmf.mark.network.model.SsccInfo;
import ru.ttmf.mark.network.model.SsccInfoP.PVSsccInfo;
import ru.ttmf.mark.network.model.SsccInfoP.TTNSsccInfo;
import ru.ttmf.mark.network.model.SsccInfoP.UNPSsccInfo;
import ru.ttmf.mark.positions.PositionsAdapter;
import ru.ttmf.mark.positions.PositionsViewModel;
import ru.ttmf.mark.preference.PreferenceController;
import android.widget.TextView;
import ru.ttmf.mark.R;
import ru.ttmf.mark.scan_sscc.Sscc_item;

public class activity_sgtin_sscc_info extends ScanActivity implements Observer<Response>{

    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    //@BindView(R.id.textView2)
    //TextView textView2;

    //@BindView(R.id.scrollView)
    //ScrollView scrollView;

    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    private activity_sgtin_sscc_info_view_model viewModel;
    private ProgressDialog progressDialog;
    private TextView textView2;
    private ScrollView scroll;
    private LinearLayout linlay1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgtin_sscc_info);

        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.sscc_sgtin_info_txt));

        scroll = (ScrollView) this.findViewById(R.id.scrollView);

        viewModel = ViewModelProviders.of(this)
                .get(activity_sgtin_sscc_info_view_model.class);

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

    public void onDecodeComplete(int symbology, int length, String barcode) {
        successfulScan(barcode);
    }

    private void successfulScan(String code)
    {
        //textView2.setText("");
        DataMatrix matrix = new DataMatrix();
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);
            if (matrix.SGTIN() != null) {
                //sgtin
                viewModel.getTTNSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN()).observe(this, this);
                viewModel.getUNPSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN()).observe(this, this);
                viewModel.getPVSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN()).observe(this, this);
            }
             else {
                //sscc
                viewModel.getTTNSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC()).observe(this, this);
                viewModel.getUNPSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC()).observe(this, this);
                viewModel.getPVSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC()).observe(this, this);
            }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),"Произошла ошибка!", Toast.LENGTH_SHORT);
            toast.show();
            return;
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
                    case GetTTNSgtinInfo:
                        hideProgressDialog();
                        List<TTNSgtinInfo> ttnSgtinInfoList = (List<TTNSgtinInfo>) response.getObject();
                        if (ttnSgtinInfoList.size() > 0) {
                            linlay1 = new LinearLayout(this);
                            linlay1.setOrientation(LinearLayout.VERTICAL);
                            linlay1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

                            textView2 = new TextView(this);
                            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            textView2.setText("TEST."); // + "\n" + "\n" + "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum." + "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum."+ "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum."+ "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum."+ "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum."+ "Lorem Ipsum is simply dummy text of the printing and typesetting industry...like Aldus PageMaker including versions of Lorem Ipsum.");
                            //textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            textView2.setTextSize(20);
                            textView2.setTextColor(Color.BLACK);
                            linlay1.addView(textView2);
                            scroll.addView(linlay1);
                            //setContentView(scroll);
                        }
                        break;
                    case GetTTNSsccInfo:
                        hideProgressDialog();
                        List<TTNSsccInfo> ttnSsccInfoList = (List<TTNSsccInfo>) response.getObject();
                        if (ttnSsccInfoList.size() > 0) {
                            //
                        }
                        break;
                    case GetUNPSgtinInfo:
                        hideProgressDialog();
                        List<UNPSgtinInfo> unpSgtinInfoList = (List<UNPSgtinInfo>) response.getObject();
                        if (unpSgtinInfoList.size() > 0) {
                            //
                        }
                        break;
                    case GetUNPSsccInfo:
                        hideProgressDialog();
                        List<UNPSsccInfo> unpSsccInfoList = (List<UNPSsccInfo>) response.getObject();
                        if (unpSsccInfoList.size() > 0) {
                            //
                        }
                        break;
                    case GetPVSgtinInfo:
                        hideProgressDialog();
                        List<PVSgtinInfo> pvSgtinInfoList = (List<PVSgtinInfo>) response.getObject();
                        if (pvSgtinInfoList.size() > 0) {
                            //
                        }
                        break;
                    case GetPVSsccInfo:
                        hideProgressDialog();
                        List<PVSsccInfo> pvSsccInfoList = (List<PVSsccInfo>) response.getObject();
                        if (pvSsccInfoList.size() > 0) {
                            //
                        }
                        break;
                }
                break;
        }
    }

    protected void showProgressDialog() {
        //progressDialog = new ProgressDialog(this);
        //progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
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
