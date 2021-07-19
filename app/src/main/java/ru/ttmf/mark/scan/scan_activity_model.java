package ru.ttmf.mark.scan;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.CisRequest.requestdata;

public class scan_activity_model extends ViewModel {
    public LiveData<Response> getCisInfo(requestdata request) {
        return NetworkRepository.getInstance().getCisInfo(request);
    }
    /*
    public LiveData<Response> GetCises(ArrayList<String> request, String groupType, String token) {
        return NetworkRepository.getInstance().GetCises(request, groupType, token);
    }

     */
    public LiveData<Response> getTTNSgtinInfo(String token, String sgtin) {
        return NetworkRepository.getInstance().getTTNSgtinInfo(token, sgtin);
    }
    public LiveData<Response> getTTNSsccInfo(String token, String sscc) {
        return NetworkRepository.getInstance().getTTNSsccInfo(token, sscc);
    }
}
