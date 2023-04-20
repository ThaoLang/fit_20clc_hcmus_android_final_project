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
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationChatBinding;
import com.example.fit_20clc_hcmus_android_final_project.service.NotificationService;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationChatFragment extends Fragment implements CustomNotificationAdapter.Callbacks {
    private MainActivity main_activity;
    private Context context;
    private FirebaseUser currentUser;

    private FragmentNotificationChatBinding binding;
    private CustomNotificationAdapter chatAdapter;
    private ArrayList<Notification> notificationChatList;
    LinearLayoutManager mLinearLayoutManager;
    NotificationChatFragment notificationPage;
    NotificationService notificationService;

    public NotificationChatFragment() {
        notificationPage = this;
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
        binding = FragmentNotificationChatBinding.inflate(inflater, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(false);
        notificationChatList = new ArrayList<>();

        FirebaseFirestore fb = DatabaseAccess.getFirestore();

        fb.collection("chatHistory")
                .orderBy("sendTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("TAG", "Listen failed.", error);
                            return;
                        }

                        if (!querySnapshot.isEmpty()) {
                            notificationChatList.clear();

                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String senderEmail = String.valueOf(document.get("senderEmail"));
                                if (!senderEmail.equals(currentUser.getEmail())) {
                                    String id = String.valueOf(document.get("tripId"));

                                    Log.e("tripId", id);

                                    // TODO: implement below
//                                    if (!id.equals("0") && id.equals(<< tripId of trips that user is in >>)){
                                    if (!id.equals("0")){
                                        Notification notification;
                                        String message = document.get("message").toString();
                                        String senderName = document.get("senderName").toString();

                                        notification = new Notification(senderName, message);
                                        notificationChatList.add(notification);

                                        Log.e("message", message);
                                    }
                                }
                            }

                            chatAdapter = new CustomNotificationAdapter(context, notificationChatList);
                            chatAdapter.setListener(notificationPage);
                            binding.listItem.setAdapter(chatAdapter);
                            binding.listItem.smoothScrollToPosition(0);

                            // TODO: Revise intent to send to the right activity / plan when click on notification
                            Intent intent = new Intent(context, ChatActivity.class); //supposedly from notification to plan detail?
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                            if (notificationChatList.size()>0) {
                                String latestTitle = notificationChatList.get(notificationChatList.size() - 1).getTitle();
                                String latestContent = notificationChatList.get(notificationChatList.size() - 1).getContent();
                                notificationService.sendNotification(latestTitle, latestContent, pendingIntent);
                            }
                        } else {
                            Log.d("TAG", "Current data: null");
                        }
                    }
                });
        chatAdapter = new CustomNotificationAdapter(context, notificationChatList);
        chatAdapter.setListener(this);
        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.setAdapter(chatAdapter);
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
        Intent intent = new Intent(context, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Bundle bundle = new Bundle();
        bundle.putString("PlanId", tripId);
        intent.putExtra("CHAT", bundle);

        getActivity().startActivity(intent);
    }
}