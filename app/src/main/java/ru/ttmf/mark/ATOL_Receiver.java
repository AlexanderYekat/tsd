package ru.ttmf.mark;

import android.content.IntentFilter;

import ru.ttmf.mark.barcode.ATOL_BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;

public class ATOL_Receiver extends ReceiverCreator {


    public ATOL_Receiver(OnDecodeCompleteListener listener) {
        super(listener);
    }

    @Override
    public ATOL_BarcodeDataBroadcastReceiver getReceiver() {
        return new ATOL_BarcodeDataBroadcastReceiver(listener);
    }

    @Override
    public IntentFilter getFilter() {
        return new IntentFilter("com.xcheng.scanner.action.BARCODE_DECODING_BROADCAST");
    }
}
