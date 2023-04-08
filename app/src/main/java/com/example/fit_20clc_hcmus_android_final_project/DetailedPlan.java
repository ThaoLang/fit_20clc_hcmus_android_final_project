package com.example.fit_20clc_hcmus_android_final_project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailedPlan extends Fragment
{
    private RecyclerView destinations, travelers;
    private FloatingActionButton addButton;
    private Context context;
    private MainActivity main;

    private static final String INIT_PARAM = "INIT_PARAM";
    private String InitParam = null;


    public DetailedPlan newInstance(String initParam)
    {
        DetailedPlan fragment = new DetailedPlan();
        Bundle args = new Bundle();
        args.putString(INIT_PARAM, initParam);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null)
        {
            InitParam = getArguments().getString(INIT_PARAM);
        }

        try
        {
            main = (MainActivity) getActivity();
            context = getContext();
        }
        catch(IllegalStateException e)
        {
            throw new IllegalStateException("MainActivity must implements callbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_detailed_plan, null);
        destinations = inflatedView.findViewById(R.id.detailed_plan_recyclerview_destinations);
        travelers = inflatedView.findViewById(R.id.detailed_plan_recyclerview_travelers);



        return inflatedView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
