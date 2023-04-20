package com.example.fit_20clc_hcmus_android_final_project.chat_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatAdapter.Callbacks, ChatActivity.ChatCallbacks {
    private FragmentChatBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private ChatActivity chat_activity;
    private Context context;

    private LinearLayoutManager mLinearLayoutManager;
    private ChatAdapter adapter;
    private ArrayList<Chat> chatHistory;
    private String friendEmail;
    private String currentTripId = "1";

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
            chat_activity.setListener(this);

        } catch (IllegalStateException e) {
            throw new IllegalStateException("ChatActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        chatHistory = new ArrayList<>();
        readChat(this);

//        TODO: not working yet
        if (friendEmail!=null){
            Log.e("friendEmail", friendEmail);

            for (Chat c : chatHistory){
                if (c.getSenderEmail().equals(friendEmail)){
                    tagFriend(c.getSenderName());
                    Log.e("Tagged Name",c.getSenderName());
                    break;
                }
            }
        }
        // ERROR.end

        adapter = new ChatAdapter(context, chatHistory);
        adapter.setListener(this);
        binding.listItem.setAdapter(adapter);
        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.smoothScrollToPosition(0);

        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.chatTextField.getText().toString();

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
                    binding.listItem.smoothScrollToPosition(chatHistory.size()-1);

                    Log.d("user avatar URL", user.getAvatarUrl());

                    // add to database
                    fb.collection("chatHistory").document()
                            .set(chat)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("TAG", "DocumentSnapshot successfully written!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("TAG", "Error writing document", e);
                                }
                            });

                }
            }
        });

        return binding.getRoot();
    }

    private boolean isValidMessage(String input) {
        return !input.isEmpty();
    }

    private void readChat(ChatFragment chatFragment) {
        FirebaseFirestore fb = chat_activity.getFirebaseFirestore();

        fb.collection("chatHistory")
                .orderBy("sendTime", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w("TAG", "Listen failed.", error);
                    return;
                }

                if (!value.isEmpty()) {
                  chatHistory.clear();

                    for (DocumentSnapshot document : value.getDocuments()) {
                        String id = String.valueOf(document.get("tripId"));

                        Log.e("tripId", id);

                        if (id.equals("0") || id.equals(currentTripId)){
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
            }
        });

    }

    public void setFriendEmail(String email) {
        friendEmail = email;
    }

    // TODO: Notify friend when they are tagged

    public void tagFriend(String friend) {
        String tag = binding.chatTextField.getText() + "@" + friend;
        binding.chatTextField.setText(tag);
    }
}