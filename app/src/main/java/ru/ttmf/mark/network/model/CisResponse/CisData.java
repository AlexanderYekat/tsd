package ru.ttmf.mark.network.model.CisResponse;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import ru.ttmf.mark.R;


public class CisData
{
    @SerializedName("cisInfo")
    public cisInfo cisInfo;

    @SerializedName("errorMessage")
    public String errorMessage;

    @SerializedName("errorCode")
    public String errorCode;


    //TODO: разобраться что убрать и убрать
    public ArrayList<listview_item> GetBasicInfo()
    {
        if(errorMessage!=null) {
            ArrayList list = new ArrayList<listview_item>();
            list.add(new listview_item(cisInfo.GetCis(), R.string.cis));
            list.add(new listview_item(cisInfo.GetGtin(), R.string.gtin));
            if (errorCode.equals("401")) {
                list.add(new listview_item("Для получения информации выполните авторизацию", R.string.error));
            }
            else
            {
                list.add(new listview_item(errorMessage, R.string.error));
            }
            return list;
        }
        else {
            return cisInfo.GetBasicInfo();
        }
    }
}
