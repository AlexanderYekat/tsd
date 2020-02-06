package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SaveConsumptionPositionsData {

    @SerializedName("token")
    private String token;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("DocName")
    private String docname;

    @SerializedName("Sgtins")
    private List<String> items;


    public SaveConsumptionPositionsData(String token, String userId, String docname, List<String> items) {
        this.token = token;
        this.userId = userId;
        this.docname = docname;
        this.items = items;
    }
}
