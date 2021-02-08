package ru.ttmf.mark.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import ru.ttmf.mark.R;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.preference.PreferenceController;

public class ProxySettingsFragment extends BaseFragment {

    @BindView(R.id.proxy_address_field)
    TextInputEditText proxyAddressField;

    @BindView(R.id.proxy_port_field)
    TextInputEditText proxyPortField;

    @BindView(R.id.proxy_login_field)
    TextInputEditText proxyLoginField;

    @BindView(R.id.proxy_password_field)
    TextInputEditText proxyPasswordField;

    @BindView(R.id.proxy_enable)
    CheckBox proxyEnable;

    @BindView(R.id.btn_proxy_settings_save)
    AppCompatButton btnProxySettingsSave;

    @BindView(R.id.btn_proxy_settings_clear)
    AppCompatButton btnProxySettingsClear;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_settings;
    }

    private String address = PreferenceController.getInstance().getProxyAddress();
    private String port = PreferenceController.getInstance().getProxyPort();
    private String login = PreferenceController.getInstance().getProxyLogin();
    private String password = PreferenceController.getInstance().getProxyPassword();
    private Boolean proxySettingsState = PreferenceController.getInstance().getProxySettingsState();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_proxy_settings, container, false);

        ButterKnife.bind(this, rootView);
        proxyAddressField.setText(this.address);
        proxyPortField.setText(this.port);
        proxyLoginField.setText(this.login);
        proxyPasswordField.setText(this.password);
        proxyEnable.setChecked(proxySettingsState);

        return rootView;
    }

    @OnClick(R.id.btn_proxy_settings_save)
    public void onProxySettingsSave() {

        String address = proxyAddressField.getText().toString();
        String port = proxyPortField.getText().toString();
        String login = proxyAddressField.getText().toString();
        String password = proxyPasswordField.getText().toString();
        Boolean proxyState = proxyEnable.isChecked();

        if (!address.isEmpty() && !port.isEmpty() && !login.isEmpty() && !password.isEmpty() && proxyState) {

            PreferenceController.getInstance().setProxyAddress(address);
            PreferenceController.getInstance().setProxyPort(port);
            PreferenceController.getInstance().setProxyLogin(login);
            PreferenceController.getInstance().setProxyPassword(password);

            PreferenceController.getInstance().setProxySettingsState(true);
            hideKeyboard();
            NetworkRepository.refresh();
            getActivity().onBackPressed();
        } else if (!proxyState) {
            PreferenceController.getInstance().setProxySettingsState(false);
            hideKeyboard();
            NetworkRepository.refresh();
            getActivity().onBackPressed();
        } else {
            Toast.makeText(getActivity(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
    }


    @OnClick(R.id.btn_proxy_settings_clear)
    public void onProxySettingsClear() {
        proxyAddressField.setText("");
        proxyPortField.setText("");
        proxyLoginField.setText("");
        proxyPasswordField.setText("");
    }

    @OnCheckedChanged(R.id.proxy_enable)
    public void onComboboxChanged(CheckBox checkBox) {
        Boolean checked = checkBox.isChecked();
        if (checked) {
            changeFieldState(true);
        } else {
            changeFieldState(false);
        }
    }

    private void changeFieldState(Boolean state) {
        proxyAddressField.setEnabled(state);
        proxyPortField.setEnabled(state);
        proxyLoginField.setEnabled(state);
        proxyPasswordField.setEnabled(state);
    }
}
