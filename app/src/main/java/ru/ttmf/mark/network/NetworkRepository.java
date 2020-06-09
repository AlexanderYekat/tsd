package ru.ttmf.mark.network;

import android.app.AlertDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.text.TextUtils;
import android.util.Log;

import ru.ttmf.mark.common.NetworkStatus;
import ru.ttmf.mark.common.QueryType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.network.model.BaseModel;
import ru.ttmf.mark.network.model.BaseResponse;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.ConsumptionSearchData;
import ru.ttmf.mark.network.model.Invoice;
import ru.ttmf.mark.network.model.LoginModel;
import ru.ttmf.mark.network.model.LoginResponse;
import ru.ttmf.mark.network.model.PositionData;
import ru.ttmf.mark.network.model.PositionsResponse;
import ru.ttmf.mark.network.model.SaveConsumptionPositionsData;
import ru.ttmf.mark.network.model.SavePositionsData;
import ru.ttmf.mark.network.model.SearchData;
import ru.ttmf.mark.network.model.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.ConnectionSpec;
import okhttp3.TlsVersion;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ttmf.mark.positions.ReverseSaveModel;
import ru.ttmf.mark.preference.PreferenceController;

public class NetworkRepository {
    private static NetworkRepository instance;
    private ApiService apiService;
    private ApiService officeApiService;

    public NetworkRepository() {

        String url = PreferenceController.getInstance().getUrl();

        apiService = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient())
                .build()
                .create(ApiService.class);

        officeApiService = new Retrofit.Builder()
                .baseUrl("https://office2.ttmf.ru/KassaWeb/api/ArtisProgramVersionCheck/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient())
                .build()
                .create(ApiService.class);
    }

    public OkHttpClient createHttpClient() {

        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(message -> Log.d("REST", message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .addInterceptor(interceptor)
                .hostnameVerifier((hostname, session) -> true);

        return enableTls(client).build();
    }

    private static OkHttpClient.Builder enableTls(OkHttpClient.Builder client) {
        try {

            final TrustManager[] trustManager = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            String secureProtocol = PreferenceController.getInstance().getSecureProtocol();

            SSLContext sc = SSLContext.getInstance(secureProtocol);
            sc.init(null, trustManager, null);
            client.sslSocketFactory(new TlsSocketFactory(sc.getSocketFactory()), (X509TrustManager) trustManager[0]);

            ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.forJavaName(secureProtocol)).build();

            List<ConnectionSpec> specs = new ArrayList<>();
            specs.add(cs);
            specs.add(ConnectionSpec.COMPATIBLE_TLS);
            specs.add(ConnectionSpec.CLEARTEXT);

            client.connectionSpecs(specs);
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return client;
    }

    public static NetworkRepository getInstance() {
        if (instance == null) {
            synchronized (NetworkRepository.class) {
                if (instance == null) {
                    instance = new NetworkRepository();
                }
            }
        }
        return instance;
    }

    public static void refresh() {
        instance = null;
    }

    public LiveData<Response> version() {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.Version, NetworkStatus.LOADING));
        officeApiService.version(18).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(new Response(response.body(), QueryType.Login, NetworkStatus.SUCCESS));

                    int last_version = 0;
                    String t_obj = response.body();
                    if (t_obj != null) {
                        last_version =  Integer.parseInt(t_obj);
                    }

                    PreferenceController.getInstance().setLastVersion(last_version);
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.Login,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }



    public LiveData<Response> login(String login, String password) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.Login, NetworkStatus.LOADING));
        apiService.login(new BaseModel("LOGIN", new LoginModel(login, password))).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getData(), QueryType.Login, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.Login,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.Login,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.Login,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;

    }


    public LiveData<Response> search(DataType type, String token, String userId, String name) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.Search, NetworkStatus.LOADING));

        String action = "";

        switch (type) {
            case TTN_DIRECT:
                action = "GET_TTN_SHIFRS2";
                break;
            case TTN_REVERSE:
                action = "GET_TTN_SHIFRS3";
                break;
            case PV:
                action = "GET_PV_SHIFRS";
                break;
        }

        apiService.search(new BaseModel(action, new SearchData(type, token, userId, name)))
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            liveData.postValue(new Response(response.body().getInvoiceList(), QueryType.Search, NetworkStatus.SUCCESS));
                        } else {
                            if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                                liveData.postValue(new Response(
                                        QueryType.Search,
                                        NetworkStatus.ERROR,
                                        response.body().getErrorText()));
                            } else {
                                liveData.postValue(new Response(
                                        QueryType.Search,
                                        NetworkStatus.ERROR,
                                        ""));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call, Throwable t) {
                        liveData.postValue(new Response(
                                QueryType.Search,
                                NetworkStatus.ERROR,
                                "INTERNET ERROR"));
                    }
                });
        return liveData;

    }

    public LiveData<Response> getPositions(DataType type, String token, String cipher) {
        String action = "";
        switch (type) {
            case TTN_DIRECT:
                action = "GET_SHIFR_SGTINS2";
                break;
            case TTN_REVERSE:
                action = "GET_SHIFR_SGTINS3";
                break;
        }

        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetPositions, NetworkStatus.LOADING));
        apiService.getPositions(new BaseModel(action, new PositionData(token, cipher)))
                .enqueue(new Callback<PositionsResponse>() {
                    @Override
                    public void onResponse(Call<PositionsResponse> call, retrofit2.Response<PositionsResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            liveData.postValue(new Response(response.body().getPositions(), QueryType.GetPositions, NetworkStatus.SUCCESS));
                        } else {
                            if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                                liveData.postValue(new Response(
                                        QueryType.GetPositions,
                                        NetworkStatus.ERROR,
                                        response.body().getErrorText()));
                            } else {
                                liveData.postValue(new Response(
                                        QueryType.GetPositions,
                                        NetworkStatus.ERROR,
                                        ""));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<PositionsResponse> call, Throwable t) {
                        liveData.postValue(new Response(
                                QueryType.GetPositions,
                                NetworkStatus.ERROR,
                                "INTERNET ERROR"));
                    }
                });
        return liveData;

    }


    public LiveData<Response> savePositions(DataType type,
                                            String token,
                                            String userId,
                                            String itemId,
                                            String name,
                                            Object positions) {

        String action = "";

        switch (type) {
            case TTN_DIRECT:
                action = "SET_SGTIN2";
                break;
            case TTN_REVERSE:
                action = "SET_SGTIN3";
                break;
            case PV:
                action = "SET_PV_SGTINS";
                break;
        }

        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.SavePositions, NetworkStatus.LOADING));
        apiService.savePositions(new BaseModel(action, new SavePositionsData(type, token, userId, itemId, name, positions))).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                if (response.isSuccessful() && response.isSuccessful()) {
                    liveData.postValue(new Response(response.body(), QueryType.SavePositions, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.SavePositions,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.SavePositions,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.SavePositions,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;

    }

    public LiveData<Response> getConsumptionPositions(String token, String cipher) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetConsumptionPositions, NetworkStatus.LOADING));
        apiService.getConsumptionPositions(new BaseModel("GET_PV_SHIFR_SGTINS", new ConsumptionSearchData(token, cipher)))
                .enqueue(new Callback<ConsumptionResponse>() {
                    @Override
                    public void onResponse(Call<ConsumptionResponse> call, retrofit2.Response<ConsumptionResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            liveData.postValue(new Response(response.body().getData(), QueryType.GetConsumptionPositions, NetworkStatus.SUCCESS));
                        } else {
                            if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                                liveData.postValue(new Response(
                                        QueryType.GetConsumptionPositions,
                                        NetworkStatus.ERROR,
                                        response.body().getErrorText()));
                            } else {
                                liveData.postValue(new Response(
                                        QueryType.GetConsumptionPositions,
                                        NetworkStatus.ERROR,
                                        ""));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ConsumptionResponse> call, Throwable t) {
                        liveData.postValue(new Response(
                                QueryType.GetConsumptionPositions,
                                NetworkStatus.ERROR,
                                "INTERNET ERROR"));
                    }
                });
        return liveData;

    }

    public LiveData<Response> saveConsumptionPositions(String token, String userId, String docName, List<String> positions) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.SaveConsumptionPositions, NetworkStatus.LOADING));
        apiService.saveConsumptionPositions(new BaseModel("SET_DOC_SGTINS",
                new SaveConsumptionPositionsData(token, userId, docName, positions)))
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, retrofit2.Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            liveData.postValue(new Response(response.body(), QueryType.SaveConsumptionPositions, NetworkStatus.SUCCESS));
                        } else {
                            if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                                liveData.postValue(new Response(
                                        QueryType.SaveConsumptionPositions,
                                        NetworkStatus.ERROR,
                                        response.body().getErrorText()));
                            } else {
                                liveData.postValue(new Response(
                                        QueryType.SaveConsumptionPositions,
                                        NetworkStatus.ERROR,
                                        ""));
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        liveData.postValue(new Response(
                                QueryType.SaveConsumptionPositions,
                                NetworkStatus.ERROR,
                                "INTERNET ERROR"));
                    }
                });
        return liveData;

    }
}
