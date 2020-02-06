package ru.ttmf.mark.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Position implements Serializable {
    @SerializedName("sgtin")
    @Expose
    private String sgTin;

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
