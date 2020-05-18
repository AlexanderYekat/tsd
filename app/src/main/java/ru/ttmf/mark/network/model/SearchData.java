package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import ru.ttmf.mark.common.DataType;

public class SearchData {
    @SerializedName("token")
    private String token;

    @SerializedName("UserId")
    private String userId;

    @SerializedName("TtnName")
    private String ttnName;

    @SerializedName("PvName")
    private String pvName;

    public SearchData(DataType type, String token, String userId, String name) {
        this.token = token;
        this.userId = userId;

        switch (type) {
            case TTN_REVERSE:
            case TTN_DIRECT:
                ttnName = name;
                break;
            case PV:
                pvName = name;
                break;
        }
    }
}
