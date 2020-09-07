package ru.ttmf.mark.DeviceInfo;

import com.google.gson.annotations.SerializedName;

public class DeviceInfoModel {
    @SerializedName("cur_serial")
    private String cur_serial;

    @SerializedName("cur_version")
    private Integer cur_version;

    @SerializedName("login")
    private String login;

    public DeviceInfoModel(String cur_serial, Integer cur_version, String login){
        this.cur_serial = cur_serial;
        this.cur_version = cur_version;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getCur_serial() {
        return cur_serial;
    }

    public void setCur_serial(String cur_serial) {
        this.cur_serial = cur_serial;
    }

    public Integer getCur_version() {
        return cur_version;
    }

    public void setCur_version(Integer cur_version) {
        this.cur_version = cur_version;
    }
}
