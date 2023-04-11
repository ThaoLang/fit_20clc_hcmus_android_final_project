package com.example.fit_20clc_hcmus_android_final_project;

import android.accessibilityservice.GestureDescription;
import android.net.Uri;
import android.os.Handler;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.grpc.Context;
import io.grpc.Metadata;

public class DatabaseAccess{
    public static String ACCESS_ACCOUNT_COLLECTION = "account";
    public static String ACCESS_PLANS_COLLECTION = "plans";
    public static String ACCESS_COMMENT_SET_COLLECTION = "commentSets";

    public static String ACCESS_PLANS_STORAGE = "plans/";

    private static FirebaseAuth auth;
    private static User mainUserInfo;

    private static List<Plan> plans;
    private static FirebaseFirestore firestore;

    private static FirebaseStorage firebaseStorage;

    private static Handler handler = new Handler();

    private static List<Plan> demoData = new ArrayList<Plan>();

    public static void initDatabaseAccess()
    {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
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
                /*LocalDate date = LocalDate.now();
                LocalDate endDate = LocalDate.of(2023,4,20);

                demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(), false, 1F));
                endDate = LocalDate.of(2023, 4, 30);

                demoData.add(new Plan("Hoi An Tour", "None", date.toString(), endDate.toString(), false, 1F));
                //Demo data*/

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

    public static void addNewPlan(@NotNull Plan newPlan, Runnable successfulTask, Runnable failureTask)
    {
        Thread backgroundAddition = new Thread(new Runnable() {
            @Override
            public void run() {
                //add and update data in the cloud database
                firestore.runTransaction(new Transaction.Function<String>() {
                    final DocumentReference accountDoc = firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(auth.getCurrentUser().getUid());
                    final DocumentReference plansDoc = firestore.collection(ACCESS_PLANS_COLLECTION).document();
                    final DocumentReference commentSetDoc = firestore.collection(ACCESS_COMMENT_SET_COLLECTION).document();
                    @Nullable
                    @Override
                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        String newPlanId = plansDoc.getId();
                        newPlan.setPlanId(newPlanId);
                        transaction.set(plansDoc, newPlan);
                        transaction.update(accountDoc, "plans", FieldValue.arrayUnion(newPlanId));
                        return newPlanId;
                    }
                    }).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String newPlanIdCreated) {
                            //update local data
                            System.out.println("<<NewPlanId created: >>" + newPlanIdCreated);
                            mainUserInfo.addNewPlan(newPlanIdCreated);

                            //get uri of the selected image in local storage, which was selected by the user as the result returned by CreatePlan
                            Uri imageNeedToUpload = Uri.parse(newPlan.getImageLink());
                            String childName = newPlanIdCreated+".jpg";
                            StorageReference plansImageReference = firebaseStorage.getReference().child(ACCESS_PLANS_STORAGE + childName);
                            System.out.println("imageLocalUri" + imageNeedToUpload);
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg").build();

                            UploadTask uploadTask = (UploadTask) plansImageReference.putFile(imageNeedToUpload, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUrl = String.valueOf(plansImageReference.getDownloadUrl());

                                }
                            });

                            if(successfulTask != null)
                            {
                                runForegroundTask(successfulTask);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Exception: " + e);
                            if(failureTask != null)
                            {
                                runForegroundTask(failureTask);
                            }
                        }
                    });

            }
        });
        backgroundAddition.start();
    }


}
