package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class LoginResponse extends BaseResponse{

    @SerializedName("Data")
    private UserData data;

    public Object getData() {
        return data;
    }

}
