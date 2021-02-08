package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DeviceSavePosition {

    @SerializedName("scanguid")
    private UUID uuid;

    @SerializedName("ttnsId")
    private long ttnsId;

    @SerializedName("pvsId")
    private long pvsId;

    @SerializedName("sgtinSscc")
    private List<String> sgtinSscc;

    @SerializedName("scanDate")
    private String scanDate;

    @SerializedName("saved")
    private Boolean saved;


    public String getScanDate() {
        return scanDate;
    }

    public void setScanDate(String scanDate) {
        this.scanDate = scanDate;
    }

    public Boolean getSaved() {
        return saved;
    }

    public void setSaved(Boolean saved) {
        this.saved = saved;
    }

    public List<String> getSgtinSscc() {
        return sgtinSscc;
    }

    public void setSgtinSscc(List<String> sgtinSscc) {
        this.sgtinSscc = sgtinSscc;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getTtnsId() {
        return ttnsId;
    }

    public void setTtnsId(long ttnsId) {
        this.ttnsId = ttnsId;
    }

    public long getPvsId() {
        return pvsId;
    }

    public void setPvsId(long pvsId) {
        this.pvsId = pvsId;
    }
}
