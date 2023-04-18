package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.tab_fragments.NotificationChatFragment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.tab_fragments.NotificationPlanFragment;

public class TabLayoutAdapter extends FragmentStatePagerAdapter {

    Context mContext;
    int mTotalTabs;

    public TabLayoutAdapter(Context context , FragmentManager fragmentManager , int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new NotificationChatFragment();
            case 1:
                return new NotificationPlanFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }
}
