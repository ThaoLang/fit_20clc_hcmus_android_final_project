package com.example.fit_20clc_hcmus_android_final_project;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.adapter.Trips_Incoming_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.Trips_Ongoing_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Incoming_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.search.SearchBar;
import com.google.firebase.database.DataSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TripsPage extends Fragment {

    private List<Plan> demoData;
    private MainActivity main;
    private Context context;
    private String InitParam;

    private SearchBar searchBar;
    private MaterialButton IncomingButton, OngoingButton, HistoryButton;
    private FloatingActionButton fab;

    private RecyclerView recyclerViewPosition;

    private int currentMode;

    public static String CREATE_PLAN_MODE = "CREATE_PLAN_MODE";
    public static String EDIT_PLAN_MODE = "EDIT_PLAN_MODE";

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


        demoData = DatabaseAccess.getDemoData();
        Trips_Incoming_Adapter adapter = new Trips_Incoming_Adapter(getContext(), demoData);
        recyclerViewPosition.setAdapter(adapter);

        currentMode = 0;
        IncomingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Incoming trips");
                if(currentMode == 0)
                {
                    return;
                }
                currentMode= 0;
                demoData = DatabaseAccess.getDemoData();
                Trips_Incoming_Adapter adapter = new Trips_Incoming_Adapter(getContext(), demoData);
                recyclerViewPosition.setAdapter(adapter);
                IncomingButton.setBackgroundColor(getResources().getColor(R.color.CustomColor7, Resources.getSystem().newTheme()));
                OngoingButton.setBackgroundColor(getResources().getColor(R.color.CustomColor3, Resources.getSystem().newTheme()));
            }
        });

        OngoingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Ongoing trips");
                if(currentMode == 1)
                {
                    return;
                }
                currentMode = 1;
                demoData = DatabaseAccess.getDemoData();
                Trips_Ongoing_Adapter adapter = new Trips_Ongoing_Adapter(getContext(), demoData);
                recyclerViewPosition.setAdapter(adapter);
                OngoingButton.setBackgroundColor(getResources().getColor(R.color.CustomColor7, Resources.getSystem().newTheme()));
                IncomingButton.setBackgroundColor(getResources().getColor(R.color.CustomColor3, Resources.getSystem().newTheme()));
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

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //start CreatePlan activity
                //new way to start a activity and receive results from that activity.
                Intent createPlanLaunchIntent = new Intent(context, CreatePlan.class);
                createPlanLaunchIntent.putExtra("MODE", CREATE_PLAN_MODE);
                activityLauncher.launch(createPlanLaunchIntent);
            }
        });

        return view;
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    //receive results returned from the specific activity launched by activityLauncher.launch(...);
    private ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result)
        {
            if(result.getResultCode() == Activity.RESULT_OK)
            {
                Intent intentReturned = result.getData();
                Plan newPlan;
                if(intentReturned != null)
                {
                    //get the new plan created by CreatePlan activity
                    Bundle bundle = intentReturned.getBundleExtra(CreatePlan.IDENTIFIED_CODE);
                    byte[] byteArray = bundle.getByteArray(CreatePlan.RETURN_NEW_PLAN_CODE);
                    newPlan = Plan.byteArrayToObject(byteArray);
//                    System.out.println("<<<System out>>> " + plan.getName());
                    //run insertion task
                    DatabaseAccess.addNewPlan(newPlan, null, null);
                }
            }
        }
        });


}
