package ru.ttmf.mark.scan;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.CisResponse.listview_item;


public class scan_listview_adapter extends ArrayAdapter<listview_item> {
    Context context;
    LayoutInflater layout;
    int layoutResource;
    ArrayList<listview_item> items;

   scan_listview_adapter(@NonNull Context context, ArrayList<listview_item> items)
   {
       super(context, R.layout.listview_item, items);
       this.items = items;
       this.context = context;
       this.layoutResource = layoutResource;
   }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public listview_item getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view==null)
        {
            view = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
        }
        listview_item item = getItem(position);
        TextView text = (TextView) view.findViewById(R.id.listview_text);
        TextView title = (TextView) view.findViewById(R.id.listview_title);
        title.setText(item.title);
        text.setText(item.text);
        return view;
    }
}
