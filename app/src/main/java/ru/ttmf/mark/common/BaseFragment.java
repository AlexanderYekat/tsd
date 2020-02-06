package ru.ttmf.mark.common;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import ru.ttmf.mark.MainActivity;
import ru.ttmf.mark.R;
import ru.ttmf.mark.login.LoginFragment;
import ru.ttmf.mark.preference.PreferenceController;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    private ProgressDialog progressDialog;

    @LayoutRes
    protected abstract int getLayoutId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        ButterKnife.bind(this, view);
        setTitle(getTag());
        hideKeyboard();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected void showFragment(Fragment fragment, String tag) {
        showFragment(R.id.container, fragment, tag);

    }

    protected void showFragment(Fragment fragment, String tag, boolean isReplace, boolean isAddToBackStack) {
        showFragment(R.id.container, fragment, tag, isReplace, isAddToBackStack);

    }

    protected void showFragment(@IdRes int containerViewId, Fragment fragment, String tag) {
        getActivity().getSupportFragmentManager().beginTransaction().replace(containerViewId, fragment, tag).addToBackStack(tag).commit();

    }

    protected void showFragment(@IdRes int containerViewId, Fragment fragment, String tag, boolean isReplase, boolean isAddToBackStack) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (isReplase) {
            fragmentTransaction.replace(containerViewId, fragment, tag);
        } else {
            fragmentTransaction.add(containerViewId, fragment, tag);
        }
        if (isAddToBackStack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commit();

    }

    protected void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setTitle(getString(R.string.loading));
        progressDialog.show();
    }

    protected void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void setTitle(String title) {
        if (((MainActivity) getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(title);
        }
    }

    protected void showErrorDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(text);
        builder.setTitle(R.string.error);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            dialog.dismiss();
            if (text.contains("Токен") || text.contains("токен")) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                PreferenceController.getInstance().clear();
                showFragment(new LoginFragment(), getString(R.string.enter), true, false);
            }
        });
        builder.show();

    }
}
