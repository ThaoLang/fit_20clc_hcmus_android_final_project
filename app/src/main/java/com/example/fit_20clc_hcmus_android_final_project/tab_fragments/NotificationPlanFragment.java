package com.example.fit_20clc_hcmus_android_final_project.tab_fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.adapter.CustomNotificationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Notification;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPlanBinding;
import com.example.fit_20clc_hcmus_android_final_project.service.NotificationService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationPlanFragment extends Fragment implements CustomNotificationAdapter.Callbacks {
    private MainActivity main_activity;
    private Context context;
    private FirebaseUser currentUser;

    private FragmentNotificationPlanBinding binding;

    private CustomNotificationAdapter planAdapter;
    private ArrayList<Notification> notificationPlanList;
    LinearLayoutManager mLinearLayoutManager;
    NotificationPlanFragment notificationPage;
    NotificationService notificationService;
    private final int PLAN_TAB_ID = 1;


    public NotificationPlanFragment() {
        notificationPage = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main_activity = (MainActivity) getActivity();
            currentUser = DatabaseAccess.getCurrentUser();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationPlanBinding.inflate(inflater, container, false);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(false);
        notificationPlanList = new ArrayList<>();

        FirebaseFirestore fb = DatabaseAccess.getFirestore();

        fb.collection("plans")
                .whereEqualTo("status", "Upcoming")
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null) {
                        Log.w("TAG_PLAN", "Listen failed.", error);
                        return;
                    }

                    if (!querySnapshot.isEmpty()) {
                        notificationPlanList.clear();

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            ArrayList<String> passengers = (ArrayList<String>) document.get("passengers");
                            passengers.add(String.valueOf(document.get("owner_email")));

                            if (passengers.contains(currentUser.getEmail())){
                                Notification notification;

                                String name = String.valueOf(document.get("name"));
                                String message = String.valueOf(document.get("departure_date"));
                                String planId = String.valueOf(document.get("planId"));

                                notification = new Notification(name, message, planId);
                                notificationPlanList.add(notification);

                                Log.e("plan_message", message);
                            }
                        }

                        planAdapter = new CustomNotificationAdapter(context, notificationPlanList);
                        planAdapter.setListener(notificationPage);
                        binding.listItem.setLayoutManager(mLinearLayoutManager);
                        binding.listItem.setAdapter(planAdapter);
                        binding.listItem.smoothScrollToPosition(0);

                        Intent intent = new Intent(context, DetailedPlan.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("DETAILED_PLAN_ID", notificationPlanList.get(notificationPlanList.size() - 1).getTripId());

                        if (notificationPlanList.size()>0) {
                            String latestTitle = notificationPlanList.get(notificationPlanList.size() - 1).getTitle();
                            String latestContent = notificationPlanList.get(notificationPlanList.size() - 1).getContent();
                            notificationService.sendNotification(latestTitle, latestContent, intent, main_activity, PLAN_TAB_ID);
                        }
                    } else {
                        Log.d("TAG_PLAN", "Current plan data: null");
                    }
                });
        planAdapter = new CustomNotificationAdapter(context, notificationPlanList);
        planAdapter.setListener(this);
        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.setAdapter(planAdapter);
        binding.listItem.smoothScrollToPosition(0);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        currentUser = DatabaseAccess.getCurrentUser();
        //user has signed in
        if(currentUser != null) {
            notificationService = new NotificationService(context);
            notificationService.createNotificationChannel();
        }
    }

    public void swapToChat(String tripId){
        Intent intent = new Intent(context, DetailedPlan.class);
        intent.putExtra("DETAILED_PLAN_ID", tripId);
        main_activity.startActivity(intent);
    }
}