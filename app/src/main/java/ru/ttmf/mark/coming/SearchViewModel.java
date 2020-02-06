package ru.ttmf.mark.coming;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.io.Serializable;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.network.NetworkRepository;

public class SearchViewModel extends ViewModel {

    private DataType dataType;

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public LiveData<Response> searchInvoice(String token, String userId, String name) {
        return NetworkRepository.getInstance().search(dataType, token, userId, name);
    }
}
