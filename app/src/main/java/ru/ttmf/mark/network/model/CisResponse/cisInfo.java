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
    private String requestedCis;

    @SerializedName("cis")
    private String cis;

    @SerializedName("gtin")
    private String gtin;

    @SerializedName("tnVedEaes")
    private String tnVedEaes;

    @SerializedName("tnVedEaesGroup")
    private String tnVedEaesGroup;

    @SerializedName("productName")
    private String productName;

    @SerializedName("productGroupId")
    private int productGroupId;

    @SerializedName("productGroup")
    private String productGroup;

    @SerializedName("brand")
    private String brand;

    @SerializedName("emissionDate")
    private String emissionDate;

    @SerializedName("emissionType")
    private String emissionType;

    @SerializedName("packageType")
    private String packageType;

    @SerializedName("ownerInn")
    private String ownerInn;

    @SerializedName("ownerName")
    private String ownerName;

    @SerializedName("status")
    private String status;

    @SerializedName("statusEx")
    private String statusEx;

    @SerializedName("maxRetailPrice")
    private Object maxRetailPrice;

    @SerializedName("producerInn")
    private String producerInn;

    @SerializedName("producerName")
    private String producerName;

    @SerializedName("prVetDocument")
    private String prVetDocument;

    @SerializedName("markWithdraw")
    private boolean markWithdraw;

    @SerializedName("expirationDate")
    private String expirationDate;

    @SerializedName("producedDate")
    private String producedDate;

    @SerializedName("parent")
    private String parent;

    @SerializedName("child")
    private ArrayList<String> child;

    public String GetRequestedCis() { return requestedCis; }

    public String GetCis() { return cis; }

    public String GetGtin() { return gtin; }

    public String GetTnVedEaes() { return tnVedEaes; }

    public String GetTnVedEaesGroup() {return tnVedEaesGroup; }

    public String GetProductName() {return productName; }

    public int GetProductGroupId() {return productGroupId; }

    public String GetProductGroup() {
        switch (productGroup) {
            case "lp": return "Предметы одежды, белье постельное, столовое, туалетное и куханное";
            case "shoes": return "Обувные товары";
            case "tobacco": return "Табачная продукция";
            case "perfumery": return "Духи и туалетная вода";
            case "tires": return "Шины и покрышки пневматические резиновые новые";
            case "electronics": return "Фотокамеры";
            case "pharma": return "Лекарственные препараты";
            case "milk": return "Молочная продукция";
            case "bicycle": return "Велосипеды и велосипедные рамы";
            case "wheelchairs": return "Кресла-коляски";
            case "water": return "Упаковання вода";
            case "orp": return "Альтернативная табачная продукция";
            default: return null;
        }
    }

    public String GetBrand() { return brand; }

    public String GetProduceDate() { return DateFormat(producedDate); }

    public String GetEmissionDate() { return DateFormat(emissionDate); }

    public String GetEmissionType() {
        switch (emissionType) {
            case "LOCAL": return "Производство РФ";
            case "FOREIGN": return "Ввезён в РФ";
            case "REMAINS": return "Маркировка остатков";
            case "CROSSBORDER": return "Ввезён из стран ЕАЭС";
            case "REMARK": return "Перемаркировка";
            case "COMISSION": return "Принят на комиссию от физического лица";
            default: return null;
        }
    }

    public String GetPackageType() {
        switch (packageType) {
            case "UNIT": return "Единица товара";
            case "GROUP": return "Групповая упаковка";
            case "LEVEL1": return "Упаковка 1-го уровня";
            case "LEVEL2": return "Упаковка 2-го уровня";
            case "LEVEL3": return "Упаковка 3-го уровня";
            case "LEVEL4": return "Упаковка 4-го уровня";
            case "LEVEL5": return "Упаковка 5-го уровня";
            case "BUNDLE": return "Комплект";
            case "ATK": return "Агрегированный таможенный код";
            case "SET": return "Набор";
            default: return null;
        }
    }

    public String GetOwnerInn() { return ownerInn; }

    public String GetOwnerName() { return ownerName; }

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

    public String GetStatusEx() { return statusEx; }

    public ArrayList<String> GetChild() { return child; }

    public String GetProducerInn() { return producerInn; }

    public String GetProducerName() { return productName; }

    public boolean GetMarkWithDraw() { return markWithdraw; }

    public String GetExpirationDate() { return DateFormat(expirationDate); }

    public String GetPrVetDocument() { return prVetDocument; }

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

    public ArrayList<listview_item> GetBasicInfo() {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(GetCis(), R.string.cis));
        list.add(new listview_item(GetGtin(), R.string.gtin));
        list.add(new listview_item(GetProductGroup(), R.string.productGroupTitle));
        list.add(new listview_item(GetBrand(), R.string.brandTitle));
        list.add(new listview_item(GetProductName(), R.string.productNameTitle));
        list.add(new listview_item(GetTnVedEaes(), R.string.tnVedTitle));
        list.add(new listview_item(GetStatus(), R.string.statusTitle));
        list.add(new listview_item(GetExpirationDate(), R.string.expiratioinDateTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetEmissionInfo() {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(GetEmissionType(), R.string.emissionTypeTitle));
        list.add(new listview_item(GetEmissionDate(), R.string.emissionDateTitle));
        list.add(new listview_item(GetPackageType(), R.string.packageTypeTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetOwnerInfo() {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(GetOwnerName(), R.string.ownerNameTitle));
        list.add(new listview_item(GetOwnerInn(), R.string.ownerInnTitle));
        list.add(new listview_item(GetProducerName(), R.string.producerNameTitle));
        list.add(new listview_item(GetProducerInn(), R.string.producerInnTitle));
        return RemoveNulls(list);
    }

    public ArrayList<listview_item> GetPrvetInfo() {
        ArrayList list = new ArrayList<listview_item>();
        list.add(new listview_item(GetPrVetDocument(), R.string.prVetTitle));
        return RemoveNulls(list);
    }

    private ArrayList<listview_item> RemoveNulls(ArrayList<listview_item> list) {
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


