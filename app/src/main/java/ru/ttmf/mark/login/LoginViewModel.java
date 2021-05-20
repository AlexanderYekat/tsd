package ru.ttmf.mark.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.OwnerId.OwnerIDRequest;
import ru.ttmf.mark.preference.PreferenceController;

public class LoginViewModel extends ViewModel {

    public String login = PreferenceController.getInstance().getLogin();
    public String password = PreferenceController.getInstance().getPassword();

    public LiveData<Response> login(String login, String password, Context context) {
        return NetworkRepository.getInstance(context).login(login, password);
    }

    public LiveData<Response> GetOwnerID(OwnerIDRequest request)
    {
        return NetworkRepository.getInstance().GetOwnerID(request);
    }
}
