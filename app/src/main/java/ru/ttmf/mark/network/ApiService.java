package ru.ttmf.mark.network;

import ru.ttmf.mark.network.model.BaseModel;
import ru.ttmf.mark.network.model.BaseResponse;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.LoginResponse;
import ru.ttmf.mark.network.model.OwnerId.OwnerIDRequest;
import ru.ttmf.mark.network.model.OwnerId.OwnerIDResponse;
import ru.ttmf.mark.network.model.PalletTransformRequest.PalletTransformRequest;
import ru.ttmf.mark.network.model.PalletTransformResponse.TaskResponse;
import ru.ttmf.mark.network.model.PositionsResponse;
import ru.ttmf.mark.network.model.CisRequest.Request;
import ru.ttmf.mark.network.model.CisResponse.ResponseData;
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

    @POST(".")
    Call<ResponseData> getCisInfo(@Body Request cisInfo);

    @POST(".")
    Call<TaskResponse> SaveTaskTransformation(@Body PalletTransformRequest TaskData);

    @POST(".")
    Call<OwnerIDResponse> GetOwnerID(@Body OwnerIDRequest ownerIDInfo);
}
