package com.example.fit_20clc_hcmus_android_final_project;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.fit_20clc_hcmus_android_final_project.chat_fragments.ChatFragment;
import com.example.fit_20clc_hcmus_android_final_project.chat_fragments.FriendFragment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends FragmentActivity {
    static boolean active;
    private static FirebaseAuth mAuth = null;
    private Fragment currentScreen;
    private NavigationBarView bottomNavigation;

    private int CURRENT_SELECTED_ID = 0;

    private static Integer screenType = 0;

    public static final int CHAT = 0;
    public static final int FRIEND = 1;
    public static final int RETURN = 2;

    private User mainUserInfo;
    private String currentTripId;
    private ChatActivity.ChatCallbacks listener;

    private  FragmentTransaction transaction;

    public void setListener(ChatActivity.ChatCallbacks listener) {
        this.listener = listener;
    }

    public interface ChatCallbacks {
        void setFriendEmail(String email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        transaction = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getBundleExtra("CHAT");
        currentTripId = String.valueOf(bundle.get("PlanId"));
        Log.e("PlanId", currentTripId);

        active = true;
        bottomNavigation.setOnItemSelectedListener(item -> {
            int idItemSelected = item.getItemId();
            switchScreenBySelectMenuItem(idItemSelected);
            //return true if we want to the item be selected (be colored). Else, return false
            return true;
        });

        currentScreen = ChatFragment.newInstance(currentTripId);

        transaction.replace(R.id.main_frame,currentScreen);
        transaction.commit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!active)
        {
            transaction.detach(currentScreen);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        active = true;
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(ChatActivity.this, SignIn.class));
            getParent().onBackPressed();
        }
        else
        {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            fb.collection(DatabaseAccess.ACCESS_ACCOUNT_COLLECTION).document(user.getUid()).get().addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    mainUserInfo =  task.getResult().toObject(User.class);
                }
                else
                {
                    mainUserInfo = null;
                }
            });
        }
    }

    private void switchScreenBySelectMenuItem(int idItemSelected)
    {
        if(idItemSelected == CURRENT_SELECTED_ID)
        {
            return;
        }
        if(idItemSelected == R.id.bottom_nav_chat)
        {
            screenType = 0;
            System.out.println("CHAT");
        }
        else if(idItemSelected == R.id.bottom_nav_friend)
        {
            screenType = 1;
            System.out.println("FRIEND");
        }
        else if(idItemSelected == R.id.bottom_nav_return)
        {
            screenType = 2;
            System.out.println("RETURN");
        }
        else{
            System.out.println("NOTHING WAS CHOSEN");
        }
        CURRENT_SELECTED_ID = idItemSelected;
        switchScreenByScreenType(screenType, null);
    }

    public void switchScreenByScreenType(int inputScreenType, String email)
    {
        switch (inputScreenType)
        {
            case CHAT:
            {
                currentScreen = ChatFragment.newInstance(currentTripId);
                if (email!=null){
                    Log.e("email", email);
                    listener.setFriendEmail(email);
                }

                bottomNavigation.setSelectedItemId(R.id.bottom_nav_chat);
                break;
            }
            case FRIEND:
            {
                currentScreen = FriendFragment.newInstance(currentTripId);
                bottomNavigation.setSelectedItemId(R.id.bottom_nav_friend);
                break;
            }
            case RETURN:
            {
                active = false;
                finish();
                break;
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, currentScreen);
        transaction.commit();
    }

    public User getMainUserInfo()
    {
        return mainUserInfo;
    }
    public FirebaseFirestore getFirebaseFirestore()
    {
        return FirebaseFirestore.getInstance();
    }
}