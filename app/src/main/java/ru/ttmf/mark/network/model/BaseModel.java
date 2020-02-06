package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class BaseModel {
    @SerializedName("Action")
    private String action;

    @SerializedName("Data")
    private Object data;

    public BaseModel(String action, Object data){
        this.action = action;
        this.data = data;
    }
}
