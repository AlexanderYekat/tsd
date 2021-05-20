package ru.ttmf.mark.scan_sgtin;

import java.util.ArrayList;

import ru.ttmf.mark.network.model.CisResponse.listview_item;
import ru.ttmf.mark.preference.PreferenceController;

public class scan_viewpage_fragment_owner_info extends scan_viewpage_fragment_abstract {

    protected ArrayList<listview_item> getInfo() {
        return PreferenceController.getInstance().CisesInfoList.get(position).cisInfo.GetOwnerInfo();
    }
}


