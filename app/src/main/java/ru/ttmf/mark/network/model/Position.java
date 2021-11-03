package ru.ttmf.mark.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Position implements Serializable {
    @SerializedName("sgtin_sscc")
    @Expose
    private String sgTin;

    @SerializedName("quant")
    private String quant;

    public String getQuant() {
        if (quant != null) return quant;
        return "";
    }

    public Position(String tin){
        this.sgTin = tin;
    }

    public String getSgTin() {
        return sgTin;
    }

    public void setSgTin(String sgTin) {
        this.sgTin = sgTin;
    }
}
