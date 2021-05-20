package ru.ttmf.mark.pallet_transform;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.PalletTransformRequest.PalletTransformRequest;

public class pallet_transform_activity_model extends ViewModel {
    public LiveData<Response> SaveTaskTransformation(PalletTransformRequest request) {
        return NetworkRepository.getInstance().SaveTaskTransformation(request);
    }

}
