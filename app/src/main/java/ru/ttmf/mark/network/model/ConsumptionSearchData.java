package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class ConsumptionSearchData {

    @SerializedName("token")
    private String token;

    @SerializedName("pvs_id")
    private String pvsId;

    public ConsumptionSearchData(String token, String pvsId) {
        this.token = token;
        this.pvsId = pvsId;
    }
}
