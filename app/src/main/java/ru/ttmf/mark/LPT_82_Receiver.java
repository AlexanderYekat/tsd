package ru.ttmf.mark;

import android.content.IntentFilter;

import ru.ttmf.mark.barcode.LPT_82_BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;

public class LPT_82_Receiver extends ReceiverCreator {


    public LPT_82_Receiver(OnDecodeCompleteListener listener) {
        super(listener);
    }

    @Override
    public LPT_82_BarcodeDataBroadcastReceiver getReceiver() {
        return new LPT_82_BarcodeDataBroadcastReceiver(listener);
    }

    @Override
    public IntentFilter getFilter() {
        return new IntentFilter("DATA_SCAN");
    }
}
