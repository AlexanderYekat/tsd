package ru.ttmf.mark.network.model.SsccInfoP;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.network.model.BaseResponse;

public class UNPSsccInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<UNPSsccInfo> ssccInfo;

    public List<UNPSsccInfo> getSsccInfo() {
        return ssccInfo;
    }
}
