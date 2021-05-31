package ru.ttmf.mark.network.model.CisResponse;

import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.RequiresApi;

import com.google.gson.annotations.SerializedName;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.Predicate;

import ru.ttmf.mark.R;

public class cisInfo
{
    @SerializedName("requestedCis")
    public String requestedCis;

    @SerializedName("cis")
    public String cis;

    @SerializedName("gtin")
    public String gtin;

    @SerializedName("tnVedEaes")
    public String tnVedEaes;

    @SerializedName("tnVedEaesGroup")
    public String tnVedEaesGroup;

    @SerializedName("productName")
    public String productName;

    @SerializedName("productGroupId")
    public int productGroupId;

    @SerializedName("productGroup")
    public String productGroup;

    @SerializedName("brand")
    public String brand;

    @SerializedName("emissionDate")
    public String emissionDate;

    @SerializedName("emissionType")
    public String emissionType;

    @SerializedName("packageType")
    public String packageType;

    @SerializedName("ownerInn")
    public String ownerInn;

    @SerializedName("ownerName")
    public String ownerName;

    @SerializedName("status")
    public String status;

    @SerializedName("statusEx")
    public String statusEx;

    @SerializedName("maxRetailPrice")
    public Object maxRetailPrice;

    @SerializedName("producerInn")
    public String producerInn;

    @SerializedName("producerName")
    public String producerName;

    @SerializedName("prVetDocument")
    public String prVetDocument;

    @SerializedName("markWithdraw")
    public boolean markWithdraw;

    @SerializedName("expirationDate")
    public String expirationDate;

    @SerializedName("parent")
    public String parent;

    @SerializedName("child")
    public ArrayList<String> child;

    public String GetStatus() {
        switch (status) {
            case "EMITTED":
                return "Выпущен";
            case "APPLIED":
                return "Выпущен";
            case "INTRODUCED":
                return "В обороте";
            case "WRITTEN_OFF":
                return "Списан";
            case "RETIRED":
                return "Выбыл";
            case "WITHDRAWN":
                return "Выбыл";
            case "INTRODUCED_RETURNED":
                return "Возвращён в оборот";
            case "DISAGGREGATION":
                return "Расформирован";
            case "DISAGGREGATED":
                return "Расформирован";
            default: return null;
        }
    }
    private String DateFormat(String date) {
        //SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat outputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date inputdate = inputformat.parse(date);
            return outputformat.format(inputdate);
        } catch (Exception ex)
        {
           return date;
        }
    }
    public String GetExpirationDate()
    {
        return DateFormat(expirationDate);
    }
    public String GetEmissionDate()
    {
        return DateFormat(emissionDate);
    }


    public ArrayList<listview_item> GetBasicInfo()
    {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(cis, R.string.cis));
        list.add(new listview_item(gtin, R.string.gtin));
        list.add(new listview_item(productGroup, R.string.productGroupTitle));
        list.add(new listview_item(brand, R.string.brandTitle));
        list.add(new listview_item(productName, R.string.productNameTitle));
        list.add(new listview_item(tnVedEaes, R.string.tnVedTitle));
        list.add(new listview_item(status, R.string.statusTitle));
        list.add(new listview_item(GetExpirationDate(), R.string.expiratioinDateTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetEmissionInfo()
    {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(emissionType, R.string.emissionTypeTitle));
        list.add(new listview_item(GetEmissionDate(), R.string.emissionDateTitle));
        list.add(new listview_item(packageType, R.string.packageTypeTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetOwnerInfo()
    {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(ownerName, R.string.ownerNameTitle));
        list.add(new listview_item(ownerInn, R.string.ownerInnTitle));
        list.add(new listview_item(producerName, R.string.producerNameTitle));
        list.add(new listview_item(producerInn, R.string.producerInnTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetPrvetInfo()
    {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(prVetDocument, R.string.prVetTitle));
        return RemoveNulls(list);
    }

    public ArrayList<String> GetChild()
    {
        return child;
    }
    private ArrayList<listview_item> RemoveNulls(ArrayList<listview_item> list)
    {
        ArrayList<listview_item> result = new ArrayList<listview_item>();
        for(listview_item item: list)
        {
            if(item.text!=null)
            {
                result.add(item);
            }
        }
        return result;
    }
}


