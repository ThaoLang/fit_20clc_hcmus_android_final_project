package com.example.fit_20clc_hcmus_android_final_project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.adapter.Trips_Incoming_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Incoming_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TripsPage extends Fragment {

    private List<Plan> demoData = new ArrayList<Plan>();
    private MainActivity main;
    private Context context;
    private String InitParam;

    private SearchBar searchBar;
    private MaterialButton IncomingButton, OngoingButton, HistoryButton;
    private FloatingActionButton fab;

    private RecyclerView recyclerViewPosition;

    private Trips_Incoming_Adapter trips_incoming_adapter;

    private int currentMode;

    private static final String INIT_PARAM = "INIT_PARAM";



    public static TripsPage newInstance(String initParam)
    {
        TripsPage fragment = new TripsPage();
        Bundle args = new Bundle();
        args.putString(INIT_PARAM, initParam);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
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

//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_trips, null);

        searchBar = (SearchBar) view.findViewById(R.id.trips_search_bar);
        IncomingButton = (MaterialButton) view.findViewById(R.id.trips_incoming_button);
        OngoingButton = (MaterialButton) view.findViewById(R.id.trips_ongoing_button);
        HistoryButton = (MaterialButton) view.findViewById(R.id.trips_history_button);
        fab = (FloatingActionButton) view.findViewById(R.id.trips_fab);
        recyclerViewPosition = (RecyclerView) view.findViewById(R.id.trips_recyclerview_holder);

        //Demo data - should be removed after using to database
        LocalDate date = LocalDate.now();
        LocalDate endDate = LocalDate.of(2023,4,20);

        demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(),4, false, 1F));
        endDate = LocalDate.of(2023, 4, 30);

        demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(),4, false, 1F));

        Trips_Incoming_Adapter adapter = new Trips_Incoming_Adapter(getContext(), demoData);
        recyclerViewPosition.setAdapter(adapter);
        //Demo data

        currentMode = 0;

        IncomingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMode= 0;
                System.out.println("Incoming trips");
            }
        });

        OngoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMode = 1;
                System.out.println("Ongoing trips");
            }
        });

        HistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMode = 2;
                System.out.println("History");
            }
        });

        IncomingButton.setBackgroundColor(getResources().getColor(R.color.CustomColor7, Resources.getSystem().newTheme()));

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();

    }

}
