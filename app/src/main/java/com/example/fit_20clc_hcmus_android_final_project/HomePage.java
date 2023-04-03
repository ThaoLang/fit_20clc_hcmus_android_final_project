package com.example.fit_20clc_hcmus_android_final_project;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityHomepageBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentNotificationPageBinding;
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
public class HomePage extends Fragment implements FavoriteLocationAdapter.Callbacks, PostAdapter.Callbacks { //TODO: implement other adapter callbacks as well

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private MainActivity main_activity;
    private Context context;

    LinearLayoutManager recentPostManager;
    LinearLayoutManager favoriteLocationManager;
    LinearLayoutManager nearbyLocationManager;
    ActivityHomepageBinding binding;

    private PostAdapter postAdapter;
    private FavoriteLocationAdapter favoriteLocationAdapter;
    private FavoriteLocationAdapter nearbyLocationAdapter;

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
        binding = ActivityHomepageBinding.inflate(inflater, container, false);

        //recent posts
        recentPostManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL,false);
        recentPostManager.setStackFromEnd(true);

        binding.listPost.setLayoutManager(recentPostManager);

        postAdapter = new PostAdapter(context);
        postAdapter.setListener(this);
        binding.listPost.setAdapter(postAdapter);
        binding.listPost.smoothScrollToPosition(0);

        //favorite locations
        favoriteLocationManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL,false);
        favoriteLocationManager.setStackFromEnd(true);

        binding.favoriteLocations.setLayoutManager(favoriteLocationManager);

        favoriteLocationAdapter = new FavoriteLocationAdapter(context);
        favoriteLocationAdapter.setListener(this);
        binding.favoriteLocations.setAdapter(favoriteLocationAdapter);
        binding.favoriteLocations.smoothScrollToPosition(0);

        //nearby locations
        nearbyLocationManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL,false);
        nearbyLocationManager.setStackFromEnd(true);

        binding.nearbyLocations.setLayoutManager(nearbyLocationManager);

        nearbyLocationAdapter = new FavoriteLocationAdapter(context);
        nearbyLocationAdapter.setListener(this);
        binding.nearbyLocations.setAdapter(nearbyLocationAdapter);
        binding.nearbyLocations.smoothScrollToPosition(0);

        //search btn
        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getActivity(),Search.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseFirestore fb = FirebaseFirestore.getInstance();
//        FirebaseUser currentUser = main_activity.getTheCurrentUser();
//        //user has signed in
//        if(currentUser != null)
//        {
//            fb.collection(DatabaseAcess.ACCESS_ACCOUNT_COLLECTION).document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if(task.isSuccessful() && task.getResult() != null)
//                    {
//                        User user = task.getResult().toObject(User.class);
//                        greeting.setText("Hello " + user.getName());
//                        username.setText("Full name: " + user.getName());
//                        userphone.setText("Phone: " + user.getPhone());
//                        useraddress.setText("Address: " + user.getAddress());
//                    }
//                }
//            });
//        }
  //  }

    public void swapToLocationInfo(){
        startActivity(new Intent(context, LocationInfo.class));
    }

    public void swapToPost(){ //swap locationinfo into postdetail
        startActivity(new Intent(context, LocationInfo.class));
    }

}