package ru.ttmf.mark.network.model.SgtinInfoP;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.network.model.BaseResponse;

public class PVSgtinInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<UNPSgtinInfo> sgtinInfo;

    public List<UNPSgtinInfo> getSgtinInfo() {
        return sgtinInfo;
    }
}
