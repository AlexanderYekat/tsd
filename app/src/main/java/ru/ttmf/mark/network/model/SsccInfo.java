package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SsccInfo {
    @SerializedName("unpDocId")
    private Long unpDocId;

    @SerializedName("sscc")
    private String sscc;

    @SerializedName("unpDate")
    private String unpDate;

    @SerializedName("actionId")
    private Integer actionId;

    @SerializedName("action")
    private String action;

    @SerializedName("rezultId")
    private Integer rezultId;

    @SerializedName("rezult")
    private String rezult;

    public Long getUnpDocId() {
        return unpDocId;
    }

    public void setUnpDocId(Long unpDocId) {
        this.unpDocId = unpDocId;
    }

    public String getSscc() {
        return sscc;
    }

    public void setSscc(String sscc) {
        this.sscc = sscc;
    }

    public String getUnpDate() {
        return unpDate;
    }

    public void setUnpDate(String unpDate) {
        this.unpDate = unpDate;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Integer getRezultId() {
        return rezultId;
    }

    public void setRezultId(Integer rezultId) {
        this.rezultId = rezultId;
    }

    public String getRezult() {
        return rezult;
    }

    public void setRezult(String rezult) {
        this.rezult = rezult;
    }
}
