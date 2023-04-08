package com.example.fit_20clc_hcmus_android_final_project;

import android.content.Context;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess{
    public static String ACCESS_ACCOUNT_COLLECTION = "account";


    private static FirebaseAuth auth;
    private static User mainUserInfo;

    private static List<Plan> plans;
    private static FirebaseFirestore firestore;

    private static Handler handler = new Handler();

    private static List<Plan> demoData = new ArrayList<Plan>();

    public static void initDatabaseAccess()
    {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
    }

    public static boolean load_data()
    {
        Thread backgroundLoadDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            mainUserInfo = task.getResult().toObject(User.class);
                            handler.post(MainActivity.increaseProgress());
                        }
                    }
                });

                //Demo data - should be removed after using to database
                LocalDate date = LocalDate.now();
                LocalDate endDate = LocalDate.of(2023,4,20);

                demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(), false, 1F));
                endDate = LocalDate.of(2023, 4, 30);

                demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(), false, 1F));
                //Demo data

                //load plans from database
                handler.post(MainActivity.hideProgressBar());
            }
        });
        backgroundLoadDataThread.start();
        return true;
    }

    public static List<Plan> getDemoData()
    {
        return demoData;
    }

    public static FirebaseUser getCurrentUser()
    {
        return auth.getCurrentUser();
    }

    public static User getMainUserInfo()
    {
        return mainUserInfo;
    }

    public static boolean setMainUserInfo(User newUserInfo)
    {
        mainUserInfo = newUserInfo;
        return true;
    }

    public static void updateUserInfo_In_Database(User newUserInfo, Runnable successfullForegroundAction, Runnable failedForegroundAction)
    {
        if(auth == null || firestore == null)
        {
            return;
        }

        firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(auth.getUid()).set(newUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful() && successfullForegroundAction != null)
                {
                    handler.post(successfullForegroundAction);
                }
                else
                {
                    if(failedForegroundAction != null)
                    {
                        handler.post(failedForegroundAction);
                    }
                }
            }
        });
    }

    public static void runForegroundTask(@NotNull Runnable task)
    {
        handler.post(task);
    }


}
