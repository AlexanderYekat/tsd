package ru.ttmf.mark.coming;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import ru.ttmf.mark.R;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.preference.PreferenceController;


public class DirectReversePositions extends BaseFragment {
    @BindView(R.id.direct)
    Button directButton;
    @BindView(R.id.reverse)
    Button reverseButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_direct_reverse_coming;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        PreferenceController.getInstance().setNotFinish(false);
        PreferenceController.getInstance().setUnScanned(false);

        View view = super.onCreateView(inflater, container, savedInstanceState);
        directButton.setOnClickListener(v -> showFragment(SearchFragment.createInstance(DataType.TTN_DIRECT),
                getString(R.string.direct_coming), true, true));
        reverseButton.setOnClickListener(view1 -> showFragment(SearchFragment.createInstance(DataType.TTN_REVERSE),
                getString(R.string.reverse_coming), true, true));
        return view;
    }
}
