package ru.ttmf.mark.network.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserInfo implements Serializable {
    @SerializedName("Id")
    @Expose
    private String id;

    @SerializedName("Fio")
    @Expose
    private String fio;


    public UserInfo(String id, String fio) {
        this.fio = fio;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }
}
