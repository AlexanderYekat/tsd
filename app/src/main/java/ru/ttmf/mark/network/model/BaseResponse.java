package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("IsSuccess")
    private boolean isSuccess;

    @SerializedName("ErrorText")
    private String errorText;

    //@SerializedName("Data")
    //private String str_data;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    /*public String getBaseData() {
        return str_data;
    }

    public void setBaseData(String data) {
        this.str_data = str_data;
    }*/
}
