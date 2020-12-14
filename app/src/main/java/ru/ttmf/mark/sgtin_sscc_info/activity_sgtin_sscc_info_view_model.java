package ru.ttmf.mark.sgtin_sscc_info;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.Position;

import java.util.List;

public class activity_sgtin_sscc_info_view_model extends ViewModel {

    private DataType dataType;

    public LiveData<Response> getTTNSgtinInfo(String token, String sgtin) {
        return NetworkRepository.getInstance().getTTNSgtinInfo(token, sgtin);
    }

    public LiveData<Response> getTTNSsccInfo(String token, String sscc) {
        return NetworkRepository.getInstance().getTTNSsccInfo(token, sscc);
    }

    public LiveData<Response> getUNPSgtinInfo(String token, String sgtin) {
        return NetworkRepository.getInstance().getUNPSgtinInfo(token, sgtin);
    }

    public LiveData<Response> getUNPSsccInfo(String token, String sscc) {
        return NetworkRepository.getInstance().getUNPSsccInfo(token, sscc);
    }

    public LiveData<Response> getPVSgtinInfo(String token, String sgtin) {
        return NetworkRepository.getInstance().getPVSgtinInfo(token, sgtin);
    }

    public LiveData<Response> getPVSsccInfo(String token, String sscc) {
        return NetworkRepository.getInstance().getPVSsccInfo(token, sscc);
    }

}
