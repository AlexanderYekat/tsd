package ru.ttmf.mark.scan;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

import ru.ttmf.mark.R;
import ru.ttmf.mark.network.model.CisResponse.listview_item;
import ru.ttmf.mark.preference.PreferenceController;

public class scan_viewpage_fragment_child extends scan_viewpage_fragment_abstract {
    @Override
    ArrayList<listview_item> getInfo() {
        return null;
    }

    @Override
    protected ArrayAdapter getAdapter()
    {
        return new ArrayAdapter<String>(getActivity().getApplicationContext(),
                R.layout.array_adapter_list_item,
                PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.GetChild());
    }
}
