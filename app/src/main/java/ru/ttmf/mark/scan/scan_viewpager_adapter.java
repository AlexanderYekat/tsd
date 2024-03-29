package ru.ttmf.mark.scan;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class scan_viewpager_adapter extends FragmentStatePagerAdapter {
    public scan_viewpager_adapter(FragmentManager fm) {
        super(fm);
    }
private final List<Fragment> mFragmentList = new ArrayList<>();
private final List<String> mFragmentTitleList = new ArrayList<>();
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
    @Override
    public int getCount() {
        return mFragmentList.size();
    }
    public void addFragment(Fragment fragment, String title)
    {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }
}
