package ru.ttmf.mark.network.model.SsccInfoP;

import com.google.gson.annotations.SerializedName;

public class PVSsccInfo {

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

    @SerializedName("agnName")
    private String agnName;

    @SerializedName("agnAbbr")
    private String agnAbbr;

    @SerializedName("count")
    private String count;

    @SerializedName("naim")
    private String naim;



    public Long getPvId() { return pvId; }
    public String getPvDate() { return pvDate; }
    public String getSName() { return sName; }
    public String getPvNom() { return pvNom; }
    public String getShifr() { return Shifr; }
    public String getAgnName() { return agnName; }
    public String getAgnAbbr() { return agnAbbr; }
    public String getCount() { return count; }
    public String getNaim() { return naim; }
}
