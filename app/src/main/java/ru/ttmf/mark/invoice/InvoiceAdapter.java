package ru.ttmf.mark.invoice;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.Invoice;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvoiceAdapter extends RecyclerView.Adapter<InvoiceAdapter.InvoiceData> {

    private List<Invoice> invoices = new ArrayList<>();
    private OnInvoiceClickListener listener;

    public InvoiceAdapter(List<Invoice> invoices, OnInvoiceClickListener listener) {
        this.invoices.addAll(invoices);
        this.listener = listener;
    }

    @NonNull
    @Override
    public InvoiceData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InvoiceData(LayoutInflater.from(parent.getContext()).inflate(R.layout.invoice_viewholder, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull InvoiceData holder, int position) {
        holder.itemView.setTag(position);
        Invoice invoice = invoices.get(position);
        holder.count.setText(invoice.getCount() + " уп");
        holder.id.setText(invoice.getId());
        holder.name.setText(invoice.getName());
    }

    public void clearData(){
        invoices.clear();
        notifyDataSetChanged();
    }

    public void addData(List<Invoice> invoices){
        this.invoices.addAll(invoices);
        notifyDataSetChanged();
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
}
