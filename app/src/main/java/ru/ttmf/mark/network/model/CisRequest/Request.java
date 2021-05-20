package ru.ttmf.mark.network.model.CisRequest;

import com.google.gson.annotations.SerializedName;

public class Request
{
    @SerializedName("Action")
    public String Action;

    @SerializedName("data")
    public requestdata data;

    public Request(String Action, requestdata data)
    {
        this.Action = Action;
        this.data = data;
    }

    public Request() {}


}
