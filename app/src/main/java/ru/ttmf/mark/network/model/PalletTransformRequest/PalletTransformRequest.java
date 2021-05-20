package ru.ttmf.mark.network.model.PalletTransformRequest;

import com.google.gson.annotations.SerializedName;

public class PalletTransformRequest {
    @SerializedName("Operation")
    public String Operation;

    @SerializedName("Data")
    public palletData Data;

    public PalletTransformRequest(String Operation, palletData Data)
    {
        this.Operation=Operation;
        this.Data=Data;
    }
}
