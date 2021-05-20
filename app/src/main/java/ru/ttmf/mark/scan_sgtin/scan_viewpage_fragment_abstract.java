package ru.ttmf.mark.scan_sgtin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.CisResponse.CisData;
import ru.ttmf.mark.network.model.CisResponse.listview_item;
import ru.ttmf.mark.preference.PreferenceController;

abstract class scan_viewpage_fragment_abstract extends Fragment {
    public int position;
    private void setposition()
    {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            position = bundle.getInt("position", position);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_scan_page, container, false);
        setposition();
        CisData data = PreferenceController.getInstance().CisesInfoList.get(position);
        ListView listview = (ListView) returnView.findViewById(R.id.listview);
        scan_listview_adapter adapter = new scan_listview_adapter(getActivity().getApplicationContext(), getInfo());
        listview.setAdapter(adapter);
        return returnView;
    }
    abstract ArrayList<listview_item> getInfo();
}
