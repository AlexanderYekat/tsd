package ru.ttmf.mark.positions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.Position;
import ru.ttmf.mark.network.model.SgtinInfo;

import java.util.List;

public class PositionsViewModel extends ViewModel {
    private List<Position> positions;

    private DataType dataType;

    public LiveData<Response> loadPositions(String token, String cipher) {
        return NetworkRepository.getInstance().getPositions(dataType, token, cipher);
    }

    public LiveData<Response> savePositions(String token,
                                            String userId,
                                            String itemId,
                                            String name,
                                            Object positions) {

        return NetworkRepository.getInstance().savePositions(dataType, token, userId, itemId, name, positions);
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public LiveData<Response> getSgtinInfo(String token, String sgtin, Integer operationType) {
        return NetworkRepository.getInstance().getSgtinInfo(token, sgtin, operationType);
    }

    public LiveData<Response> getSsccInfo(String token, String sscc, Integer operationType) {
        return NetworkRepository.getInstance().getSsccInfo(token, sscc, operationType);
    }
}
