package ru.ttmf.mark.home;
import android.widget.Button;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import ru.ttmf.mark.R;
import ru.ttmf.mark.coming.DirectReversePositions;
import ru.ttmf.mark.coming.SearchFragment;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.login.LoginFragment;
import ru.ttmf.mark.preference.PreferenceController;

import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @OnClick(R.id.coming)
    public void onComingClick() {
        showFragment(new DirectReversePositions(),  getString(R.string.direct_reverse), true, true);
        //showFragment(SearchFragment.createInstance(DataType.TTN),
        //      getString(R.string.coming), true, true);
    }

    @OnClick(R.id.consumption)
    public void onConsumptionClick(){
        showFragment(SearchFragment.createInstance(DataType.PV),
                getString(R.string.consumption), true, true);
    }

    @OnClick(R.id.logout)
    public void onLogoutClick() {
        PreferenceController.getInstance().clear();
        showFragment(new LoginFragment(), getString(R.string.enter), true, false);
    }

    @OnClick(R.id.inventory)
    public void onInventoryClick() {
        Toast.makeText(getContext(), R.string.inventory_in_dev, Toast.LENGTH_SHORT).show();
    }

}
