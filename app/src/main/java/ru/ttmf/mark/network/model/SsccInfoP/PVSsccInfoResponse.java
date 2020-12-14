package ru.ttmf.mark.network.model.SsccInfoP;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.network.model.BaseResponse;

public class PVSsccInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<PVSsccInfo> ssccInfo;

    public List<PVSsccInfo> getSsccInfo() {
        return ssccInfo;
    }
}
