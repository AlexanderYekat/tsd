package ru.ttmf.mark.network;

import ru.ttmf.mark.network.model.BaseModel;
import ru.ttmf.mark.network.model.BaseResponse;
import ru.ttmf.mark.network.model.ConsumptionResponse;
import ru.ttmf.mark.network.model.LoginResponse;
import ru.ttmf.mark.network.model.PositionsResponse;
import ru.ttmf.mark.network.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

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
}
