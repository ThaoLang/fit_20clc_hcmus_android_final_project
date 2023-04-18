package com.example.fit_20clc_hcmus_android_final_project.data_struct.tab_fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.adapter.CustomNotificationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Notification;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPlanBinding;
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

    final private String CHANNEL_ID = "NOTIFICATION_CH_ID";
    final int notificationId = 1;

    public NotificationPlanFragment() {
        notificationPage = this;
    }

    public static NotificationPlanFragment newInstance() {
        NotificationPlanFragment fragment = new NotificationPlanFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            main_activity = (MainActivity) getActivity();
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

        FirebaseFirestore fb = main_activity.getFirebaseFirestore();
//        FirebaseFirestore fb = DatabaseAccess.getFirestore();

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
            createNotificationChannel();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager)this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification(String title, String content) {
        // TODO: Revise intent to send to the right activity / plan when click on notification
        Intent intent = new Intent(context, ChatActivity.class); //supposedly from notification to plan detail?
//        intent.putExtra(INTENT_EXTRA_NOTIFICATION, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_48px)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        // TODO: Revise notificationId
        notificationManager.notify(notificationId, builder.build());
    }

    public void swapToChat(){
        getActivity().startActivity(new Intent(getContext(), ChatActivity.class));
    }
}