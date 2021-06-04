package ru.ttmf.mark.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.ttmf.mark.common.DataWedgeConstants;

public class ATOL_BarcodeDataBroadcastReceiver extends BroadcastReceiver {

    public OnDecodeCompleteListener listener;

    public ATOL_BarcodeDataBroadcastReceiver(OnDecodeCompleteListener listener){
        this.listener = listener;
    }
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        String barcode = arg1.getStringExtra(DataWedgeConstants.EXTRA_BARCODE_DECODING_DATA);
        int type = arg1.getIntExtra(DataWedgeConstants.EXTRA_BARCODE_DECODING_SYMBOLE, 0);
        int length = arg1.getIntExtra(DataWedgeConstants.DATA_LENGTH, 0);
        listener.onDecodeCompleted(type, length, barcode);
    }
}
