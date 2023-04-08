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
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;
import android.widget.ProgressBar;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends FragmentActivity {
    private Fragment currentScreen;
    private NavigationBarView bottomNavigation;

    private static ContentLoadingProgressBar progressBar;
    private static int progressStep;

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

        progressBar = (ContentLoadingProgressBar) findViewById(R.id.loading_progressbar);

        DatabaseAccess.initDatabaseAccess();
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
            DatabaseAccess.runForegroundTask(setLoadingProgressBarVisible(4,0,1));
            DatabaseAccess.load_data();
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

    public static Runnable setLoadingProgressBarVisible(@NotNull int Max,@NotNull int StartWith,@NotNull int ProgressStep)
    {
        progressBar.setMax(100);
        progressBar.setMin(0);
        progressStep = 1;
        Runnable loadingProgress = new Runnable() {
            @Override
            public void run() {
                progressBar.setMax(Max);
                progressBar.setMin(StartWith);
                progressStep = ProgressStep;
                progressBar.show();
            }
        };
        return loadingProgress;
    }

    public static Runnable increaseProgress()
    {
        Runnable increase = new Runnable() {
            @Override
            public void run() {
                progressBar.incrementProgressBy(progressStep);
                if(progressBar.getProgress() >= progressBar.getMax())
                {
                    progressBar.hide();
                }
            }
        };
        return increase;
    }

    public static Runnable hideProgressBar()
    {
        Runnable hide = new Runnable() {
            @Override
            public void run() {
                progressBar.hide();
            }
        };
        return hide;
    }

}