package ru.ttmf.mark.network.model.SgtinInfoP;

import com.google.gson.annotations.SerializedName;

public class UNPSgtinInfo {
    @SerializedName("unpDocId")
    private Long unpDocId;

    @SerializedName("sscc")
    private String sscc;

    @SerializedName("unpDate")
    private String unpDate;

    @SerializedName("actionId")
    private Byte actionId;

    @SerializedName("action")
    private String action;

    @SerializedName("rezultId")
    private Byte rezultId;

    @SerializedName("rezult")
    private String rezult;


    public Long getUnpDocId() { return unpDocId; }
    public String getSscc() { return sscc; }
    public String getUnpDate() { return unpDate; }
    public Byte getActionId() { return actionId; }
    public String getAction() { return action; }
    public Byte getRezultId() { return rezultId; }
    public String getRezult() { return rezult; }


}
