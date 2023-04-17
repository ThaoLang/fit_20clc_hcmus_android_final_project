package com.example.fit_20clc_hcmus_android_final_project;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.fit_20clc_hcmus_android_final_project.adapter.TabLayoutAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPageBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseUser;

public class NotificationPage extends Fragment {
    private FragmentNotificationPageBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private Context context;

    private String mParam1;

    private MainActivity main_activity;
    private FirebaseUser currentUser;
    private FragmentManager fragmentManager;
    private TabLayoutAdapter adapter;

    public NotificationPage() {
        // Required empty public constructor
    }

    public static NotificationPage newInstance(String param1) {
        NotificationPage fragment = new NotificationPage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        try {
            context = getActivity();
            main_activity = (MainActivity) getActivity();
//            fragmentManager = this.getChildFragmentManager();
            fragmentManager = this.getFragmentManager();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationPageBinding.inflate(inflater, container, false);

        binding.tablayout.addTab(binding.tablayout.newTab().setText("My Chat"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("My Plan"));
        binding.tablayout.setTabGravity(binding.tablayout.GRAVITY_FILL);

        adapter = new TabLayoutAdapter(context,fragmentManager,binding.tablayout.getTabCount());
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tablayout));

        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return binding.getRoot();
    }
}