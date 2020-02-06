package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

public class BaseResponse {

    @SerializedName("IsSuccess")
    private boolean isSuccess;


    @SerializedName("ErrorText")
    private String errorText;

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
}
