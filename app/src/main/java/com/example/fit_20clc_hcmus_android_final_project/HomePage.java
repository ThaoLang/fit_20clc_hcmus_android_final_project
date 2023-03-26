package com.example.fit_20clc_hcmus_android_final_project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomePage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomePage extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private MainActivity main_activity;
    private Context context;

    private TextView greeting, username, userphone, useraddress;



    public HomePage() {
        // Required empty public constructor
    }

    public static HomePage newInstance(String param1) {
        HomePage fragment = new HomePage();
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
        }
        catch (IllegalStateException e)
        {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.activity_homepage, container, false);
        View homepage = inflater.inflate(R.layout.activity_homepage, null);
        //connect to the layout
        greeting = (TextView) homepage.findViewById(R.id.homepage_greeting);
        username = (TextView) homepage.findViewById(R.id.homepage_username);
        userphone = (TextView) homepage.findViewById(R.id.homepage_userphone);
        useraddress = (TextView) homepage.findViewById(R.id.homepage_useraddress);

        return homepage;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = main_activity.getTheCurrentUser();
        //user has signed in
        if(currentUser != null)
        {
            fb.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        User user = task.getResult().toObject(User.class);
                        greeting.setText("Hello " + user.getName());
                        username.setText("Full name: " + user.getName());
                        userphone.setText("Phone: " + user.getPhone());
                        useraddress.setText("Address: " + user.getAddress());
                    }
                }
            });
        }
    }

}