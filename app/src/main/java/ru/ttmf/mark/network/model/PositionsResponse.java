package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PositionsResponse extends BaseResponse {
    @SerializedName("Data")
    private List<Position> positions;

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
