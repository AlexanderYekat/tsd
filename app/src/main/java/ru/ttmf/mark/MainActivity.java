package ru.ttmf.mark;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.home.HomeFragment;
import ru.ttmf.mark.login.LoginFragment;
import ru.ttmf.mark.network.NetworkRepository;
import ru.ttmf.mark.network.model.UserData;
import ru.ttmf.mark.preference.PreferenceController;
import ru.ttmf.mark.search_consumption.ConsumptionSearchViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends ScanActivity
        implements //Observer<Response>,
        FragmentManager.OnBackStackChangedListener,
        android.support.v4.app.FragmentManager.OnBackStackChangedListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private BarcodeDataBroadcastReceiver intentBarcodeDataReceiver;
    ConsumptionSearchViewModel searchViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initToolbar(toolbar);
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        intentBarcodeDataReceiver = new BarcodeDataBroadcastReceiver((type, length, barcode)
                -> searchViewModel.setSearchTerm(barcode));

        if (TextUtils.isEmpty(PreferenceController.getInstance().getToken())) {
            showFragment(new LoginFragment(), getString(R.string.enter), true, false);
        } else {
            showFragment(new HomeFragment(), getString(R.string.menu), true, false);
        }


        LiveData version;
        int cur_version = PreferenceController.getInstance().getVersion();
        //int last_version = 0;
        //int check_version_attempt_count = 30;

        NetworkRepository.getInstance().version();
        /*Object t_obj = version.getValue();
        if (t_obj != null) {
            last_version = (int) t_obj;
        }*/

        /*while (last_version == 0 && (check_version_attempt_count > 0)) {
            if (last_version == 0  && check_version_attempt_count != 0) {
                version = NetworkRepository.getInstance().version();
                Object t_obj = version.getValue();
                if (t_obj != null) {
                    last_version = (int) t_obj;
                }
                check_version_attempt_count = check_version_attempt_count - 1;
            }
            else
            {
                break;
            }
        }*/

        /*if (cur_version < last_version) {
            showDialog("Вышла новая версия программы!\nНачать скачивание?");
        }*/
    }


    @Override
    protected void onStart() {
        registerReceiver(intentBarcodeDataReceiver, new IntentFilter("DATA_SCAN"));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(intentBarcodeDataReceiver);
        super.onStop();
    }

    protected void showFragment(Fragment fragment, String tag) {
        showFragment(R.id.container, fragment, tag);
    }

    protected void showStoredFragment(Fragment fragment, String tag) {
        Fragment storedFragment = getSupportFragmentManager().findFragmentByTag(tag);
        showFragment(R.id.container, storedFragment == null ? fragment : storedFragment, tag);
    }

    protected void showFragment(@IdRes int containerViewId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().replace(containerViewId, fragment, tag).addToBackStack(tag).commit();
    }

    protected void showFragment(Fragment fragment, String tag, boolean isReplace, boolean isAddToBackStack) {
        showFragment(R.id.container, fragment, tag, isReplace, isAddToBackStack);
    }

    public void showFragment(@IdRes int containerViewId, Fragment fragment, String tag, boolean isReplace, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (isReplace) {
            fragmentTransaction.replace(containerViewId, fragment, tag);
        } else {
            fragmentTransaction.add(containerViewId, fragment, tag);
        }
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();
    }

    public void initToolbar(Toolbar toolbar) {
        initToolbar(toolbar, "");
    }

    public void initToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }
        }
    }

    public String getFragmentTag() {
        String fragmentTag = getString(R.string.menu);
        int index = getSupportFragmentManager().getBackStackEntryCount();
        if (index > 0) {
            fragmentTag = getSupportFragmentManager().getBackStackEntryAt(index - 1).getName();
        }
        return fragmentTag;
    }


    @Override
    public void setTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    @Override
    public void onBackStackChanged() {
        if (getSupportActionBar() != null) {
            boolean show = getSupportFragmentManager().getBackStackEntryCount() != 0;
            getSupportActionBar().setTitle(getFragmentTag());
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        hideKeyboard();
    }

    /*private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.yes, (dialog, which) -> {
            dialog.dismiss();
            startDownload();
        });
        builder.setNegativeButton(R.string.no, (dialog, which) -> {
            dialog.dismiss();
        });
        builder.show();
    }*/

    /*private void startDownload() {
        final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
        try {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        } catch (Exception ex) {
            Intent browserIntent = new
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://office2.ttmf.ru/ProgramLogs/api/TSDgetapk"));
            startActivity(browserIntent);
        }
    }*/

    /*@Override
    public void onChanged(@Nullable Response response) {
        switch (response.getStatus()) {
            case ERROR:
                break;
            case SUCCESS:
                hideKeyboard();
                String version = (String) response.getObject();
                break;
            case LOADING:
                break;
        }
    }*/
}

