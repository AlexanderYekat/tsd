package ru.ttmf.mark.scan_sscc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ttmf.mark.R;
import ru.ttmf.mark.common.BaseFragment;

public class ScanSscc extends BaseFragment {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_sscc;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }
}
