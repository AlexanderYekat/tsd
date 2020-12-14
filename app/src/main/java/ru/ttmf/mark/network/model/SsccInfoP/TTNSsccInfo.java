package ru.ttmf.mark.network.model.SsccInfoP;

import com.google.gson.annotations.SerializedName;


public class TTNSsccInfo {
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

    @SerializedName("count")
    private String count;

    @SerializedName("naim")
    private String naim;

    @SerializedName("markAcceptTypeNaim")
    private String markAcceptTypeNaim;


    public String getMarkAcceptTypeNaim() {
        return markAcceptTypeNaim;
    }
    public String getShifr() { return shifr; }
    public String getCount() { return count; }
    public String getNaim() { return naim; }
    public String getParty() { return party; }
    public String getTtnDate() { return ttnDate; }
    public String getSkladName() { return skladName; }
    public Long getTtnId() { return ttnId; }

}
