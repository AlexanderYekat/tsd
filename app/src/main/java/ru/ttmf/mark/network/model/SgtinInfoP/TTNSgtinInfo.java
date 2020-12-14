package ru.ttmf.mark.network.model.SgtinInfoP;

import com.google.gson.annotations.SerializedName;

public class TTNSgtinInfo {
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

    @SerializedName("tsd")
    private Byte tsd;

    @SerializedName("tsdNaim")
    private String tsdNaim;

    @SerializedName("accept")
    private Byte accept;

    @SerializedName("acceptNaim")
    private String acceptNaim;

    @SerializedName("ost")
    private Byte ost;

    @SerializedName("ostNaim")
    private String ostNaim;

    @SerializedName("markAcceptType")
    private Byte markAcceptType;

    @SerializedName("markAcceptTypeNaim")
    private String markAcceptTypeNaim;


    public String getMarkAcceptTypeNaim() {
        return markAcceptTypeNaim;
    }
    public String getOstNaim() { return ostNaim; }
    public String getAcceptNaim() { return acceptNaim; }
    public String getTsdNaim() { return tsdNaim; }
    public String getSgtin() { return sgtin; }
    public String getSscc() { return sscc; }
    public String getShifr() { return shifr; }
    public String getParty() { return party; }
    public String getTtnDate() { return ttnDate; }
    public String getSkladName() { return skladName; }
    public Long getTtnId() { return ttnId; }


    /*public void setMarkAcceptTypeNaim(String markAcceptTypeNaim) {
        this.markAcceptTypeNaim = markAcceptTypeNaim;
    }*/

    /*public void setOstNaim(String ostNaim) {
        this.ostNaim = ostNaim;
    }*/

    /*public void setAcceptNaim(String acceptNaim) {
        this.acceptNaim = acceptNaim;
    }*/

    /*public void setTsdNaim(String tsdNaim) {
        this.tsdNaim = tsdNaim;
    }*/

    /*public void setSgtin(String sgtin) {
        this.sgtin = sgtin;
    }*/

    //public void setSscc(String sscc) { this.sscc = sscc; }

    /*public void setShifr(String shifr) {
        this.shifr = shifr;
    }*/

    /*public void setParty(String party) {
        this.party = party;
    }*/

    /*public void setTtnDate(String ttnDate) {
        this.ttnDate = ttnDate;
    }*/

    /*public void setSkladName(String skladName) {
        this.skladName = skladName;
    }*/

    /*public void setTtnId(Long ttnId) {
        this.ttnId = ttnId;
    }*/
}
