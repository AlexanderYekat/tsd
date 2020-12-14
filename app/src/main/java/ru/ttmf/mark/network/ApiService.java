package ru.ttmf.mark.network;

import ru.ttmf.mark.network.model.BaseModel;
import ru.ttmf.mark.network.model.BaseResponse;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.LoginResponse;
import ru.ttmf.mark.network.model.PositionsResponse;
import ru.ttmf.mark.network.model.SearchResponse;
import ru.ttmf.mark.network.model.SgtinInfoP.PVSgtinInfoResponse;
import ru.ttmf.mark.network.model.SgtinInfoP.TTNSgtinInfoResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import ru.ttmf.mark.network.model.SgtinInfoP.UNPSgtinInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoP.PVSsccInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoP.TTNSsccInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoP.UNPSsccInfoResponse;
import ru.ttmf.mark.network.model.SsccInfoResponse;

public interface ApiService {

    @POST(".")
    Call<LoginResponse> login(@Body BaseModel login);

    @POST(".")
    Call<SearchResponse> search(@Body BaseModel search);

    @POST(".")
    Call<PositionsResponse> getPositions(@Body BaseModel positions);

    @POST(".")
    Call<BaseResponse> savePositions(@Body BaseModel positions);

    @POST(".")
    Call<ConsumptionResponse> getConsumptionPositions(@Body BaseModel positions);

    @POST(".")
    Call<BaseResponse> saveConsumptionPositions(@Body BaseModel positions);

    @POST(".")
    Call<String> version(@Body int version);

    @POST(".")
    Call<LoginResponse> send_device_info(@Body BaseModel device_info);

    @POST(".")
    Call<TTNSgtinInfoResponse> getTTNSgtinInfo(@Body BaseModel sgtinInfo);

    @POST(".")
    Call<TTNSsccInfoResponse> getTTNSsccInfo(@Body BaseModel ssccInfo);

    @POST(".")
    Call<UNPSgtinInfoResponse> getUNPSgtinInfo(@Body BaseModel sgtinInfo);

    @POST(".")
    Call<UNPSsccInfoResponse> getUNPSsccInfo(@Body BaseModel ssccInfo);

    @POST(".")
    Call<PVSgtinInfoResponse> getPVSgtinInfo(@Body BaseModel sgtinInfo);

    @POST(".")
    Call<PVSsccInfoResponse> getPVSsccInfo(@Body BaseModel ssccInfo);
}
