package ru.ttmf.mark.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse extends BaseResponse {

    @SerializedName("Data")
    private List<Invoice> invoiceList;


    public List<Invoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<Invoice> invoiceList) {
        this.invoiceList = invoiceList;
    }
}
