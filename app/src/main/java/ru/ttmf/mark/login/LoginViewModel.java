package ru.ttmf.mark.login;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.preference.PreferenceController;

public class LoginViewModel extends ViewModel {

    public String login = PreferenceController.getInstance().getLogin();
    public String password = PreferenceController.getInstance().getPassword();

    public LiveData<Response> login(String login, String password) {
        return NetworkRepository.getInstance().login(login, password);
    }
}
