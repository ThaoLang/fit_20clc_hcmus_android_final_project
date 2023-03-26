package com.example.fit_20clc_hcmus_android_final_project;

import androidx.appcompat.app.AppCompatActivity;

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

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class MainActivity extends FragmentActivity {

    private static FirebaseAuth mAuth = null;
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
    public static String NOTIFICATION_INIT_PARAM = "NOTIFICATION_INIT_PARAM";
    public static String ACCOUNT_INFO_INIT_PARAM = "ACCOUNT_INFO";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        mAuth = FirebaseAuth.getInstance();

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
        if(mAuth.getCurrentUser() == null)
        {
            startActivity(new Intent(MainActivity.this, SignIn.class));
            finish();
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
    private void switchScreenByScreenType(int inputScreenType)
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
                //TODO: code
                break;
            }
            case NOTIFICATION:
            {
                //TODO: code
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