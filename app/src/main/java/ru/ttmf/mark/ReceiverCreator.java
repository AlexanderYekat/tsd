package ru.ttmf.mark;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import ru.ttmf.mark.barcode.BarcodeDataBroadcastReceiver;
import ru.ttmf.mark.barcode.OnDecodeCompleteListener;

abstract public class ReceiverCreator {
    OnDecodeCompleteListener listener;
    public ReceiverCreator(OnDecodeCompleteListener listener)
    {
        this.listener = listener;
    }
    protected abstract BroadcastReceiver getReceiver();
    protected abstract IntentFilter getFilter();
}
