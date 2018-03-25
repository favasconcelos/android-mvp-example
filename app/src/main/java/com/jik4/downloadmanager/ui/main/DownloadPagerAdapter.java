package com.jik4.downloadmanager.ui.main;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.jik4.downloadmanager.ui.main.active.ActiveFragment;
import com.jik4.downloadmanager.ui.main.completed.CompletedFragment;

public class DownloadPagerAdapter extends FragmentStatePagerAdapter {

    public DownloadPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return ActiveFragment.newInstance();
            case 1:
                return CompletedFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
