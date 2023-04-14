package com.example.fit_20clc_hcmus_android_final_project;

//import static com.example.fit_20clc_hcmus_android_final_project.MainActivity.INTENT_EXTRA_NOTIFICATION;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.adapter.CustomNotificationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Chat;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationPage extends Fragment implements CustomNotificationAdapter.Callbacks {
    private FragmentNotificationPageBinding binding;

    // TODO: Revise notificationId
    final private String CHANNEL_ID = "NOTIFICATION_CH_ID";
    final int notificationId = 1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private MainActivity main_activity;
    private Context context;
    private CustomNotificationAdapter adapter;
    private ArrayList<String> notificationList;

    LinearLayoutManager mLinearLayoutManager;

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
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNotificationPageBinding.inflate(inflater, container, false);
//        return binding.getRoot();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        notificationList = new ArrayList<>();
        //TODO: create notification dataset and update with this function
        getNotificationInfo(this);

        adapter = new CustomNotificationAdapter(context, notificationList);
        adapter.setListener(this);

        binding.listItem.setLayoutManager(mLinearLayoutManager);

        binding.listItem.setAdapter(adapter);
        binding.listItem.smoothScrollToPosition(0);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = DatabaseAccess.getCurrentUser();
        //user has signed in
        if(currentUser != null) {
            createNotificationChannel();
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager)this.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendNotification() {
        // TODO: CONSIDER: When click on notification, swap to trips using public method and var
//         main_activity.switchScreenByScreenType(main_activity.TRIPS);

        // TODO: Revise intent to send to the right activity / plan when click on notification
        Intent intent = new Intent(context, ChatActivity.class); //supposedly from notification to plan detail?
//        intent.putExtra(INTENT_EXTRA_NOTIFICATION, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_48px)
                .setContentTitle(getString(R.string.channel_name))
                .setContentText(getString(R.string.channel_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireActivity());
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
//        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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

    private void getNotificationInfo(NotificationPage notificationPage){
        //TODO: firebasefirestore
        FirebaseFirestore fb = main_activity.getFirebaseFirestore();
//        FirebaseFirestore fb = DatabaseAccess.getFirestore();

        fb.collection("chatHistory")
                .orderBy("sendTime", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Chat chat;

                                    Log.e("tripId", document.get("tripId").toString());

//                                    int id = Integer.parseInt(document.get("tripId").toString());
                                    String message = document.get("message").toString();
//                                    int sendTime = Integer.parseInt(document.get("sendTime").toString());
//                                    String senderName = document.get("senderName").toString();
//                                    String senderPhone = document.get("senderPhone").toString();

//                                    chat = new Chat(id, message, sendTime, senderName, senderPhone);
                                    notificationList.add(message);

                                    Log.e("message", message);
                                }

                                adapter = new CustomNotificationAdapter(context, notificationList);
                                adapter.setListener(notificationPage);
                                binding.listItem.setAdapter(adapter);
                            }
                        } else {
                            // no notification
                        }
                    }
                });
    }

    public void swapToTrips(){
        main_activity.switchScreenByScreenType(main_activity.TRIPS);
    }
}