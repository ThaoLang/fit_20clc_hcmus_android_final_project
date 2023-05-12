package com.example.fit_20clc_hcmus_android_final_project.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Notification;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AlertService extends Service {
    Thread serviceThread;

    NotificationService notificationService;
    private FirebaseFirestore fb;
    private FirebaseUser currentUser;

    private ArrayList<Notification> notificationPlanList;
//    private final int CHAT_TAB_ID = 0;
    private final int PLAN_TAB_ID = 1;

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("<<MyAlertService-onStart>>", "I am alive!");
        notificationService = new NotificationService(this);

        serviceThread = new Thread(this::sendAlert);
        serviceThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendAlert(){
        fb = DatabaseAccess.getFirestore();
        currentUser = DatabaseAccess.getCurrentUser();

        notificationPlanList = new ArrayList<>();

        // plan notification
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
                    } else {
                        Log.d("TAG_PLAN", "Current plan data: null");
                    }
                });

        // TODO: Send plan notifications in the background
        Intent intent = new Intent(this, DetailedPlan.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("DETAILED_PLAN_ID", notificationPlanList.get(notificationPlanList.size() - 1).getTripId());

        if (notificationPlanList.size()>0) {
            String latestTitle = notificationPlanList.get(notificationPlanList.size() - 1).getTitle();
            String latestContent = notificationPlanList.get(notificationPlanList.size() - 1).getContent();
            notificationService.sendNotification(latestTitle, latestContent, intent, (Activity) this.getApplicationContext(), PLAN_TAB_ID);
        }

        // TODO: Send chat notifications

    }
}
