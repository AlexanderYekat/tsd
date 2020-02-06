package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConsumptionResponse extends BaseResponse {
    @SerializedName("Data")
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("Sgtins")
        private List<String> positions;

        @SerializedName("ValidSgtins")
        private List<String> validPositions;

        public List<String> getPositions() {
            return positions;
        }

        public void setPositions(List<String> positions) {
            this.positions = positions;
        }

        public List<String> getValidPositions() {
            return validPositions;
        }

        public void setValidPositions(List<String> validPositions) {
            this.validPositions = validPositions;
        }
    }
}
