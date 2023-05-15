package com.example.fit_20clc_hcmus_android_final_project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.adapter.FavoriteLocationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityHomepageBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment implements FavoriteLocationAdapter.Callbacks, PostAdapter.Callbacks {

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private MainActivity main_activity;
    private Context context;

    LinearLayoutManager recentPostManager;
    LinearLayoutManager favoriteLocationManager;
    LinearLayoutManager popularLocationManager;
    ActivityHomepageBinding binding;

    private PostAdapter postAdapter;
    private FavoriteLocationAdapter favoriteLocationAdapter;
    private FavoriteLocationAdapter popularLocationAdapter;

    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance(String param1) {
        HomePage fragment = new HomePage();
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
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        if (DatabaseAccess.getMainUserInfo() == null) {
            return;
        }
        if (DatabaseAccess.getMainUserInfo().getFavorite_locations() == null) {
            binding.emptyPlaceText.setVisibility(View.VISIBLE);
        } else {
            binding.emptyPlaceText.setVisibility(View.GONE);
            favoriteLocationAdapter = new FavoriteLocationAdapter(context, DatabaseAccess.getMainUserInfo().getFavorite_locations());
            favoriteLocationAdapter.setListener(HomePage.this);
            binding.favoriteLocations.setAdapter(favoriteLocationAdapter);
            binding.favoriteLocations.smoothScrollToPosition(0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ActivityHomepageBinding.inflate(inflater, container, false);

        //recent posts
        recentPostManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        recentPostManager.setStackFromEnd(true);

        binding.listPost.setLayoutManager(recentPostManager);

        ArrayList<Plan> plans = new ArrayList<>();
        FirebaseFirestore fb = DatabaseAccess.getFirestore();

        fb.collection("plans")
                .whereEqualTo("publicAttribute", Boolean.valueOf("true"))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Plan plan = document.toObject(Plan.class);
                                plans.add(plan);
                                Log.e("MANY POST", String.valueOf(plans.size()));
                            }

                            Log.e("Last POST", String.valueOf(plans.size()));

                            postAdapter = new PostAdapter(context, plans);
                            postAdapter.setListener(HomePage.this);
                            binding.listPost.setAdapter(postAdapter);
                            binding.listPost.smoothScrollToPosition(0);
                        }
                    }
                });


        favoriteLocationManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        favoriteLocationManager.setStackFromEnd(true);
        binding.favoriteLocations.setLayoutManager(favoriteLocationManager);

        //favorite locations
        Thread favoriteThread = new Thread(() -> {
            while (!DatabaseAccess.getUserInfoStatus()) {
                Log.e("LOADING FAVORITE", "YES");
            }
            Runnable updateFavoriteLocation = () -> {
                if (DatabaseAccess.getMainUserInfo().getFavorite_locations() == null) {
                    binding.emptyPlaceText.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyPlaceText.setVisibility(View.GONE);
                    favoriteLocationAdapter = new FavoriteLocationAdapter(context, DatabaseAccess.getMainUserInfo().getFavorite_locations());
                    favoriteLocationAdapter.setListener(HomePage.this);
                    binding.favoriteLocations.setAdapter(favoriteLocationAdapter);
                    binding.favoriteLocations.smoothScrollToPosition(0);
                }
            };
            DatabaseAccess.runForegroundTask(updateFavoriteLocation);
        });
        favoriteThread.start();


        DatabaseAccess.getFirestore().collection("location")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (!querySnapshot.isEmpty()) {
                            List<String> locations = new ArrayList<>();

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                locations.add(String.valueOf(document.get("formalName")));
                            }

                            popularLocationAdapter = new FavoriteLocationAdapter(context, locations);
                            popularLocationAdapter.setListener(HomePage.this);
                            binding.popularLocations.setAdapter(popularLocationAdapter);
                            binding.popularLocations.smoothScrollToPosition(0);
                        }
                    }
                });

        //popular locations
        popularLocationManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        popularLocationManager.setStackFromEnd(true);
        binding.popularLocations.setLayoutManager(popularLocationManager);

        //search btn
        binding.searchBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), Search.class);
            intent.putExtra(Search.SEARCH_MODE, Search.SEARCH_LOCATION_INFO);
            startActivity(intent);
        });

        return binding.getRoot();
    }

    public void swapToLocationInfo(String locationName) {
        Intent intent = new Intent(context, LocationInfo.class);
        Bundle bundle = new Bundle();
        bundle.putString("location address", locationName);

        intent.putExtra("location search", bundle);
        startActivity(intent);
    }

    public void swapToPost(Plan plan) {
        Intent intent = new Intent(context, DetailedPost.class);
        Bundle bundle = new Bundle();
        bundle.putString("plan id", plan.getPlanId());
        intent.putExtra("plan post", bundle);
        startActivity(intent);
    }

}