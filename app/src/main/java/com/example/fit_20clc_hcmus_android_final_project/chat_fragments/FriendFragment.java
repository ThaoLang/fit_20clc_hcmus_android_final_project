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
import com.example.fit_20clc_hcmus_android_final_project.adapter.FriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentFriendBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FriendFragment extends Fragment implements FriendAdapter.Callbacks {

    private FragmentFriendBinding binding;

    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private ChatActivity chat_activity;
    private Context context;

    LinearLayoutManager mLinearLayoutManager;

    FriendAdapter adapter;
    ArrayList<User> users;

    User user;
    FirebaseFirestore fb;

    public FriendFragment() {
        // Required empty public constructor
    }

    public static FriendFragment newInstance(String param1) {
        FriendFragment fragment = new FriendFragment();
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
            chat_activity = (ChatActivity) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("ChatActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFriendBinding.inflate(inflater, container, false);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);

        users = new ArrayList<>();
        readFriends(this);

        adapter = new FriendAdapter(context, users);
        adapter.setListener(this);
        binding.listItem.setAdapter(adapter);

        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.smoothScrollToPosition(0);

        return binding.getRoot();
    }

    private void readFriends(FriendFragment friendFragment) {
        user = chat_activity.getMainUserInfo();
        fb = chat_activity.getFirebaseFirestore();

        Log.e("userphoneff",user.getPhone());

        fb.collection("account")
                .whereNotEqualTo("phone", user.getPhone())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()){
                                for (DocumentSnapshot document : querySnapshot.getDocuments()){
                                    User friend;

                                    Log.e("name",document.get("name").toString());

                                    String name = document.get("name").toString();
                                    String phone = document.get("phone").toString();
                                    String address = document.get("address").toString();
                                    String email = document.get("email").toString();
                                    friend = new User(name, email, phone, address,null,null,null);
                                    users.add(friend);
                                }
                                    binding.textView.setText("Friend");
                                    adapter = new FriendAdapter(context, users);
                                    adapter.setListener(friendFragment);
                                    binding.listItem.setAdapter(adapter);
                            }
                        } else{
                            // show "Plan has no friends"
                        }
                    }
                });
    }

    public void swapToChat(String phone){
        chat_activity.switchScreenByScreenType(0, phone);
    }
}