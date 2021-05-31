package ru.ttmf.mark.network.model.TaskTransformationRequest;

import com.google.gson.annotations.SerializedName;

public class TaskRequest {
    @SerializedName("Operation")
    public String Operation;

    @SerializedName("Data")
    public TaskData Data;

    public TaskRequest(String Operation, TaskData Data)
    {
        this.Operation=Operation;
        this.Data=Data;
    }
}
