package com.example.fit_20clc_hcmus_android_final_project.service;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.AlarmNotification;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.CloudNotification;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFirebaseNotificationService extends Service {
    MainActivity activity = new MainActivity();
    private static boolean isRunning;
    private NotificationService notificationService;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationService = new NotificationService(getApplicationContext());
        notificationService.createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        isRunning = true;
        System.out.println("Service is started.");
        DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_ALARM_COLLECTION)
                .document(DatabaseAccess.getMainUserInfo().getUserEmail()).addSnapshotListener(new EventListener<DocumentSnapshot>()
                {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error)
                    {
                        if(error != null)
                        {
                            Log.e("ERROR IN DOCUMENTSNAPSHOT LISTENER", error.getMessage());
                            return;
                        }
                        AlarmNotification alarm = value.toObject(AlarmNotification.class); //contains notification id

                        for(int i = 0; i< alarm.getChanges().size();i++)
                        {
                            System.out.println(alarm.getChanges().get(i));
                        }

                        //broadcast the new notification
                        List<String> noti_id = alarm.getChanges();
                        for(int i = 0; i< noti_id.size(); i++)
                        {
                            DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_NOTIFICATION_COLLECTION)
                                    .document(noti_id.get(i))
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            CloudNotification notifiction = documentSnapshot.toObject(CloudNotification.class);
                                            synchronized (DatabaseAccess.getNotifications())
                                            {
                                                DatabaseAccess.getNotifications().add(notifiction);
                                            }
                                            //call a main function to notify that having a new notification

                                            for(int i=0; i< DatabaseAccess.getNotifications().size(); i++)
                                            {
                                                if(DatabaseAccess.getNotifications().get(i).getTopic().equals(CloudNotification.TOPIC_INVITE_FRIENDS))
                                                {
                                                    String title = DatabaseAccess.getNotifications().get(i).getTitle();
                                                    String content = DatabaseAccess.getNotifications().get(i).getContent();
                                                    Intent intent = new Intent(getApplicationContext(), DetailedPlan.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    intent.putExtra("DETAILED_PLAN_ID", DatabaseAccess.getNotifications().get(i).getTargets().get(0));
                                                    intent.putExtra("MODE", "REMOTE");
                                                    notificationService.sendNotification(title, content, intent,activity , 1);
                                                }
                                            }

                                        }
                                    });
                        }
                        for(int i = 0; i< noti_id.size(); i++)
                        {
                            DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_ALARM_COLLECTION)
                                    .document(DatabaseAccess.getMainUserInfo().getUserEmail())
                                    .update("changes", FieldValue.arrayRemove(noti_id.get(i)))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            System.out.println("Remove read alarm");
                                        }
                                    });
                        }
                    }
                });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service is destroyed");
    }

}
