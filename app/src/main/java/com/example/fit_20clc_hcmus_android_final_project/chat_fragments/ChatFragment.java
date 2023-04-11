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
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.adapter.ChatAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Chat;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatFragment extends Fragment implements ChatAdapter.Callbacks, ChatActivity.ChatCallbacks {
    private FragmentChatBinding binding;

    private static final String ARG_PARAM1 = "friendPhone";
    private String param1;
    private ChatActivity chat_activity;
    private Context context;

    LinearLayoutManager mLinearLayoutManager;
    ChatAdapter adapter;
    ArrayList<Chat> chatHistory;
    private static String friendPhone;

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
            param1 = getArguments().getString(ARG_PARAM1);
        }

        try {
            context = getActivity();
            chat_activity = (ChatActivity) getActivity();
//            chat_activity.setListener(this);

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

        adapter = new ChatAdapter(context, chatHistory);
        adapter.setListener(this);
        binding.listItem.setAdapter(adapter);
        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.smoothScrollToPosition(0);

        // TODO: Add new message to database
        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.chatTextField.getText().toString();

                binding.chatTextField.setText("");

                if (isValidMessage(input)) {
//                    adapter.addMessage(input); // only temporary local data
                    User user = chat_activity.getMainUserInfo();
                    FirebaseFirestore fb = chat_activity.getFirebaseFirestore();

                    int id = 0;
                    String message = input;
                    int sendTime = chatHistory.size(); // TODO: fix pls
                    String senderName = user.getName();
                    String senderPhone = user.getPhone();

                    Chat chat = new Chat(id, message, sendTime, senderName, senderPhone);
                    chatHistory.add(chat);
                    adapter.notifyDataSetChanged();
                    binding.listItem.smoothScrollToPosition(chatHistory.size()-1);

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
        if (input.isEmpty())
            return false;
        return true;
    }

    private void readChat(ChatFragment chatFragment) {
        // TODO: read chat from database
        FirebaseFirestore fb = chat_activity.getFirebaseFirestore();

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

                                    int id = Integer.parseInt(document.get("tripId").toString());
                                    String message = document.get("message").toString();
                                    int sendTime = Integer.parseInt(document.get("sendTime").toString());
                                    String senderName = document.get("senderName").toString();
                                    String senderPhone = document.get("senderPhone").toString();

                                    chat = new Chat(id, message, sendTime, senderName, senderPhone);
                                    chatHistory.add(chat);

                                    Log.e("message", message);
                                }

                                for (int i=0;i<chatHistory.size();i++){
                                    Log.e("MESSAGE", chatHistory.get(i).getMessage());
//                                    adapter.addMessage(chatHistory.get(i));
                                }
//                                adapter = new ChatAdapter(context);
                                adapter = new ChatAdapter(context, chatHistory);
                                adapter.setListener(chatFragment);
                                binding.listItem.setAdapter(adapter);
                            }
                        } else {
                            // show "Plan has no chat yet"
                        }
                    }
                });

//        fb.collection("chatHistory").addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.w("TAG", "Listen failed.", error);
//                    return;
//                }
//
//                if (!value.isEmpty()) {
////                  chatHistory.clear();
//
//                    for (DocumentSnapshot document : value.getDocuments()) {
//                        Chat chat;
//
//                        Log.e("tripId", document.get("tripId").toString());
//
//                        int id = Integer.parseInt(document.get("tripId").toString());
//                        String message = document.get("message").toString();
//                        String sendTime = document.get("sendTime").toString();
//                        String senderName = document.get("senderName").toString();
//                        String senderPhone = document.get("senderPhone").toString();
//
//                        chat = new Chat(id, message, sendTime, senderName, senderPhone);
//                        chatHistory.add(chat);
//                    }
//                    adapter = new ChatAdapter(context, chatHistory);
//                    adapter.setListener(chatFragment);
//                    binding.listItem.setAdapter(adapter);
//                } else {
//                    Log.d("TAG", "Current data: null");
//                }
//            }
//
//        });

    }

    public void swapToFriend() { //TODO: pass over friend user
        chat_activity.switchScreenByScreenType(1, null);
    }

    public void setFriendPhone(String phone) {
        Log.e("friendphone1", phone);
        friendPhone = phone;
    }

    public void tagFriend(String friend) {
        binding.chatTextField.setText(binding.chatTextField.getText() + "@" + friend);
    }
}