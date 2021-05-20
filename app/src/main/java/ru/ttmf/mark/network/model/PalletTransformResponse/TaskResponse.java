package ru.ttmf.mark.network.model.PalletTransformResponse;

import com.google.gson.annotations.SerializedName;

import ru.ttmf.mark.network.model.BaseResponse;

public class TaskResponse extends BaseResponse {
    @SerializedName("Data")
    public String Data;
}
