package ru.ttmf.mark.search_consumption;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import ru.ttmf.mark.R;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.consumption_positions.ConsumptionPositionsActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchConsumptionFragment extends BaseFragment implements Observer<String> {
    @BindView(R.id.code)
    EditText code;
    ConsumptionSearchViewModel searchViewModel;

    private TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                code.setOnEditorActionListener(null);
                search(code.getText().toString());
                return true;
            }
            return false;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coming;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        searchViewModel = ViewModelProviders.of(getActivity())
                .get(ConsumptionSearchViewModel.class);
        searchViewModel.getSearchTerm().observe(getActivity(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        searchViewModel.getSearchTerm().removeObserver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        code.setOnEditorActionListener(listener);
    }


    @Override
    public void onPause() {
        super.onPause();
        code.setOnEditorActionListener(null);
    }

    @OnClick(R.id.search)
    public void onSearchPressed() {
        search(code.getText().toString());
    }


    protected void search(String searchCode) {
        hideKeyboard();
        searchViewModel.setSearchTerm(searchCode);
    }

    @Override
    public void onChanged(@Nullable String code) {
        if (!TextUtils.isEmpty(code) && isAdded()) {
            searchViewModel.setSearchTerm("");
            Intent intent = new Intent(this.requireContext(), ConsumptionPositionsActivity.class);
            intent.putExtra(ConsumptionPositionsActivity.CODE, code);
            startActivity(intent);
        }
    }
}
