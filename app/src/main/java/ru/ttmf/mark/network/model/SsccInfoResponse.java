package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SsccInfoResponse extends BaseResponse {

    @SerializedName("Data")
    private List<SsccInfo> ssccInfo;

    public List<SsccInfo> getSsccInfo() {
        return ssccInfo;
    }
}
