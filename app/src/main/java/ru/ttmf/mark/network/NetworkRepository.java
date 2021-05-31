package ru.ttmf.mark.network;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.Route;
import ru.ttmf.mark.DeviceInfo.DeviceInfoModel;
import ru.ttmf.mark.common.NetworkStatus;
import ru.ttmf.mark.common.QueryType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.network.model.BaseModel;
import ru.ttmf.mark.network.model.BaseResponse;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.ConsumptionSearchData;
import ru.ttmf.mark.network.model.LoginModel;
import ru.ttmf.mark.network.model.LoginResponse;
import ru.ttmf.mark.network.model.OwnerId.OwnerIDRequest;
import ru.ttmf.mark.network.model.OwnerId.OwnerIDResponse;
import ru.ttmf.mark.network.model.TaskTransformationRequest.TaskRequest;
import ru.ttmf.mark.network.model.TaskTransformationResponse.TaskResponse;
import ru.ttmf.mark.network.model.PositionData;
import ru.ttmf.mark.network.model.PositionsResponse;
import ru.ttmf.mark.network.model.CisRequest.Request;
import ru.ttmf.mark.network.model.CisRequest.requestdata;
import ru.ttmf.mark.network.model.CisResponse.ResponseData;
import ru.ttmf.mark.network.model.SaveConsumptionPositionsData;
import ru.ttmf.mark.network.model.SavePositionsData;
import ru.ttmf.mark.network.model.SearchData;
import ru.ttmf.mark.network.model.SearchResponse;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import ru.ttmf.mark.network.model.SgtinInfoP.PVSgtinInfoResponse;
import ru.ttmf.mark.network.model.SgtinInfoP.TTNSgtinInfoResponse;
import ru.ttmf.mark.network.model.SgtinInfoP.UNPSgtinInfoResponse;
import ru.ttmf.mark.network.model.SgtinSsccData;
import ru.ttmf.mark.network.model.SsccInfoP.PVSsccInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoP.TTNSsccInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoP.UNPSsccInfoResponse;
import ru.ttmf.mark.preference.PreferenceController;

public class NetworkRepository {
    private static NetworkRepository instance;
    private static Context context;
    private ApiService apiService;
    private OkHttpClient client;
    private ApiService officeApiService;
    private ApiService apiServiceMarkMilk;
    private ApiService apiServiceMarkTsd;

    public NetworkRepository() {

        String url = PreferenceController.getInstance().getUrl();

        client = createHttpClient();

        apiService = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService.class);

        officeApiService = new Retrofit.Builder()
                .baseUrl("https://office2.ttmf.ru/KassaWeb/api/ArtisProgramVersionCheck/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient())
                .build()
                .create(ApiService.class);
        apiServiceMarkMilk = new Retrofit.Builder()
                .baseUrl("https://office2.ttmf.ru/perfume/api/MarkMilk/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient())
                .build()
                .create(ApiService.class);
        apiServiceMarkTsd = new Retrofit.Builder()
                .baseUrl("https://etpzakaz.ru/sfarm/ws/api/markTsd/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(createHttpClient())
                .build()
                .create(ApiService.class);
    }

    public OkHttpClient createHttpClient() {

        OkHttpClient.Builder client = null;

        HttpLoggingInterceptor interceptor =
                new HttpLoggingInterceptor(message -> Log.d("REST", message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        try {
            Boolean proxySettingsState = PreferenceController.getInstance().getProxySettingsState();

            //если заданы настройки прокси, то выставляем их в параметры запроса
            if (proxySettingsState) {

                String proxyHost = PreferenceController.getInstance().getProxyAddress();
                int proxyPort = Integer.parseInt(PreferenceController.getInstance().getProxyPort());
                final String username = PreferenceController.getInstance().getProxyLogin();
                final String password = PreferenceController.getInstance().getPassword();

                InetSocketAddress proxyAddr = InetSocketAddress.createUnresolved(proxyHost, proxyPort);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddr);

                Authenticator proxyAuthenticator = new Authenticator() {
                    @Override
                    public okhttp3.Request authenticate(Route route, okhttp3.Response response) throws IOException {
                        String credential = Credentials.basic(username, password);
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credential)
                                .build();
                    }
                };

                client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(500, TimeUnit.SECONDS)
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .addInterceptor(interceptor)
                        .hostnameVerifier((hostname, session) -> true)
                        .proxy(proxy)
                        .proxyAuthenticator(proxyAuthenticator);

                //иначе запрос без настроек
            } else {

                client = new OkHttpClient.Builder()
                        .connectTimeout(100, TimeUnit.SECONDS)
                        .readTimeout(500, TimeUnit.SECONDS)
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .addInterceptor(interceptor)
                        .hostnameVerifier((hostname, session) -> true);
            }

            return enableTls(client).build();
        } catch (
                Exception ex) {
            ex.getMessage();
            return null;
        }

    }

    public static String[] getProxyDetails(Context context) {
        String[] proxyAddress = new String[2];
        try {
            proxyAddress[0] = System.getProperty("http.proxyHost");
            proxyAddress[1] = ":" + System.getProperty("http.proxyPort");

            if (proxyAddress[0] != null && proxyAddress[1] != null) {
                return proxyAddress;
            } else {
                return null;
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
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

    public static NetworkRepository getInstance(Context _context) {
        if (instance == null) {
            synchronized (NetworkRepository.class) {
                if (instance == null) {
                    instance = new NetworkRepository();
                    context = _context;
                }
            }
        }
        return instance;
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
                    liveData.postValue(new Response(response.body(), QueryType.Version, NetworkStatus.SUCCESS));

                    int last_version = 0;
                    String t_obj = response.body();
                    if (t_obj != null) {
                        last_version = Integer.parseInt(t_obj);
                    }
                    PreferenceController.getInstance().setLastVersion(last_version);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.Version,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> send_device_info(String cur_serial, Integer cur_version, String login) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.Device_info, NetworkStatus.LOADING));
        apiService.send_device_info(new BaseModel("TSD_DEVICE_INFO", new DeviceInfoModel(cur_serial, cur_version, login))).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, retrofit2.Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getData(), QueryType.Device_info, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.Device_info,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.Device_info,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.Device_info,
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
                        NetworkStatus.ERROR, "INTERNET ERROR"));
                        /*"Ошибка: " + t.getMessage() + "\n" +
                                "URL: " + call.request().url() + "\n" +
                                "METHOD: " + call.request().method() + "\n" +
                                "BODY: " + call.request().toString() + "\n"));/* +
                                "PROXY: " + client.proxy().address()));*/
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
                                t.getMessage()));
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

    public LiveData<Response> getTTNSgtinInfo(String token, String sgtin) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetTTNSgtinInfo, NetworkStatus.LOADING));
        apiService.getTTNSgtinInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, sgtin, null, 1))).enqueue(new Callback<TTNSgtinInfoResponse>() {
            @Override
            public void onResponse(Call<TTNSgtinInfoResponse> call, retrofit2.Response<TTNSgtinInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSgtinInfo(), QueryType.GetTTNSgtinInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetTTNSgtinInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetTTNSgtinInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<TTNSgtinInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetTTNSgtinInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getTTNSsccInfo(String token, String sscc) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetTTNSsccInfo, NetworkStatus.LOADING));
        apiService.getTTNSsccInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, null, sscc, 1))).enqueue(new Callback<TTNSsccInfoResponse>() {
            @Override
            public void onResponse(Call<TTNSsccInfoResponse> call, retrofit2.Response<TTNSsccInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSsccInfo(), QueryType.GetTTNSsccInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetTTNSsccInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetTTNSsccInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<TTNSsccInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetTTNSsccInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getUNPSgtinInfo(String token, String sgtin) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetUNPSgtinInfo, NetworkStatus.LOADING));
        apiService.getUNPSgtinInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, sgtin, null, 2))).enqueue(new Callback<UNPSgtinInfoResponse>() {
            @Override
            public void onResponse(Call<UNPSgtinInfoResponse> call, retrofit2.Response<UNPSgtinInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSgtinInfo(), QueryType.GetUNPSgtinInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetUNPSgtinInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetUNPSgtinInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<UNPSgtinInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetUNPSgtinInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getUNPSsccInfo(String token, String sscc) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetUNPSsccInfo, NetworkStatus.LOADING));
        apiService.getUNPSsccInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, null, sscc, 2))).enqueue(new Callback<UNPSsccInfoResponse>() {
            @Override
            public void onResponse(Call<UNPSsccInfoResponse> call, retrofit2.Response<UNPSsccInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSsccInfo(), QueryType.GetUNPSsccInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetUNPSsccInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetUNPSsccInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<UNPSsccInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetUNPSsccInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getPVSgtinInfo(String token, String sgtin) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetPVSgtinInfo, NetworkStatus.LOADING));
        apiService.getPVSgtinInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, sgtin, null, 3))).enqueue(new Callback<PVSgtinInfoResponse>() {
            @Override
            public void onResponse(Call<PVSgtinInfoResponse> call, retrofit2.Response<PVSgtinInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSgtinInfo(), QueryType.GetPVSgtinInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetPVSgtinInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetPVSgtinInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<PVSgtinInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetPVSgtinInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getPVSsccInfo(String token, String sscc) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetPVSsccInfo, NetworkStatus.LOADING));
        apiService.getPVSsccInfo(new BaseModel("GetSgtinSsccInfo", new SgtinSsccData(token, null, sscc, 3))).enqueue(new Callback<PVSsccInfoResponse>() {
            @Override
            public void onResponse(Call<PVSsccInfoResponse> call, retrofit2.Response<PVSsccInfoResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getSsccInfo(), QueryType.GetPVSsccInfo, NetworkStatus.SUCCESS));
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) {
                        liveData.postValue(new Response(
                                QueryType.GetPVSsccInfo,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else {
                        liveData.postValue(new Response(
                                QueryType.GetPVSsccInfo,
                                NetworkStatus.ERROR,
                                ""));
                    }
                }
            }

            @Override
            public void onFailure(Call<PVSsccInfoResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetPVSsccInfo,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> getCisInfo(requestdata request) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetCisesInfoList, NetworkStatus.LOADING));
        apiServiceMarkMilk.getCisInfo(new Request("GetCisesInfoList", request)).enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, retrofit2.Response<ResponseData> response) {
                if (response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().getData(), QueryType.GetCisesInfoList, NetworkStatus.SUCCESS)); // Если запрос выполнен успешно
                } else {
                    if (response.body() != null && !TextUtils.isEmpty(response.body().getErrorText())) { // Если запрос выполнен некорректно, ответ не пустой и содержит ошибку
                        liveData.postValue(new Response(
                                QueryType.GetCisesInfoList,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    } else if (response.body() != null && TextUtils.isEmpty(response.body().getErrorText())){ // Если запрос не выполнен корректно, ответ непустой, но не содержит ошибку (ошибку содержит CisData)
                        liveData.postValue(new Response(response.body().getData(),
                                QueryType.GetCisesInfoList,
                                NetworkStatus.SUCCESS));
                    }
                    else {
                        liveData.postValue(new Response(
                                QueryType.GetCisesInfoList,
                                NetworkStatus.ERROR,
                                response.body().CisData.get(0).errorMessage));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetCisesInfoList,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }
    public LiveData<Response> SaveTaskTransformation(TaskRequest request) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.SaveTaskTransformation, NetworkStatus.LOADING));
        apiServiceMarkTsd.SaveTaskTransformation(request).enqueue(new Callback<TaskResponse>() {
            @Override
            public void onResponse(Call<TaskResponse> call, retrofit2.Response<TaskResponse> response) {
                if (response.body().isSuccess()) {
                    liveData.postValue(new Response(response.body().Data, QueryType.SaveTaskTransformation, NetworkStatus.SUCCESS));
                }
                else {
                        liveData.postValue(new Response(
                                QueryType.SaveTaskTransformation,
                                NetworkStatus.ERROR,
                                response.body().getErrorText()));
                    }
                }
            @Override
            public void onFailure(Call<TaskResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.SaveTaskTransformation,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }

    public LiveData<Response> GetOwnerID(OwnerIDRequest request) {
        MutableLiveData<Response> liveData = new MutableLiveData<>();
        liveData.postValue(new Response(QueryType.GetOwnerID, NetworkStatus.LOADING));
        apiServiceMarkTsd.GetOwnerID(request).enqueue(new Callback<OwnerIDResponse>() {
            @Override
            public void onResponse(Call<OwnerIDResponse> call, retrofit2.Response<OwnerIDResponse> response) {
                if (response.isSuccessful()) {
                    liveData.postValue(new Response(response.body(), QueryType.GetOwnerID, NetworkStatus.SUCCESS));
                }
                else {
                    liveData.postValue(new Response(
                            QueryType.GetOwnerID,
                            NetworkStatus.ERROR,
                            response.body().getErrorText()));
                }
            }
            @Override
            public void onFailure(Call<OwnerIDResponse> call, Throwable t) {
                liveData.postValue(new Response(
                        QueryType.GetOwnerID,
                        NetworkStatus.ERROR,
                        "INTERNET ERROR"));
            }
        });
        return liveData;
    }
}
