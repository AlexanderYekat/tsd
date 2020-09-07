package ru.ttmf.mark.DeviceInfo;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.preference.PreferenceController;

public class DeviceInfoViewModel extends ViewModel {

    public String login = PreferenceController.getInstance().getLogin();
    public String cur_serial = PreferenceController.getInstance().getSerial();
    public Integer cur_version = PreferenceController.getInstance().getVersion();

    public LiveData<Response> send_device_info(String cur_serial, Integer cur_version, String login) {
        return NetworkRepository.getInstance().send_device_info(cur_serial, cur_version, login);
    }
}
