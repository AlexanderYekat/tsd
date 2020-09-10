package ru.ttmf.mark.coming;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.ttmf.mark.R;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.invoice.InvoiceFragment;
import ru.ttmf.mark.network.model.Invoice;
import ru.ttmf.mark.preference.PreferenceController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SearchFragment extends BaseFragment implements Observer<Response> {

    private static final String DATA_TYPE = "dataType";

    public static SearchFragment createInstance(DataType type)
    {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putSerializable(DATA_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @BindView(R.id.code)
    EditText code;
    SearchViewModel searchViewModel;

    private TextView.OnEditorActionListener listener= new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                code.setOnEditorActionListener(null);
                search();
                return true;
            }
            return false;
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_coming;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        searchViewModel = ViewModelProviders.of(this)
                .get(SearchViewModel.class);

        if (getArguments() != null)
            searchViewModel.setDataType((DataType) getArguments().getSerializable(DATA_TYPE));

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        code.setOnEditorActionListener(listener);
        if (PreferenceController.getInstance().getNotFinish()) {
            Toast toast = Toast.makeText(getContext(), "Отсканированы не все позиции!", Toast.LENGTH_LONG);
            toast.show();
            PreferenceController.getInstance().setNotFinish(false);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        code.setOnEditorActionListener(null);
    }

    @OnClick(R.id.search)
    public void onSearchPressed() {
        search();
    }

    public void search() {
        hideKeyboard();
        searchViewModel.searchInvoice(
                        PreferenceController.getInstance().getToken(),
                        PreferenceController.getInstance().getUserId(),
                        code.getText().toString())
                .observe(this, this);
    }

    @Override
    public void onChanged(@Nullable Response response) {
        if (!isAdded()){
            return;
        }
        switch (response.getStatus()) {
            case ERROR:
                hideProgressDialog();
                showErrorDialog(response.getError());
                break;
            case LOADING:
                showProgressDialog();
                break;
            case SUCCESS:
                hideProgressDialog();
                if (response.getObject() == null || ((List<Invoice>) response.getObject()).isEmpty()) {
                    showErrorDialog(getString(R.string.search_error2));
                } else {
                    ArrayList<Invoice> invoices = new ArrayList<>();
                    invoices.addAll((List<Invoice>) response.getObject());

                    showFragment(InvoiceFragment.newInstance(
                            searchViewModel.getDataType(),
                            invoices,
                            code.getText().toString()),
                            getString(R.string.ttn),
                            true,
                            true);
                }
                break;
        }
    }
}
