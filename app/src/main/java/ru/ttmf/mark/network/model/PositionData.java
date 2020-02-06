package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class PositionData {

    @SerializedName("token")
    private String token;

    @SerializedName("shifrId")
    private String cipherId;

    public PositionData(String token, String cipherId) {
        this.token = token;
        this.cipherId = cipherId;
    }
}
