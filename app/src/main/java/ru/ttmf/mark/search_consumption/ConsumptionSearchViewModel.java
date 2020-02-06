package ru.ttmf.mark.search_consumption;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;

public class ConsumptionSearchViewModel extends ViewModel {
    private MutableLiveData<String> searchTerm;

    public LiveData<Response> searchInvoice(String token, String userId, String name) {
        return NetworkRepository.getInstance().getConsumptionPositions(token, name);
    }

    public MutableLiveData<String> getSearchTerm() {
        if (searchTerm==null){
            searchTerm = new MutableLiveData<>();
        }
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm.postValue(searchTerm);
    }
}
