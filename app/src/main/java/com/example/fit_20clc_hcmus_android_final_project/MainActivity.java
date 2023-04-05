package com.example.fit_20clc_hcmus_android_final_project;

import android.content.Intent;
import android.os.Bundle;


//public class MainActivity extends AppCompatActivity {
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//
//    }
//}

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity {
    private Fragment currentScreen;
    private NavigationBarView bottomNavigation;

    private int CURRENT_SELECTED_ID = 0;

    private static Integer screenType = 0;

    public static final int HOME_PAGE = 0;
    public static final int TRIPS = 1;
    public static final int NOTIFICATION = 2;
    public static final int ACCOUNT_INFO = 3;


    public static String HOME_PAGE_INIT_PARAM = "HOME_PAGE";
    public static String TRIPS_INIT_PARAM = "TRIPS";
    public static String NOTIFICATION_PAGE_INIT_PARAM = "NOTIFICATION_PAGE";
    public static String ACCOUNT_INFO_INIT_PARAM = "ACCOUNT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        //handle bottom navigation bar/////////////////////////////////////////////////
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
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

        DatabaseAccess.load_data();

        //Default: currentScreen is homepage-screen at the beginning
        if(screenType == 0)
        {
            currentScreen = HomePage.newInstance(HOME_PAGE_INIT_PARAM);
        }

        transaction.replace(R.id.main_frame,currentScreen);
        transaction.commit();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FirebaseUser user = DatabaseAccess.getCurrentUser();
        if(user == null)
        {
            startActivity(new Intent(MainActivity.this, SignIn.class));
            finish();
        }
        else
        {
            /*FirebaseFirestore fb = FirebaseFirestore.getInstance();
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
            });*/

        }
    }


    //change Fragment (screen)
    private void switchScreenBySelectMenuItem(int idItemSelected)
    {
        if(idItemSelected == CURRENT_SELECTED_ID)
        {
            return;
        }
        if(idItemSelected == R.id.bottom_nav_homepage)
        {
            screenType = 0;
            System.out.println("HOMEPAGE");
        }
        else if(idItemSelected == R.id.bottom_nav_trips)
        {
            screenType = 1;
            System.out.println("TRIPS");
        }
        else if(idItemSelected == R.id.bottom_nav_notification)
        {
            screenType = 2;
            System.out.println("NOTIFICATION");
        }
        else if(idItemSelected == R.id.bottom_nav_manage_account)
        {
            screenType = 3;
            System.out.println("MANAGE ACCOUNT");
        }
        else
        {
            System.out.println("NOTHING BE CHOSEN");
        }
        CURRENT_SELECTED_ID = idItemSelected;
        switchScreenByScreenType(screenType);
    }

    //execute to switch the screen (fragment)
    public void switchScreenByScreenType(int inputScreenType)
    {
        switch (inputScreenType)
        {
            case HOME_PAGE:
            {
                currentScreen = HomePage.newInstance(HOME_PAGE_INIT_PARAM);
                break;
            }
            case TRIPS:
            {
                currentScreen = TripsPage.newInstance(TRIPS_INIT_PARAM);
                break;
            }
            case NOTIFICATION:
            {
                currentScreen = NotificationPage.newInstance(NOTIFICATION_PAGE_INIT_PARAM);
                break;
            }
            case ACCOUNT_INFO:
            {
                currentScreen = AccountInfoPage.newInstance(ACCOUNT_INFO_INIT_PARAM);
                break;
            }
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, currentScreen);
        transaction.commit();
    }

}