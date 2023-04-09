package com.example.fit_20clc_hcmus_android_final_project.chat_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.adapter.ChatAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatFragment extends Fragment implements ChatAdapter.Callbacks, ChatActivity.ChatCallbacks {
    private FragmentChatBinding binding;

    private static final String ARG_PARAM1 = "friendPhone";
    private String param1;
    private ChatActivity chat_activity;
    private Context context;

    LinearLayoutManager mLinearLayoutManager;
    ChatAdapter adapter;
    ArrayList<User> users;
    private static String friendPhone;

    User user;
    FirebaseFirestore fb;

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

        users = new ArrayList<>();
        readFriends(this);
//        readChat(this);
//        users = chat_activity.readFriends();

        adapter = new ChatAdapter(context);
        adapter.setListener(this);

        binding.listItem.setLayoutManager(mLinearLayoutManager);

        binding.listItem.setAdapter(adapter);
//        binding.listItem.smoothScrollToPosition(0);

        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = binding.chatTextField.getText().toString();
                if (isValidMessage(input)) {
                    adapter.addMessage(input); // only temporary local data
                    adapter.notifyDataSetChanged();
                }
            }
        });

        return binding.getRoot();
    }

    private boolean isValidMessage(String input) {
        if (input.equals("") || input.isEmpty())
            return false;
        return true;
    }

    private void readFriends(ChatFragment chatFragment) {
        user = chat_activity.getMainUserInfo();

        if (user==null) {
            Log.e("error", "null user");
            return;
        }

        fb = chat_activity.getFirebaseFirestore();

        Log.e("userphonecf", user.getPhone());

        fb.collection("account")
                .whereNotEqualTo("phone", user.getPhone())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    User friend;

                                    Log.e("name", document.get("name").toString());

                                    String name = document.get("name").toString();
                                    String phone = document.get("phone").toString();
                                    String address = document.get("address").toString();

                                    friend = new User(name, phone, address, null, null, null);
                                    users.add(friend);
                                }
//                                adapter = new ChatAdapter(context, users);
                                adapter = new ChatAdapter(context);
                                adapter.setListener(chatFragment);
                                binding.listItem.setAdapter(adapter);

                                for (User friend : users) {
                                    if (friend.getPhone() == friendPhone) {
                                        Log.e("friendphone", friend.getPhone());
                                        tagFriend(friend.getName());
                                    }
                                }
                            }
                        } else {
                            // show "Plan has no friends"
                        }
                    }
                });
    }

    private void readChat(ChatFragment chatFragment) {
        // TODO: read chat from database
    }

    public void swapToFriend() { //TODO: pass over friend user
        chat_activity.switchScreenByScreenType(1, null);
    }

    public void setFriendPhone(String phone){
        Log.e("friendphone1", phone);
        friendPhone = phone;
    }

    public void tagFriend(String friend) {
        binding.chatTextField.setText(binding.chatTextField.getText() + "@" + friend);
    }
}