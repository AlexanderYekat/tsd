package ru.ttmf.mark.network.model.SsccInfoP;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.network.model.BaseResponse;

public class TTNSsccInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<TTNSsccInfo> ssccInfo;

    public List<TTNSsccInfo> getSsccInfo() {
        return ssccInfo;
    }
}
