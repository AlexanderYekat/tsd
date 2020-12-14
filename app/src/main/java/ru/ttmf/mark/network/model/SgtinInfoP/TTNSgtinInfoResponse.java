package ru.ttmf.mark.network.model.SgtinInfoP;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.network.model.BaseResponse;

public class TTNSgtinInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<TTNSgtinInfo> sgtinInfo;

    public List<TTNSgtinInfo> getSgtinInfo() {
        return sgtinInfo;
    }
}
