package ru.ttmf.mark.unpack;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ru.ttmf.mark.R;
import ru.ttmf.mark.home.HomeFragment;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.network.model.UserData;
import ru.ttmf.mark.preference.PreferenceController;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;

import java.security.*;

import butterknife.BindView;
import butterknife.OnClick;
import ru.ttmf.mark.settings.SettingsFragment;

public class UnpackFragment extends BaseFragment implements Observer<Response> {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_unpack;
    }

    @Override
    public void onChanged(@Nullable Response response) {

    }

}
