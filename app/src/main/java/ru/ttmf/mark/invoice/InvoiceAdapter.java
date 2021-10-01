package ru.ttmf.mark.invoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.Invoice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.ttmf.mark.preference.PreferenceController;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceData> {

    private List<Invoice> invoices = new ArrayList<>();
    private OnInvoiceClickListener listener;
    public int selectedRecyclerPosition = -1;

    public InvoiceAdapter(List<Invoice> invoices, OnInvoiceClickListener listener) {
        this.invoices.addAll(invoices);
        this.listener = listener;
        sortList();
    }

    @NonNull
    @Override
    public InvoiceData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InvoiceData(LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_viewholder, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceData holder, int position) {
        selectedRecyclerPosition = position;
        holder.itemView.setTag(position);
        Invoice invoice = invoices.get(position);

        int temp_scan_count = 0;
        int temp_count_int = 0;
        double temp_count_double = 0.0;

        temp_scan_count = (int)Double.parseDouble(invoice.getScanCount());
        temp_count_double = Double.parseDouble(invoice.getCount());

        if (temp_count_double % 1 > 0) {
            holder.count.setText("(" + temp_scan_count + " из "+ temp_count_double + " уп)");

            if (temp_scan_count < temp_count_double)
            { PreferenceController.getInstance().setNotFinish(true); }

            if (temp_scan_count > temp_count_double)
            { PreferenceController.getInstance().setUnScanned(true); }

        }
        else
        {
            temp_count_int = (int) temp_count_double;
            holder.count.setText("(" + temp_scan_count + " из "+ temp_count_int + " уп)");

            if (temp_scan_count < temp_count_int)
            { PreferenceController.getInstance().setNotFinish(true); }

            if (temp_scan_count > temp_count_int)
            { PreferenceController.getInstance().setUnScanned(true); }
        }

        //holder.count.setText(invoice.getCount() + " уп");
        //holder.id.setText(invoice.getId());
        holder.id.setText(invoice.getCipher());
        holder.seria.setText(invoice.getSeria());
        holder.name.setText(invoice.getName());
    }

    public void clearData(){
        invoices.clear();
        notifyDataSetChanged();
    }

    public void addData(List<Invoice> invoices){
        this.invoices.addAll(invoices);
        notifyDataSetChanged();
        sortList();
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    public class InvoiceData extends RecyclerView.ViewHolder {

        @BindView(R.id.id)
        TextView id;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.count)
        TextView count;
        @BindView(R.id.seria)
        TextView seria;

        public InvoiceData(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onInvoiceClick(invoices.get((Integer) itemView.getTag()));
                    }
                }
            });
        }
    }

    public interface OnInvoiceClickListener {
        void onInvoiceClick(Invoice invoice);
    }

    private void sortList() {
        Collections.sort(this.invoices);
    }
}
