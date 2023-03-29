package com.example.fit_20clc_hcmus_android_final_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPageBinding;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

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
//    private int selected_position=0;

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

        adapter = new CustomNotificationAdapter(context);
        adapter.setListener(this);

        binding.listItem.setLayoutManager(mLinearLayoutManager);

        binding.listItem.setAdapter(adapter);
        binding.listItem.smoothScrollToPosition(0);

        //TODO: create notification dataset and update with this function
        getNotificationInfo();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = main_activity.getTheCurrentUser();
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
        // main_activity.switchScreenByScreenType(main_activity.TRIPS);

        // TODO: Revise intent to send to the right activity / plan when click on notification
        Intent intent = new Intent(requireActivity(), SignIn.class); //supposedly from notification to main activity?
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(requireActivity(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireActivity(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_48px)
                .setContentTitle(getString(R.string.channel_name))
                .setContentText(getString(R.string.channel_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireActivity());

        // notificationId is a unique int for each notification that you must define
        if (ActivityCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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

    private void getNotificationInfo(){
        FirebaseDatabase
                .getInstance()
                .getReference("Account")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        FirebaseUser user = main_activity.getTheCurrentUser();
                        User mainUserInfo = main_activity.getMainUserInfo();
                        if(mainUserInfo != null)
                        {
                            Toast.makeText(context, "Hello World", Toast.LENGTH_SHORT).show();

                            // TODO: get user notifications
                            // TODO: create notification class array and import data

                            // val notification = snapshot.getValue(Notification.class); ? https://youtu.be/DQLZxt1qFdk?t=1120
//                            username.setText(mainUserInfo.getName());
//                            userbio.setText(mainUserInfo.getBio());
//                            useremail.setText(user.getEmail());
//                            useraddress.setText(mainUserInfo.getAddress());
//                            userphone.setText(mainUserInfo.getPhone());

                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}