package ru.ttmf.mark.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.ttmf.mark.DeviceInfo.DeviceInfoViewModel;
import ru.ttmf.mark.R;
import ru.ttmf.mark.home.HomeFragment;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.UserData;
import ru.ttmf.mark.preference.PreferenceController;

import java.security.*;

import butterknife.BindView;
import butterknife.OnClick;
import ru.ttmf.mark.settings.SettingsFragment;

public class LoginFragment extends BaseFragment implements Observer<Response> {

    @BindView(R.id.passwordField)
    TextInputEditText passwordField;
    @BindView(R.id.userField)
    TextInputEditText userField;

    LoginViewModel viewModel;
    DeviceInfoViewModel devInfoviewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        viewModel = ViewModelProviders.of(this)
                .get(LoginViewModel.class);
        devInfoviewModel = ViewModelProviders.of(this)
                .get(DeviceInfoViewModel.class);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        userField.setText(PreferenceController.getInstance().getLogin());
        passwordField.setText(PreferenceController.getInstance().getPassword());
    }

    @SuppressLint("MissingPermission")
    @OnClick(R.id.login)
    public void login() {
        hideKeyboard();

        int cur_version = PreferenceController.getInstance().getVersion();
        int last_version = PreferenceController.getInstance().getLastVersion();

        if (cur_version < last_version) {
            showDialog("Версия программы не является актуальной!");
        }

        String cur_serial = "";
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                cur_serial = Build.getSerial();
            }
            else
            {
                cur_serial = Build.SERIAL;
            }
        }
        catch (Exception ex) {
            cur_serial = "indefined";
        }

        PreferenceController.getInstance().setSerial(cur_serial);

        viewModel.login(
                userField.getText().toString(),
                this.hasGet(passwordField.getText().toString()))
                .observe(this, this::onChanged);

        devInfoviewModel.send_device_info(cur_serial, cur_version, userField.getText().toString());
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(message);
        /*.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            startDownload();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        });*/
        builder.show();
    }

    /*private void startDownload() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://office2.ttmf.ru/ProgramLogs/api/TSDgetapk"));
            startActivity(browserIntent);
        }
        catch (Exception ex) {
        }
    }*/

    @OnClick(R.id.btn_settings)
    public void onSettingsButtonClick() {
        hideKeyboard();
        showFragment(new SettingsFragment(), getString(R.string.settings), true, true);
    }

    private void next() {
        showFragment(new HomeFragment(), getString(R.string.menu), true, false);
    }

    private String md5get(final String s) {
        String output = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            output = hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return output;
    }

    private String hasGet(String password) {
        return md5get(password);
    }

    @Override
    public void onChanged(@Nullable Response response) {
        switch (response.getStatus()) {
            case ERROR:
                showErrorDialog(response.getError());
                hideProgressDialog();
                break;
            case SUCCESS:
                hideProgressDialog();
                hideKeyboard();
                UserData userData = ((UserData) response.getObject());
                PreferenceController.getInstance().setToken(userData.getToken());
                PreferenceController.getInstance().setUserId(userData.getUserInfo().getId());
                PreferenceController.getInstance().setUserName(userData.getUserInfo().getFio());

                if (PreferenceController.getInstance().isRememberAuth()) {
                    PreferenceController.getInstance().setLogin(userField.getText().toString());
                    PreferenceController.getInstance().setPassword(passwordField.getText().toString());
                }

                next();
                break;
            case LOADING:
                showProgressDialog();
                break;
        }
    }
}
