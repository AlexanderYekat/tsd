package ru.ttmf.mark.scan;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.ttmf.mark.R;
import ru.ttmf.mark.ScanActivity;
import ru.ttmf.mark.common.DataMatrix;
import ru.ttmf.mark.common.DataMatrixHelpers;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.CisRequest.requestdata;
import ru.ttmf.mark.network.model.CisResponse.CisData;
import ru.ttmf.mark.network.model.SgtinInfoP.TTNSgtinInfo;
import ru.ttmf.mark.network.model.SsccInfo;
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

    private int current_sgtin_number;
    private scan_activity_model viewModel;
    private String groupType = "milk";
    private boolean isMarkirovka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        initToolbar(toolbar2, getString(R.string.scan_name));
        viewModel = ViewModelProviders.of(this)
                .get(scan_activity_model.class);

        InitializeReceiver();
        registerReceiver(intentBarcodeDataReceiver, intentFilter);
        isMarkirovka = PreferenceController.getInstance().isMarkirovkaSelected();
        current_sgtin_number = 0;
        if (PreferenceController.getInstance().CisesInfoList.size() > 0)
        {
            textView_count.setText("1/" + PreferenceController.getInstance().CisesInfoList.size());
            initTabLayout(0);
            current_sgtin_number = 1;
        }

        if (!isMarkirovka) {
            btn_clear.setVisibility(View.GONE);
            btn_left.setVisibility(View.GONE);
            btn_right.setVisibility(View.GONE);
            textView_count.setVisibility(View.GONE);
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

            if (PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.GetChild()!=null) {
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

            if (PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.GetPrVetDocument()!=null) {
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
            if (isMarkirovka) {
                for (CisData var : PreferenceController.getInstance().CisesInfoList) {
                    if (var.cisInfo.GetCis().equals(matrix.getSGTIN()) || var.cisInfo.GetCis().equals(matrix.getSSCC())) // Проверка, был ли код отсканирован ранее
                    {
                        if (var.errorMessage != null) {
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
            }

            if (!find) {
                progressBar.setVisibility(ProgressBar.VISIBLE);
                ArrayList<String> request = new ArrayList<>();
                //if (code.startsWith("04") && code.length()==18) {
                    //viewModel.getTTNSsccInfo(PreferenceController.getInstance().getToken(), code).observe(this, this);
                    //return;
                //}
                //else
                    if (matrix.SSCC()!=null) {
                    if (!isMarkirovka) {
                        progressBar.setVisibility(ProgressBar.GONE);
                        //viewModel.getTTNSsccInfo(PreferenceController.getInstance().getToken(), matrix.SSCC()).observe(this, this);
                        //TODO: что нибудь показать
                        return;
                    }
                    request.add(matrix.getSSCC());
                    viewModel.getCisInfo(new requestdata(PreferenceController.getInstance().getOwnerId(), matrix.getSSCC())).observe(this,this);
                    //TODO: заменить после добавления эцп
                    /*viewModel.GetCises(
                            request,
                            groupType,
                            getSharedPreferences("preferences", MODE_PRIVATE).
                                    getString("token", "")).
                            observe(this,this);

                     */
                }
                else if (matrix.SGTIN()!=null){
                    if (!isMarkirovka) {
                        viewModel.getTTNSgtinInfo(PreferenceController.getInstance().getToken(), matrix.SGTIN()).observe(this, this);
                        return;
                    }
                    request.add(matrix.getSGTIN());
                    viewModel.getCisInfo(new requestdata(PreferenceController.getInstance().getOwnerId(), matrix.getSGTIN())).observe(this,this);
                    //TODO: заменить после добавления эцп
                    /*viewModel.GetCises(
                            request,
                            groupType,
                            getSharedPreferences("preferences", MODE_PRIVATE).
                                    getString("token", "")).
                            observe(this,this);

                     */
                }
                else {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast toast2 = Toast.makeText(getApplicationContext(),"Некорректный штрихкод!", Toast.LENGTH_SHORT);
                    toast2.show();
                    return;
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
                    case GetTTNSgtinInfo:
                        progressBar.setVisibility(ProgressBar.GONE);
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
                                    "Статус сканирования: " + sgtinInfoList.get(0).getTsdNaim() + "\n" +
                                    "Статус акцептования: " + sgtinInfoList.get(0).getAcceptNaim() + "\n" +
                                    "Статус товара: " + sgtinInfoList.get(0).getOstNaim() + "\n" +
                                    "Тип акцептования: " + sgtinInfoList.get(0).getMarkAcceptTypeNaim() + "\n");
                        }
                        break;
                    case GetTTNSsccInfo:
                        progressBar.setVisibility(ProgressBar.GONE);
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
            //TODO: убрать после добавления эцп
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
                    showErrorDialog("Используйте протокол безопасности TLS 1.2!");  // Ошибка при неправильном ownerId
                    break;
                }
                String errorText = response.getError().trim();
                if (errorText == null || errorText.length() == 0) {
                    errorText = "Сервер не доступен, обратитесь в тех. поддержку";
                }
                //showErrorDialog(response.getError());
                showErrorDialog(errorText);
                break;
        }
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

    private void refresh() // Заново отправляет запрос для кодов, по которым пришла неполная информация
    {
        requestdata request = new requestdata((PreferenceController.getInstance().getOwnerId()));
        for (CisData data:PreferenceController.getInstance().CisesInfoList)
        {
            if(data.errorCode!=null)
            {
                request.add(data.cisInfo.GetCis());
            }
        }
        viewModel.getCisInfo(request).observe(this,this);
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

}
