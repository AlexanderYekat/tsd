package ru.ttmf.mark.scan;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.CisRequest.requestdata;
import ru.ttmf.mark.network.model.CisResponse.CisData;
import ru.ttmf.mark.preference.PreferenceController;

public class scan_activity extends ScanActivity implements Observer<Response> {
    @BindView(R.id.toolbar2)
    Toolbar toolbar2;

    @BindView(R.id.textView_count)
    TextView textView_count;

    @BindView(R.id.btn_clear)
    Button btn_clear;

    @BindView(R.id.btn_left)
    Button btn_left;

    @BindView(R.id.btn_right)
    Button btn_right;

    @BindView(R.id.ll_1_1)
    LinearLayout ll_1_1;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.sliding_tabs)
    TabLayout tabLayout;

    @BindView(R.id.viewpager)
    ViewPager viewPager;


    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    private int current_sgtin_number;
    private scan_activity_model viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.scan_name));
        viewModel = ViewModelProviders.of(this)
                .get(scan_activity_model.class);
        IntentFilter intentFilter = new IntentFilter("DATA_SCAN");
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver(new OnDecodeCompleteListener() {
            @Override
            public void onDecodeCompleted(int type, int length, String barcode) {
                onDecodeComplete(type, length, barcode);
            }
        });

        registerReceiver(intentBarcodeDataReceiver, intentFilter);

        current_sgtin_number = 0;
        if (PreferenceController.getInstance().CisesInfoList.size() > 0)
        {
            textView_count.setText("1/" + PreferenceController.getInstance().CisesInfoList.size());
            initTabLayout(0);
            current_sgtin_number = 1;
        }
    }

    public void initTabLayout(int position) {
        tabLayout.setupWithViewPager(viewPager);
        scan_viewpager_adapter adapter = new scan_viewpager_adapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        scan_viewpage_fragment_basic_info fragment_basic_info = new scan_viewpage_fragment_basic_info();
        fragment_basic_info.setArguments(bundle);
        adapter.addFragment(fragment_basic_info, "Основная");


        if(PreferenceController.getInstance().CisesInfoList.get(position).errorMessage==null) {

            if (PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.child!=null) {
                scan_viewpage_fragment_child fragment_child_info = new scan_viewpage_fragment_child();
                fragment_child_info.setArguments(bundle);
                adapter.addFragment(fragment_child_info, "Содержимое");
            }

            scan_viewpage_fragment_emission_info fragment_emission_info = new scan_viewpage_fragment_emission_info();
            fragment_emission_info.setArguments(bundle);
            adapter.addFragment(fragment_emission_info, "Эмиссия");

            scan_viewpage_fragment_owner_info fragment_owner_info = new scan_viewpage_fragment_owner_info();
            fragment_owner_info.setArguments(bundle);
            adapter.addFragment(fragment_owner_info, "Владелец");

            if (PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.prVetDocument!=null) {
                scan_viewpage_fragment_prVet_info fragment_prVet_info = new scan_viewpage_fragment_prVet_info();
                fragment_prVet_info.setArguments(bundle);
                adapter.addFragment(fragment_prVet_info, "Вет. документ");
            }
        }

        viewPager.setAdapter(adapter);
        viewPager.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.VISIBLE);
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
        DataMatrix matrix = new DataMatrix();
        try {
            DataMatrixHelpers.splitStr(matrix, code, 29);
            Toast toast;

                    boolean find = false;
                    for (CisData var : PreferenceController.getInstance().CisesInfoList)
                    {
                        if (var.cisInfo.cis.equals(matrix.getSGTIN()) || var.cisInfo.cis.equals(matrix.getSSCC())) // Проверка, был ли код отсканирован ранее
                        {
                            if(var.errorMessage!=null)
                            {
                                PreferenceController.getInstance().CisesInfoList.remove(var); // Если код уже был отсканирован, но не корректно
                                // (напр. не была выполнена авторизация - информация пришла неполная) то он удаляется, чтобы отправить запрос еще раз
                                break;
                            }
                            find = true;
                            toast = Toast.makeText(getApplicationContext(), "Штрихкод уже был отсканирован!", Toast.LENGTH_LONG); // Если код ранее был корректно отсканирован, запрос не отправляется
                            toast.show();
                            break;
                        }
                    }

                    if (!find) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                        if (matrix.SSCC()!=null)
                        {
                            viewModel.getCisInfo(new requestdata(PreferenceController.getInstance().getOwnerId(), matrix.getSSCC())).observe(this,this);
                        }
                        else if (matrix.SGTIN()!=null){
                            viewModel.getCisInfo(new requestdata(PreferenceController.getInstance().getOwnerId(), matrix.getSGTIN())).observe(this,this);
                        }
                    }
        } catch (Exception ex) {
            Toast toast = Toast.makeText(getApplicationContext(),"Некорректный штрихкод!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
    }

    @Override
    public void onChanged(@Nullable Response response)
    {
        switch (response.getStatus()) {
            case SUCCESS:
                switch (response.getType()) {
                    case GetCisesInfoList:
                        ArrayList<CisData> cisesInfo = (ArrayList<CisData>) response.getObject();
                        for (CisData data : cisesInfo) {
                            PreferenceController.getInstance().CisesInfoList.add(data);
                        }
                        progressBar.setVisibility(ProgressBar.GONE);
                        initTabLayout(PreferenceController.getInstance().CisesInfoList.size() - 1);
                        textView_count.setText(PreferenceController.getInstance().CisesInfoList.size() + "/" + PreferenceController.getInstance().CisesInfoList.size());
                        current_sgtin_number = PreferenceController.getInstance().CisesInfoList.size();
                        break;
                }
                break;
            case ERROR:
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.getError().equals("System.InvalidOperationException: Указан недопустимый URI запроса. " +
                        "URI запроса должен быть абсолютным URI, или необходимо задать BaseAddress.\r\n   " +
                        "в System.Net.Http.HttpClient.PrepareRequestMessage(HttpRequestMessage request)\r\n   " +
                        "в System.Net.Http.HttpClient.SendAsync" +
                        "(HttpRequestMessage request, HttpCompletionOption completionOption, CancellationToken cancellationToken)\r\n   " +
                        "в System.Net.Http.HttpClient.SendAsync(HttpRequestMessage request, CancellationToken cancellationToken)\r\n   " +
                        "в System.Net.Http.HttpClient.PostAsync(Uri requestUri, HttpContent content, CancellationToken cancellationToken)\r\n   " +
                        "в MarkProducts.DAL.Helpers.TrueApiHelper.GetCisesInfoList(Int32 ownerId, IEnumerable`1 cises) " +
                        "в C:\\Projects\\MarkProducts\\MarkProducts\\DAL\\Helpers\\TrueApiHelper.cs:строка 153")) {
                    showErrorDialog("Используйте протокол безопасности TLS 1.2!");  // Ошибка при неверном ownerId
                    break;
                }
                showErrorDialog(response.getError());
                break;
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

    @OnClick(R.id.btn_clear)
    public void onbtn_clearyClick() {
        textView_count.setText("0/0");
        PreferenceController.getInstance().CisesInfoList.clear();
        viewPager.setVisibility(View.INVISIBLE);
        tabLayout.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.btn_left)
    public void onbtn_leftClick() {
        if (current_sgtin_number > 1) {
            current_sgtin_number = current_sgtin_number - 1;
            textView_count.setText(current_sgtin_number + "/" + PreferenceController.getInstance().CisesInfoList.size());
            initTabLayout(current_sgtin_number-1);
        }
    }

    @OnClick(R.id.btn_right)
    public void onbtn_rightClick() {
        if (current_sgtin_number < PreferenceController.getInstance().CisesInfoList.size()) {
            current_sgtin_number = current_sgtin_number + 1;
            textView_count.setText(current_sgtin_number + "/" + PreferenceController.getInstance().CisesInfoList.size());
            initTabLayout(current_sgtin_number-1);
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

    private void refresh() // Заного отправляет запрос для кодов, по которым пришла неполная информация
    {
        requestdata request = new requestdata((PreferenceController.getInstance().getOwnerId()));
        for (CisData data:PreferenceController.getInstance().CisesInfoList)
        {
            if(data.errorCode!=null)
            {
                request.add(data.cisInfo.cis);
            }
        }
        viewModel.getCisInfo(request).observe(this,this);
    }
}
