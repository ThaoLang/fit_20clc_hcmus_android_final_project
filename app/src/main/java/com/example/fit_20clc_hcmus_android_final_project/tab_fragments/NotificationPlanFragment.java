package com.example.fit_20clc_hcmus_android_final_project.tab_fragments;

import android.app.PendingIntent;
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

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
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

        //TODO: show upcoming plans in notification
        fb.collection("plans")
                .whereEqualTo("status", "Upcoming")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG_PLAN", "Listen failed.", error);
                            return;
                        }

                        if (!querySnapshot.isEmpty()) {
                            notificationPlanList.clear();

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                String senderEmail = String.valueOf(document.get("senderEmail"));
//                                if (!senderEmail.equals(currentUser.getEmail())) {
                                Notification notification;

                                String name = String.valueOf(document.get("name"));
                                String message = String.valueOf(document.get("departure_date"));

                                notification = new Notification(name, message);
                                notificationPlanList.add(notification);

                                Log.e("plan_message", message);
//                                }
                            }

                            planAdapter = new CustomNotificationAdapter(context, notificationPlanList);
                            planAdapter.setListener(notificationPage);
                            binding.listItem.setLayoutManager(mLinearLayoutManager);
                            binding.listItem.setAdapter(planAdapter);
                            binding.listItem.smoothScrollToPosition(0);

                            // TODO: Revise intent to send to the right activity / plan when click on notification
                            Intent intent = new Intent(context, DetailedPlan.class); //supposedly from notification to plan detail?
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                            if (notificationPlanList.size()>0) {
                                String latestTitle = notificationPlanList.get(notificationPlanList.size() - 1).getTitle();
                                String latestContent = notificationPlanList.get(notificationPlanList.size() - 1).getContent();
                                notificationService.sendNotification(latestTitle, latestContent, pendingIntent);
                            }
                        } else {
                            Log.d("TAG_PLAN", "Current plan data: null");
                        }
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        intent.putExtra("DETAILED_PLAN_ID", tripId);

        getActivity().startActivity(intent);
    }
}