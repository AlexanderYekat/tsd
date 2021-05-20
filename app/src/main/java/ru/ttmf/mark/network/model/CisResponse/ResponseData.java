package ru.ttmf.mark.network.model.CisResponse;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ru.ttmf.mark.network.model.BaseResponse;

public class ResponseData extends BaseResponse
{

    @SerializedName("Data")
    public ArrayList<CisData> CisData;

    public ArrayList<CisData> getData()
    {
        return CisData;
    }

}

