package ru.ttmf.mark.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class SgtinInfo {
    @SerializedName("ttnId")
    private Long ttnId;

    @SerializedName("ttnDate")
    private String ttnDate;

    @SerializedName("skladName")
    private String skladName;

    @SerializedName("party")
    private String party;

    @SerializedName("shifr")
    private String shifr;

    @SerializedName("sgtin")
    private String sgtin;

    @SerializedName("sscc")
    private String sscc;

    @SerializedName("tsdNaim")
    private String tsdNaim;

    @SerializedName("acceptNaim")
    private String acceptNaim;

    @SerializedName("ostNaim")
    private String ostNaim;

    @SerializedName("markAcceptTypeNaim")
    private String markAcceptTypeNaim;

    public String getMarkAcceptTypeNaim() {
        return markAcceptTypeNaim;
    }

    public void setMarkAcceptTypeNaim(String markAcceptTypeNaim) {
        this.markAcceptTypeNaim = markAcceptTypeNaim;
    }

    public String getOstNaim() {
        return ostNaim;
    }

    public void setOstNaim(String ostNaim) {
        this.ostNaim = ostNaim;
    }

    public String getAcceptNaim() {
        return acceptNaim;
    }

    public void setAcceptNaim(String acceptNaim) {
        this.acceptNaim = acceptNaim;
    }

    public String getTsdNaim() {
        return tsdNaim;
    }

    public void setTsdNaim(String tsdNaim) {
        this.tsdNaim = tsdNaim;
    }

    public String getSgtin() {
        return sgtin;
    }

    public void setSgtin(String sgtin) {
        this.sgtin = sgtin;
    }

    public String getSscc() { return sscc; }

    public void setSscc(String sscc) { this.sscc = sscc; }

    public String getShifr() {
        return shifr;
    }

    public void setShifr(String shifr) {
        this.shifr = shifr;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getTtnDate() {
        return ttnDate;
    }

    public void setTtnDate(String ttnDate) {
        this.ttnDate = ttnDate;
    }

    public String getSkladName() {
        return skladName;
    }

    public void setSkladName(String skladName) {
        this.skladName = skladName;
    }

    public Long getTtnId() {
        return ttnId;
    }

    public void setTtnId(Long ttnId) {
        this.ttnId = ttnId;
    }
}
