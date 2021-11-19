package ru.ttmf.mark;

import android.content.IntentFilter;

import ru.ttmf.mark.barcode.HONEYWELL_EDA50K_BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;

public class HONEYWELL_EDA50K_Receiver extends ReceiverCreator {


    public HONEYWELL_EDA50K_Receiver(OnDecodeCompleteListener listener) {
        super(listener);
    }

    @Override
    public HONEYWELL_EDA50K_BarcodeDataBroadcastReceiver getReceiver() {
        return new HONEYWELL_EDA50K_BarcodeDataBroadcastReceiver(listener);
    }

    @Override
    public IntentFilter getFilter() {
        return new IntentFilter("DATA_SCAN");
    }
}
