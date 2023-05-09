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
import android.widget.Toast;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends FragmentActivity {
    private Fragment currentScreen;
    private NavigationBarView bottomNavigation;

    private static ContentLoadingProgressBar progressBar;
    private static int progressStep;

    private static boolean isRunning;

    private int CURRENT_SELECTED_ID = 0;

    private static Integer screenType = 0;

    public static final int HOME_PAGE = 0;
    public static final int TRIPS = 1;
    public static final int NOTIFICATION = 2;
    public static final int ACCOUNT_INFO = 3;
    public static final int CREATE_TRIP = 4;


    public static String HOME_PAGE_INIT_PARAM = "HOME_PAGE";
    public static String TRIPS_INIT_PARAM = "TRIPS";
    public static String NOTIFICATION_PAGE_INIT_PARAM = "NOTIFICATION_PAGE";
    public static String ACCOUNT_INFO_INIT_PARAM = "ACCOUNT_INFO";

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        transaction = getSupportFragmentManager().beginTransaction();
        progressBar = (ContentLoadingProgressBar) findViewById(R.id.loading_progressbar);

        isRunning  = true;

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
        DatabaseAccess.initDatabaseAccess();
        if(DatabaseAccess.getCurrentUser() != null)
        {
            DatabaseAccess.runForegroundTask(setLoadingProgressBarVisible(4,0,1));
            DatabaseAccess.load_data();
            if(screenType == 0)
            {
                currentScreen = HomePage.newInstance(HOME_PAGE_INIT_PARAM);
            }
//            if (DatabaseAccess.getMainUserInfo()!=null){
//
//            }

            transaction.replace(R.id.main_frame,currentScreen);
            transaction.commit();

        }
        //Default: currentScreen is homepage-screen at the beginning

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

//            else if(screenType == 1)
//            {
//                currentScreen = TripsPage.newInstance(TRIPS_INIT_PARAM);
//            }
//            else if(screenType == 2)
//            {
//                currentScreen = NotificationPage.newInstance(NOTIFICATION_PAGE_INIT_PARAM);
//            }
//            else if(screenType == 3)
//            {
//                currentScreen = AccountInfoPage.newInstance(ACCOUNT_INFO_INIT_PARAM);
//            }
//
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isRunning == false || DatabaseAccess.getCurrentUser() == null)
        {
            transaction.detach(currentScreen);
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
        else if(idItemSelected == R.id.bottom_nav_create_trip)
        {
            screenType = 4;
            System.out.println("CREATE TRIP");
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
            case CREATE_TRIP:
            {
                Intent intent = new Intent(MainActivity.this, CreatePlan.class);
                intent.putExtra("SETTING_MODE", TripsPage.CREATE_PLAN_MODE);
                switchScreenByScreenType(1);
                bottomNavigation.setSelectedItemId(R.id.bottom_nav_trips);
                startActivity(intent);

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

    public static int getImplementedProgressStep()
    {
        return progressStep;
    }
    public static int getCurrentProgressStepOfProgressBar()
    {
        return progressBar.getProgress();
    }


    public Runnable toast(@NotNull String message)
    {
        Runnable toastTask = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        };
        return toastTask;
    }

    public static int getProgressMax()
    {
        return progressBar.getMax();
    }

    public Runnable startSpecificActivity(Intent intent)
    {
        Runnable foregroundTask = new Runnable()
        {
            @Override
            public void run()
            {
                startActivity(intent);
            }
        };
        return foregroundTask;
    }

    public void setIsRunning(@NotNull boolean status)
    {
        isRunning = status;
    }

}