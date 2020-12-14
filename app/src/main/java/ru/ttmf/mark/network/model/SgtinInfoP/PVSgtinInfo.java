package ru.ttmf.mark.network.model.SgtinInfoP;

import com.google.gson.annotations.SerializedName;

public class PVSgtinInfo {

    @SerializedName("pvId")
    private Long pvId;

    @SerializedName("pvDate")
    private String pvDate;

    @SerializedName("sName")
    private String sName;

    @SerializedName("pvNom")
    private String pvNom;

    @SerializedName("Shifr")
    private String Shifr;

    @SerializedName("sgtin")
    private String sgtin;

    @SerializedName("sscc")
    private String sscc;

    @SerializedName("tsd")
    private Byte tsd;

    @SerializedName("tsdNaim")
    private String tsdNaim;

    @SerializedName("agnName")
    private String agnName;

    @SerializedName("agnAbbr")
    private String agnAbbr;



    public Long getPvId() { return pvId; }
    public String getPvDate() { return pvDate; }
    public String getSName() { return sName; }
    public String getPvNom() { return pvNom; }
    public String getShifr() { return Shifr; }
    public String getSgtin() { return sgtin; }
    public String getSscc() { return sscc; }
    public String getTsdNaim() { return tsdNaim; }
    public String getAgnName() { return agnName; }
    public String getAgnAbbr() { return agnAbbr; }

}
