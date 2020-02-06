package ru.ttmf.mark.barcode;

public interface OnDecodeCompleteListener {
    void onDecodeCompleted(int type, int length, String barcode);
}
