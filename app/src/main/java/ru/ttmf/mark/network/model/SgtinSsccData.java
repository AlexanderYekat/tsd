package ru.ttmf.mark.network.model;


import com.google.gson.annotations.SerializedName;

public class SgtinSsccData {
    @SerializedName("Token")
    private String token;

    @SerializedName("Sgtin")
    private String sgtin;

    @SerializedName("Sscc")
    private String sscc;

    @SerializedName("OperationType")
    private Integer type;

    public SgtinSsccData(String token, String sgtin, String sscc, Integer type) {
        this.token = token;
        this.sgtin = sgtin;
        this.sscc = sscc;
        this.type = type;
    }
}
