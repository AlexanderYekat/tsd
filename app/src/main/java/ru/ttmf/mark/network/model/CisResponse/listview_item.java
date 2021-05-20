package ru.ttmf.mark.network.model.CisResponse;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

public class listview_item {
    public String text;
    public int title;
    public listview_item(String text, @StringRes int title)
    {
        this.text = text;
        this.title = title;
    }
}
