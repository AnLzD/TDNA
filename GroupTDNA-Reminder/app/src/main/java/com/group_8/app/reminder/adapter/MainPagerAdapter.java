package com.group_8.app.reminder.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.group_8.app.reminder.fragment.CalendarFragment;
import com.group_8.app.reminder.fragment.FinderFragment;
import com.group_8.app.reminder.fragment.HomeFragment;
import com.group_8.app.reminder.fragment.SettingFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private static int PAGER_COUNT = 3;

    private HomeFragment homeFragment;
    private CalendarFragment calendarFragment;
    private FinderFragment finderFragment;
    //private SettingFragment settingFragment;

    public MainPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                homeFragment = new HomeFragment();
                return homeFragment;
            case 1:
                calendarFragment = new CalendarFragment();
                return calendarFragment;
            default:
                finderFragment = new FinderFragment();
                return finderFragment;
            //default:
                //settingFragment =  new SettingFragment();
                //return settingFragment;
        }
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }
}
