package ru.ttmf.mark.home;

import android.content.Intent;

import ru.ttmf.mark.R;
import ru.ttmf.mark.Verification.VerificationActivity;
import ru.ttmf.mark.coming.DirectReversePositionsFragment;
import ru.ttmf.mark.pallet_transform.pallet_transform_activity;
import ru.ttmf.mark.scan.scan_activity;
import ru.ttmf.mark.coming.SearchFragment;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.login.LoginFragment;
import ru.ttmf.mark.logs.LogsActivity;
import ru.ttmf.mark.preference.PreferenceController;

import butterknife.OnClick;
import ru.ttmf.mark.task.task_activity;

public class HomeFragment extends BaseFragment {

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @OnClick(R.id.coming)
    public void onComingClick() {
        showFragment(new DirectReversePositionsFragment(), getString(R.string.direct_reverse), true, true);
        //showFragment(SearchFragment.createInstance(DataType.TTN),
        //      getString(R.string.coming), true, true);
    }

    @OnClick(R.id.consumption)
    public void onConsumptionClick() {
        showFragment(SearchFragment.createInstance(DataType.PV),
                getString(R.string.consumption), true, true);
    }

    @OnClick(R.id.logout)
    public void onLogoutClick() {
        PreferenceController.getInstance().clear();
        showFragment(new LoginFragment(), getString(R.string.enter), true, false);
    }
    /*
    @OnClick(R.id.inventory)
    public void onInventoryClick() {
        Toast.makeText(getContext(), R.string.inventory_in_dev, Toast.LENGTH_SHORT).show();
    }
     */
    /*
    @OnClick(R.id.scan_sscc)
    public void onScanSsccClick() {
        Intent intent = new Intent(getContext(), ScanSsccActivity.class);
        startActivity(intent);
    }
    */

    /*
    @OnClick(R.id.pallet_transform)
    public void onPalletTransformClick() {
        Intent intent = new Intent(getContext(), pallet_transform_activity.class);
        startActivity(intent);
    }
     */

    /*
    @OnClick(R.id.task)
    public void onTaskClick() {
        Intent intent = new Intent(getContext(), task_activity.class);
        startActivity(intent);
    }
     */

    @OnClick(R.id.scan_sgtin)
    public void onScanSgtinClick() {
        Intent intent = new Intent(getContext(), scan_activity.class);
        startActivity(intent);
    }

    /*
    @OnClick(R.id.barcode_info)
    public void onBarcodeInfoClick() {
        Intent intent = new Intent(getContext(), activity_sgtin_sscc_info.class);
        startActivity(intent);
    }
     */

    @OnClick(R.id.logs)
    public void onLogInfoClick() {
        Intent intent = new Intent(getContext(), LogsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.verification)
    public void onVerificationClick() {
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        startActivity(intent);
    }
}
