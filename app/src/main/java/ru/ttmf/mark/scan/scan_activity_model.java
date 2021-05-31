package ru.ttmf.mark.scan;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.CisRequest.requestdata;

public class scan_activity_model extends ViewModel {
    public LiveData<Response> getCisInfo(requestdata request) {
        return NetworkRepository.getInstance().getCisInfo(request);
    }
}
