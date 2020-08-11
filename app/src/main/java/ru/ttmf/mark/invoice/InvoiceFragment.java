package ru.ttmf.mark.invoice;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ru.ttmf.mark.R;
import ru.ttmf.mark.coming.SearchViewModel;
import ru.ttmf.mark.common.BaseFragment;
import ru.ttmf.mark.common.DataType;
import ru.ttmf.mark.common.Response;
import ru.ttmf.mark.consumption_positions.ConsumptionPositionsActivity;
import ru.ttmf.mark.network.model.Invoice;
import ru.ttmf.mark.positions.PositionsActivity;
import ru.ttmf.mark.preference.PreferenceController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class InvoiceFragment extends BaseFragment implements
        InvoiceAdapter.OnInvoiceClickListener,
        Observer<Response> {
    @BindView(R.id.invoiceData)
    RecyclerView rvInvoiceData;

    public static final String DATA_TYPE = "dataType";
    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String CIPHER = "CIPHER";
    public static final String COUNT = "COUNT";
    public static final String EAN13 = "EAN13";
    public static final String SCAN_COUNT = "SCAN_COUNT";
    public static final String SERIA = "SERIA";

    private static final String INVOICES = "invoices";
    private static final String CODE = "code";
    private static final int POSITIONS_SCANNED = 12;
    SearchViewModel searchViewModel;
    InvoiceAdapter adapter;

    public static InvoiceFragment newInstance(DataType type, ArrayList<Invoice> invoices, String code) {
        Bundle args = new Bundle();
        args.putSerializable(DATA_TYPE, type);
        args.putParcelableArrayList(INVOICES, invoices);
        args.putString(CODE, code);
        InvoiceFragment fragment = new InvoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_invoice;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initInvoiceData(getArguments().getParcelableArrayList(INVOICES));
        searchViewModel = ViewModelProviders.of(this)
                .get(SearchViewModel.class);

        if (getArguments() != null)
            searchViewModel.setDataType((DataType) getArguments().getSerializable(DATA_TYPE));

        hideKeyboard();
        return view;
    }

    public void initInvoiceData(ArrayList<Invoice> invoices) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvInvoiceData.setLayoutManager(linearLayoutManager);
        adapter = new InvoiceAdapter(invoices, this);
        rvInvoiceData.setAdapter(adapter);
    }

    @Override
    public void onInvoiceClick(Invoice invoice) {

        Intent intent = null;

        switch (searchViewModel.getDataType()) {
            case TTN_DIRECT:
                intent = new Intent(getContext(), PositionsActivity.class);
                intent.putExtra("TTN_TYPE", "0");
                break;
            case TTN_REVERSE:
                intent = new Intent(getContext(), PositionsActivity.class);
                intent.putExtra("TTN_TYPE", "1");
                break;
            case PV:
                intent = new Intent(getContext(), ConsumptionPositionsActivity.class);
                break;
        }

        intent.putExtra(CIPHER, invoice.getCipher());
        intent.putExtra(ID, invoice.getId());
        intent.putExtra(COUNT, invoice.getCount());
        intent.putExtra(SCAN_COUNT, invoice.getScanCount());
        intent.putExtra(SERIA, invoice.getSeria());
        intent.putExtra(EAN13, invoice.getEan13());
        intent.putExtra(NAME, getArguments().getString(CODE));
        intent.putExtra(DATA_TYPE, searchViewModel.getDataType());
        startActivityForResult(intent, POSITIONS_SCANNED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POSITIONS_SCANNED) {
            refreshData();
        }
    }

    public void refreshData() {
        adapter.clearData();
        searchViewModel.searchInvoice(
                PreferenceController.getInstance().getToken(),
                PreferenceController.getInstance().getUserId(),
                getArguments().getString(CODE)).observe(this, this);
    }


    @Override
    public void onChanged(@Nullable Response response) {
        switch (response.getStatus()) {
            case LOADING:
                showProgressDialog();
                break;
            case ERROR:
                hideProgressDialog();
                showErrorDialog(getString(R.string.search_error));
                break;
            case SUCCESS:
                hideProgressDialog();
                if (response.getObject() == null || ((List<Invoice>) response.getObject()).isEmpty()) {
                    showErrorDialog(getString(R.string.search_error2));
                } else {
                    adapter.addData((List<Invoice>) response.getObject());
                }
                break;
        }
    }
}
