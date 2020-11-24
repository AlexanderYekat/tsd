package ru.ttmf.mark.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class SgtinInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<SgtinInfo> sgtinInfo;

    public List<SgtinInfo> getSgtinInfo() {
        return sgtinInfo;
    }
}
