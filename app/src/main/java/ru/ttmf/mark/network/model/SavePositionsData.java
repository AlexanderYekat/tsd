package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.ttmf.mark.common.DataType;

public class SavePositionsData {

    @SerializedName("token")
    private String token;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("TtnItemId")
    private String ttnItemId;

    @SerializedName("TtnSpecItemId")
    private String ttnSpecItemId;

    @SerializedName("PvName")
    private String pvName;

    @SerializedName("PvsId")
    private String pvsId;

    @SerializedName("Sgtins")
    private List<String> items;


    public SavePositionsData(DataType type,
                             String token,
                             String userId,
                             String itemId,
                             String name,
                             List<String> items) {

        this.token = token;
        this.userId = userId;
        this.items = items;

        switch (type) {
            case TTN:
                ttnItemId = name;
                ttnSpecItemId = itemId;
                break;
            case PV:
                pvName = name;
                pvsId = itemId;
                break;
        }
    }
}
