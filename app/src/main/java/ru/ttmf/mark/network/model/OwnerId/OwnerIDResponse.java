package ru.ttmf.mark.network.model.OwnerId;

import com.google.gson.annotations.SerializedName;

import ru.ttmf.mark.network.model.BaseResponse;

public class OwnerIDResponse extends BaseResponse {
        @SerializedName("Data")
        public ownerIdData data;
    }
