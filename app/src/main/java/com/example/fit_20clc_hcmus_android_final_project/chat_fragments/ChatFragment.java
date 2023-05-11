package com.example.fit_20clc_hcmus_android_final_project.chat_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.adapter.ChatAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Chat;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentChatBinding;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatAdapter.Callbacks {
    private FragmentChatBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private ChatActivity chat_activity;
    private Context context;
    private FirebaseFirestore fb;

    private LinearLayoutManager mLinearLayoutManager;
    private ChatAdapter adapter;
    private ArrayList<Chat> chatHistory;
    private String currentTripId;

    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String param1) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTripId = getArguments().getString(ARG_PARAM1);
        }

        try {
            context = getActivity();
            chat_activity = (ChatActivity) getActivity();
            fb = chat_activity.getFirebaseFirestore();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("ChatActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        getTripName();

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        chatHistory = new ArrayList<>();
        readChat(this);

        adapter = new ChatAdapter(context, chatHistory);
        adapter.setListener(this);
        binding.listItem.setAdapter(adapter);
        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.smoothScrollToPosition(0);

        binding.chatButton.setOnClickListener(view -> {
            String input = String.valueOf(binding.chatTextField.getText());

            binding.chatTextField.setText("");

            if (isValidMessage(input)) {
                User user = chat_activity.getMainUserInfo();
                FirebaseFirestore fb = chat_activity.getFirebaseFirestore();

                String id = currentTripId;
                int sendTime = chatHistory.size();
                String senderName = user.getName();
                String senderEmail = user.getUserEmail();
                String senderAvatarURL = user.getAvatarUrl();

                Chat chat = new Chat(id, input, sendTime, senderName, senderEmail, senderAvatarURL);
                chatHistory.add(chat);
                adapter.notifyDataSetChanged();
                binding.listItem.smoothScrollToPosition(chatHistory.size() - 1);

                // add to database
                fb.collection("chatHistory").document()
                        .set(chat)
                        .addOnSuccessListener(aVoid -> Log.d("TAG", "DocumentSnapshot successfully written!"))
                        .addOnFailureListener(e -> Log.w("TAG", "Error writing document", e));

            }
        });

        return binding.getRoot();
    }

    private boolean isValidMessage(String input) {
        return !input.isEmpty();
    }

    private void getTripName() {
        fb.collection("plans")
                .whereEqualTo("planId", currentTripId).addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    if (!value.isEmpty()) {
                        for (DocumentSnapshot document : value.getDocuments()) {
                            String currentTripName = String.valueOf(document.get("name"));
                            binding.textView.setText(currentTripName);
                        }
                    }
                });
    }

    private void readChat(ChatFragment chatFragment) {
        fb.collection("chatHistory")
                .orderBy("sendTime", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.w("TAG", "Listen failed.", error);
                        return;
                    }

                    if (!value.isEmpty()) {
                        chatHistory.clear();

                        for (DocumentSnapshot document : value.getDocuments()) {
                            String id = String.valueOf(document.get("tripId"));

                            if (id.equals("0") || id.equals(currentTripId)) {
                                Chat chat;
                                String message = String.valueOf(document.get("message"));
                                int sendTime = Integer.parseInt(String.valueOf(document.get("sendTime")));
                                String senderName = String.valueOf(document.get("senderName"));
                                String senderEmail = String.valueOf(document.get("senderEmail"));
                                String senderAvatarURL = String.valueOf(document.get("senderAvatarURL"));

                                Log.d("user avatar URL", senderAvatarURL);

                                chat = new Chat(id, message, sendTime, senderName, senderEmail, senderAvatarURL);
                                chatHistory.add(chat);

                                Log.e("message", message);
                            }
                        }
                        adapter = new ChatAdapter(context, chatHistory);
                        adapter.setListener(chatFragment);
                        binding.listItem.setAdapter(adapter);
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                });
    }

    // TODO: Notify friend when they are tagged

    public void tagFriend(String friend) {
        String tag = binding.chatTextField.getText() + "@" + friend;
        binding.chatTextField.setText(tag);
    }
}