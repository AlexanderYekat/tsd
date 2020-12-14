package ru.ttmf.mark.consumption_positions;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.positions.PositionsSaveModel;

import java.util.ArrayList;
import java.util.List;

public class ConsumptionPositionsViewModel extends ViewModel {

    private List<String> validPositions;

    private DataType dataType;

    private MutableLiveData<List<String>> scannedPositions;

    public LiveData<Response> searchPositions(String token, String cipher) {
        return NetworkRepository.getInstance().getConsumptionPositions(token, cipher);
    }

    public LiveData<Response> savePositions(String token,
                                            String userId,
                                            String itemId,
                                            String name,
                                            List<PositionsSaveModel> positions) {

        return NetworkRepository.getInstance().savePositions(dataType, token, userId, itemId, name, positions);
    }

    public void addPositions(String position) {
        List<String> value = getScannedPositions().getValue();
        if (value==null){
            value = new ArrayList<>();
        }
        value.add(position);
        getScannedPositions().postValue(value);
    }

    public MutableLiveData<List<String>> getScannedPositions() {
        if (scannedPositions == null) {
            scannedPositions = new MutableLiveData<>();
        }
        return scannedPositions;
    }

    public List<String> getValidPositions() {
        return validPositions;
    }

    public void setValidPositions(List<String> positions) {
        this.validPositions = positions;
    }

    public void setScannedPositions(List<String> scannedPositions) {
        this.scannedPositions = new MutableLiveData<>();
        this.scannedPositions.postValue(scannedPositions);
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public LiveData<Response> getTTNSgtinInfo(String token, String sgtin) {
        return NetworkRepository.getInstance().getTTNSgtinInfo(token, sgtin);
    }

    public LiveData<Response> getTTNSsccInfo(String token, String sscc) {
        return NetworkRepository.getInstance().getTTNSsccInfo(token, sscc);
    }
}
