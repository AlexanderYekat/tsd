package ru.ttmf.mark.network.model.TaskTransformationRequest;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TaskData {

    @SerializedName("ownerId")
    public int ownerId;

    @SerializedName("name")
    public String name;

    @SerializedName("sgtins")
    public ArrayList<String> sgtins;

    @SerializedName("ssccs")
    public ArrayList<String> ssccs;

    public TaskData(int ownerId, String name, ArrayList<String> sgtins, ArrayList<String> ssccs)
    {
        this.ownerId = ownerId;
        this.name = name;
        this.sgtins = sgtins;
        this.ssccs = ssccs;
    }
}
