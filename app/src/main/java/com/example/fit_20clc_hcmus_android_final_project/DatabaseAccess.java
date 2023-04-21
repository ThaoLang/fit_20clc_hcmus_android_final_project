package com.example.fit_20clc_hcmus_android_final_project;

import android.accessibilityservice.GestureDescription;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.grpc.Context;
import io.grpc.Metadata;

import com.google.firebase.firestore.FieldValue;


public class DatabaseAccess{
    public static String[] default_avatar_url={
            "https://helios-i.mashable.com/imagery/articles/06zoscMHTZxU5KEFx8SRyDg/hero-image.fill.size_1200x1200.v1630023012.jpg",
            "https://static.vecteezy.com/system/resources/previews/000/209/190/non_2x/road-trip-illustration-vector.jpg",
            "https://static.vecteezy.com/system/resources/thumbnails/007/922/503/small_2x/family-vacation-road-trip-background-free-vector.jpg",
            "https://st2.depositphotos.com/1830693/10664/v/950/depositphotos_106645944-stock-illustration-road-trip-flat-design-icon.jpg",
            "https://thumbs.dreamstime.com/b/travelling-car-view-back-luggage-background-roads-trees-sky-clouds-vector-illustration-cartoon-176081268.jpg",
            "https://img.freepik.com/premium-vector/flat-travel-background_23-2148048061.jpg?w=2000",
            "https://previews.123rf.com/images/pollygrimm/pollygrimm1712/pollygrimm171200014/91507576-happy-family-on-a-car-trip-family-summer-vacation-vector-colorful-illustration-in-flat-style-image.jpg",
            "https://thumbs.dreamstime.com/b/car-travel-vintage-flat-design-vector-122216325.jpg",
            "https://st2.depositphotos.com/1001380/8150/v/450/depositphotos_81508814-stock-illustration-travel-by-car.jpg",
            "https://media.istockphoto.com/id/1214983915/vector/cute-couple-travel-by-car.jpg?s=612x612&w=0&k=20&c=dRZlTL7HpCPddHNC3OJLLdjiHKUgVVqZ1Zk7lbMJ5hI="
    };

    public static String[] default_image_url={
            "https://www.shutterstock.com/image-vector/go-road-trip-vector-illustration-260nw-624096107.jpg",
            "https://thumbs.dreamstime.com/b/road-trip-adventure-concept-vacation-travel-driving-car-highway-vector-urban-landscape-cartoon-road-trip-adventure-concept-137874821.jpg",
            "https://st4.depositphotos.com/1005738/39270/v/450/depositphotos_392702024-stock-illustration-happy-man-woman-driving-car.jpg",
            "https://static.vecteezy.com/system/resources/thumbnails/000/209/171/small_2x/roadtrip-01.jpg",
            "https://img.freepik.com/premium-vector/family-car-parents-kid-pet-weekend-holiday-road-trip-minivan-with-people-cartoon-adventure-travel-mountain-vector-concept-illustration-outdoors-vacation-trip-drive-family_102902-4005.jpg",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT7AaCZm4JK7_WgMb_gJpHYQ58IdBhMG8dEdA&usqp=CAU",
            "https://thumbs.dreamstime.com/b/road-trip-red-car-passenger-retro-luggage-roof-concept-adventure-outdoor-recreation-summer-vacation-planning-216951977.jpg",
            "https://www.revv.co.in/blogs/wp-content/uploads/2021/07/Road-Trips-Essential.png",
            "https://img.freepik.com/premium-vector/take-vacation-travelling-concept-with-red-car-flat-design-illustration-car-travel-concept_95169-3329.jpg?w=2000"
    };
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

    private static boolean isInitialized = false;

    public static void initDatabaseAccess()
    {
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        plans = new ArrayList<Plan>();
        isInitialized = true;
    }

    public static boolean load_data()
    {
        Thread backgroundLoadDataThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!isInitialized)
                {}
                firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            mainUserInfo = task.getResult().toObject(User.class);
                            handler.post(MainActivity.increaseProgress());
                        }
                        else
                        {
                            handler.post(MainActivity.hideProgressBar());
                        }
                    }
                });

                //load plans from database
                while(MainActivity.getCurrentProgressStepOfProgressBar() < 1)
                {
//                    Log.i("Wait", "run: I'm stuck 1");
                }
                if(MainActivity.getCurrentProgressStepOfProgressBar() == MainActivity.getProgressMax())
                {
                    runForegroundTask(MainActivity.hideProgressBar());
                    return;
                }

                List<String> setOfPlanId = mainUserInfo.getPlans();
                if(setOfPlanId.isEmpty())
                {
                    System.out.println("<<Loaddata>>: empty data");
                    return;
                }
                for(int i=0; i< setOfPlanId.size(); i++)
                {
                    System.out.println(i+ ": "+ setOfPlanId.get(i));
                }

                Query getSetPlansQuery = firestore.collection(ACCESS_PLANS_COLLECTION).whereIn("planId", setOfPlanId);
                getSetPlansQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.isEmpty())
                        {
                            System.out.println("queryDoc is empty");

                            return;
                        }
                        List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot doc: docs) {
                            Plan convertedDoc = doc.toObject(Plan.class);
                            Log.i("<<LoadData>>", "Plan " + convertedDoc.getPlanId() + " " + convertedDoc.getName());
                            plans.add(convertedDoc);
                        }
                        runForegroundTask(MainActivity.increaseProgress());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Load data: " + e);
                        runForegroundTask(MainActivity.hideProgressBar());
                    }
                });

                while(MainActivity.getCurrentProgressStepOfProgressBar() < 2)
                {
//                    Log.i("Wait", "run: I'm stuck 2");
                }
                for(int i = 0; i< plans.size(); i++)
                {
                    System.out.println(i + ": " + plans.get(i).getDeparture_date());
                }
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

    public static void updateUserInfo_In_Database(User newUserInfo, Runnable successfulForegroundAction, Runnable failedForegroundAction)
    {
        if(auth == null || firestore == null)
        {
            return;
        }

        firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(auth.getUid()).set(newUserInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful() && successfulForegroundAction != null)
                {
                    handler.post(successfulForegroundAction);
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
                        //newPlan.setCreatedTime(Timestamp.now());

                        transaction.set(plansDoc, newPlan);
                        transaction.update(accountDoc, "plans", FieldValue.arrayUnion(newPlanId));
                        return newPlanId;
                    }
                }).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String newPlanIdCreated) {
                        //update local data
//                        System.out.println("<<NewPlanId created: >>" + newPlanIdCreated);
                        mainUserInfo.addNewPlan(newPlanIdCreated);

                        //get uri of the selected image in local storage, which was selected by the user as the result returned by CreatePlan
                        String imageLink = newPlan.getImageLink();
                        if (!imageLink.equals("None")) {
                            Uri imageNeedToUpload = Uri.parse(imageLink);
                            String childName = newPlanIdCreated + ".jpg";
                            StorageReference plansImageReference = firebaseStorage.getReference().child(ACCESS_PLANS_STORAGE + childName);
                            System.out.println("imageLocalUri" + imageNeedToUpload);
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg").build();

                            UploadTask uploadTask = (UploadTask) plansImageReference.putFile(imageNeedToUpload, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUrl = ACCESS_PLANS_STORAGE + childName;
                                    firestore.collection(ACCESS_PLANS_COLLECTION).document(newPlanIdCreated)
                                            .update("imageLink", downloadUrl);
                                }
                            });
                        }
                        //update the local list of plans
                        plans.add(newPlan);

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

    public static List<Plan> getPlansByStatus(@NotNull String inputStatus)
    {
        List<Plan> suitablePlans = new ArrayList<Plan>();
        for(int i=0; i<plans.size(); i++)
        {
            Plan planAtIndexI = plans.get(i);
            if(planAtIndexI.getStatus().equals(inputStatus))
            {
                suitablePlans.add(planAtIndexI);
            }
        }
        return suitablePlans;
    }

    public static Plan getPlanById(@NotNull String planId)
    {
        Plan specPlan = null;
        for(int i=0;i < plans.size(); i++)
        {
            if(plans.get(i).getPlanId().equals(planId))
            {
                specPlan = plans.get(i);
                break;
            }
        }
        return specPlan;
    }
    public static FirebaseFirestore getFirestore() {
        return firestore;
    }

    public static FirebaseStorage getFirebaseStorage()
    {
        return firebaseStorage;
    }

    public static void addNewDestinationTo(@NotNull Destination newDestination, @NotNull String planId, Runnable successfulTask, Runnable failedTask)
    {
        Thread backgroundTask = new Thread(new Runnable() {
            @Override
            public void run() {

                firestore.collection(ACCESS_PLANS_COLLECTION).document(planId).update("listOfLocations", FieldValue.arrayUnion(newDestination))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                getPlanById(planId).addNewLocation(newDestination);
                                if(successfulTask != null)
                                {
                                    runForegroundTask(successfulTask);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("<<Add destination exception>> ", e.getMessage());
                                if(failedTask != null)
                                {
                                    runForegroundTask(failedTask);
                                }
                            }
                        });
            }
        });
        backgroundTask.start();
    }

//    public void static updateDestinationListTo(@NotNull List<Destination> newList,@NotNull String planId)
//    {
//        Thread backgroundTask = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                firestore.collection(ACCESS_PLANS_COLLECTION).document(planId).update("listOfLocations", newList)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//
//                            }
//                        });
//            }
//        })
//    }

    public static void updatePlanInfo(@NotNull Plan newPlanInfo, Runnable successfulTask, Runnable failedTask)
    {
        Thread backgroundTask = new Thread(new Runnable() {
            @Override
            public void run() {
                DocumentReference planDoc =  firestore.collection(ACCESS_PLANS_COLLECTION).document(newPlanInfo.getPlanId());

                firestore.runTransaction(new Transaction.Function<String>() {
                    @Nullable
                    @Override
                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        transaction.update(planDoc, "name", newPlanInfo.getName());
                        transaction.update(planDoc, "departure_date", newPlanInfo.getDeparture_date());
                        transaction.update(planDoc, "return_date", newPlanInfo.getReturn_date());
                        transaction.update(planDoc, "status", newPlanInfo.getStatus());
                        transaction.update(planDoc, "rating", newPlanInfo.getRating());
                        return newPlanInfo.getImageLink();
                    }
                }).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String imageLink) {
                        if (!imageLink.equals("None"))
                        {

//                            StorageReference plansImageReference = firebaseStorage.getReference().child(imageLink);
//                            plansImageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void unused) {
//                                    Uri imageNeedToUpload = Uri.parse(newPlanInfo.getImageLink());
//                                    StorageReference plansImageReference = firebaseStorage.getReference().child(newPlanInfo.getImageLink());
//                                    StorageMetadata metadata = new StorageMetadata.Builder()
//                                            .setContentType("image/jpg").build();
//                                    UploadTask uploadTask = (UploadTask) plansImageReference.putFile(imageNeedToUpload, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                        @Override
//                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                            runForegroundTask(successfulTask);
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Log.e("<<Update image>>", e.getMessage());
//                                            runForegroundTask(failedTask);
//                                        }
//                                    });
//                                }
//                            });
//                            System.out.println("imageLocalUri" + imageNeedToUpload);

                            if(successfulTask != null)
                            {
                                runForegroundTask(successfulTask);
                            }

                            for(int i=0; i< plans.size(); i++)
                            {
                                if(plans.get(i).getPlanId().equals(newPlanInfo.getPlanId()))
                                {
                                    plans.set(i, newPlanInfo);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("<<Update Plan>>", e.getMessage());
                        runForegroundTask(failedTask);
                    }
                });
            }
        });
        backgroundTask.start();
    }

    public static void leaveATrip(@NotNull String planId, Runnable successfulTask, Runnable failedTask)
    {
        Thread backgroundThread = new Thread(new Runnable() {
            @Override
            public void run() {
                firestore.collection(ACCESS_ACCOUNT_COLLECTION).document(DatabaseAccess.getCurrentUser().getUid())
                        .update("plans", FieldValue.arrayRemove(planId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                mainUserInfo.getPlans().remove(planId);
                                if(successfulTask != null)
                                {
                                    runForegroundTask(successfulTask);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Leave trip", e.getMessage());
                                if(failedTask != null)
                                {
                                    runForegroundTask(failedTask);
                                }
                            }
                        });
            }
        });

        backgroundThread.start();
    }
}
