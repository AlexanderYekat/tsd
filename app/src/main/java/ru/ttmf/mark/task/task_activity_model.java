package ru.ttmf.mark.task;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.PalletTransformRequest.PalletTransformRequest;

public class task_activity_model extends ViewModel {
    public LiveData<Response> SaveTaskTransformation(PalletTransformRequest request) {
        return NetworkRepository.getInstance().SaveTaskTransformation(request);
    }

}
