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
import android.widget.Toast;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.adapter.FriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentFriendBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        readFriends(this);

        adapter = new FriendAdapter(context, users);
        adapter.setListener(this);
        binding.listItem.setAdapter(adapter);

        binding.listItem.setLayoutManager(mLinearLayoutManager);
        binding.listItem.smoothScrollToPosition(0);

        users = new ArrayList<>();

        return binding.getRoot();
    }

    private void readFriends(FriendFragment friendFragment) {
        User user = chat_activity.getMainUserInfo();

        FirebaseFirestore fb = FirebaseFirestore.getInstance();

        fb.collection("account").document().get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User friend = documentSnapshot.toObject(User.class);
                    }
                });

//        reference.addValueEventListener(new ValueEventList
//        ener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                users.clear();
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    User friend = snapshot.getValue(User.class);
//                    Log.d("hehe",friend.getName());
//
////                    assert user!=null;
////                    assert friend!=null;
////
////                    if(!friend.getPhone().equals(user.getPhone())){
////                        users.add(friend);
////                    }
////
//                    users.add(friend);
//                }
//
//                adapter = new FriendAdapter(context, users);
//                adapter.setListener(friendFragment);
//                binding.listItem.setAdapter(adapter);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

    }

    public void swapToChat(){ //TODO: pass over friend user
        chat_activity.switchScreenByScreenType(0);
    }
}