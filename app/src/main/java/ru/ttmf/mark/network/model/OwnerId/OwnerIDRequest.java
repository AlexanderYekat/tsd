package ru.ttmf.mark.network.model.OwnerId;

import com.google.gson.annotations.SerializedName;

public class OwnerIDRequest {
    @SerializedName("Operation")
    public String Operation;

    @SerializedName("Data")
    public String Data;

    public OwnerIDRequest(String Operation, String Data)
    {
        this.Operation=Operation;
        this.Data=Data;
    }
}
