package com.example.fit_20clc_hcmus_android_final_project;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.fit_20clc_hcmus_android_final_project.chat_fragments.ChatFragment;
import com.example.fit_20clc_hcmus_android_final_project.chat_fragments.FriendFragment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChatActivity extends FragmentActivity {

    private static FirebaseAuth mAuth = null;
    private Fragment currentScreen;
    private NavigationBarView bottomNavigation;

    private int CURRENT_SELECTED_ID = 0;

    private static Integer screenType = 0;

    public static final int CHAT = 0;
    public static final int FRIEND = 1;
    public static final int RETURN = 2;

    public static String CHAT_INIT_PARAM = "CHAT";
    public static String FRIEND_INIT_PARAM = "FRIEND";

    private User mainUserInfo;

    private ChatActivity.ChatCallbacks listener;

    public void setListener(ChatActivity.ChatCallbacks listener) {
        this.listener = listener;
    }

    public interface ChatCallbacks {
        void setFriendPhone(String phone);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();

        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                System.out.println("Item clicked: " + item.getItemId());
                int idItemSelected = item.getItemId();
                switchScreenBySelectMenuItem(idItemSelected);
                //return true if we want to the item be selected (be colored). Else, return false
                return true;
            }
        });

        //Default: currentScreen is homepage-screen at the beginning
        if(screenType == 0)
        {
            currentScreen = ChatFragment.newInstance(CHAT_INIT_PARAM);
        }

        transaction.replace(R.id.main_frame,currentScreen);
        transaction.commit();

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(ChatActivity.this, SignIn.class));
            finish();
        }
        else
        {
            FirebaseFirestore fb = FirebaseFirestore.getInstance();
            fb.collection(DatabaseAccess.ACCESS_ACCOUNT_COLLECTION).document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        mainUserInfo =  task.getResult().toObject(User.class);
                    }
                    else
                    {
                        mainUserInfo = null;
                    }
                }
            });
        }
    }

    public FirebaseUser getTheCurrentUser()
    {
        return mAuth.getCurrentUser();
    }

    //change Fragment (screen)
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

    //execute to switch the screen (fragment)
    public void switchScreenByScreenType(int inputScreenType, String phone)
    {
        switch (inputScreenType)
        {
            case CHAT:
            {
                currentScreen = ChatFragment.newInstance(CHAT_INIT_PARAM);
                if (phone!=null){
                    Log.e("phone", phone);
                    listener.setFriendPhone(phone);
                }

                bottomNavigation.setSelectedItemId(R.id.bottom_nav_chat);
                break;
            }
            case FRIEND:
            {
                currentScreen = FriendFragment.newInstance(FRIEND_INIT_PARAM);
                bottomNavigation.setSelectedItemId(R.id.bottom_nav_friend);
                break;
            }
            case RETURN:
            {
//                MainActivity.switchScreenByScreenType(3);
//                startActivity(new Intent(Intent.EXTRA_RETURN_RESULT));
//                this.finish();
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