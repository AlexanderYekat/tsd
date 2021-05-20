package ru.ttmf.mark.network.model.CisRequest;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class requestdata
{
    @SerializedName("ownerId")
    public int ownerId;

    @SerializedName("cises")
    public ArrayList<String> cises;

    public requestdata(int ownerId, String cis)
    {
        this.ownerId = ownerId;
        this.cises = new ArrayList<String>();
        this.cises.add(cis);
    }
    public requestdata(int ownerId, String... cises)
    {
        this.ownerId = ownerId;
        this.cises = new ArrayList<String>();
        for (String cis: cises) {
            this.cises.add(cis);
        }
    }
    public requestdata(int ownerId)
    {
        this.ownerId=ownerId;
        this.cises = new ArrayList<String>();
    }
    public void add (String cis) {
        this.cises.add(cis);
    }
}
