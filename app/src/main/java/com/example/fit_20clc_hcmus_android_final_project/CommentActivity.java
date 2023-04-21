package com.example.fit_20clc_hcmus_android_final_project;

import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_ACCOUNT_COLLECTION;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_COMMENTS_STORAGE;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_COMMENT_SET_COLLECTION;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_PLANS_COLLECTION;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_PLANS_STORAGE;
import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.runForegroundTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.adapter.CommentAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Comment;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Location;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityCommentBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firestore.v1.StructuredQuery;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;
    LinearLayoutManager mLinearLayoutManager;

    private Uri selectedImageUri = null;
    private String comment_type="";
    private String planId;
    private String locationId;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle= getIntent().getBundleExtra("comment");
        planId=bundle.get("plan id").toString();
        locationId=bundle.get("location id").toString();
        comment_type=bundle.get("comment type").toString();

        binding.demoImage.setVisibility(View.GONE);

        //Load comment
//        Bundle bundle= getIntent().getBundleExtra("location search");
//        locationName=bundle.get("location address").toString();
//        ArrayList<SlideModel> slideModels=new ArrayList<>();

        //back to previous page
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.pickImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                selectImageLauncher.launch(intent);
            }
        });

        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.chatTextField.getText().length()==0 && selectedImageUri==null){
                    binding.chatTextField.setText("");
                    binding.demoImage.setVisibility(View.GONE);
                    //Log.e("TTTT","Please come here");
                    Toast.makeText(getApplicationContext(),"Please comment something or choose an image to post",Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("EMAILLL",DatabaseAccess.getMainUserInfo().getUserEmail());
                Comment newComment=new Comment();
                newComment.setAccountEmail(DatabaseAccess.getMainUserInfo().getUserEmail());
                newComment.setText_comment(binding.chatTextField.getText().toString());
                newComment.setType(comment_type);
                newComment.setTargetPlanId(planId);
                if(selectedImageUri == null)
                {
                    newComment.setImageLink("None");
                }
                else
                {
                    newComment.setImageLink(selectedImageUri.toString());
                }
                binding.chatTextField.setText("");
                binding.demoImage.setVisibility(View.GONE);
                addNewComment(newComment,null,null);

            }

        });



    }
    @Override
    protected void onStart(){
        super.onStart();
        mLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        mLinearLayoutManager.setStackFromEnd(true);

        binding.listComment.setLayoutManager(mLinearLayoutManager);
        ArrayList<Comment> comments= new ArrayList<Comment>();
//        FirebaseFirestore fb = FirebaseFirestore.getInstance();
        FirebaseFirestore fb = DatabaseAccess.getFirestore();

        fb.collection("plans")
                .whereEqualTo("planId",planId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Plan plan = document.toObject(Plan.class);

//                                    String owner_email = document.get("owner_email").toString();
//                                    String name = document.get("name").toString();
//                                    String imageLink = document.get("imageLink").toString();
//                                    String planId = document.get("planId").toString();
//                                    String sDate = document.get("departure_date").toString();
//                                    String eDate = document.get("return_date").toString();
//                                    String status = document.get("status").toString();


                                    List<String> listOfComments = (List<String>) plan.getListOfComments();
                                    for (int i=0;i<listOfComments.size();i++){
                                        Log.e("KHANH NGUYEN", listOfComments.get(i).toString());
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        db.collection("commentSets")
                                                .whereEqualTo("commentId", listOfComments.get(i).toString())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            if (!querySnapshot.isEmpty()) {
                                                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                                    // Xử lí khi có kết quả trả về
                                                                    String text=document.get("text_comment").toString();
                                                                    Comment comment=document.toObject(Comment.class);
                                                                    Log.e("HOAI PHUONG ",text);
//                                                                    .name.setText(document.get("name").toString());
//                                                                    Glide.with(context.getApplicationContext())
//                                                                            .load(document.get("avatarUrl").toString())
//                                                                            .into(viewHolder.avatar);
                                                                    comments.add(comment);
                                                                }
                                                            } else {
                                                            }

                                                        } else {
                                                        }
                                                    }
                                                });
//                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//                                        db.collection("commentSets")
//                                                .whereEqualTo("commentId",listOfComments.get(i).toString() )
//                                                .get()
//                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                        if (task.isSuccessful()) {
//                                                            QuerySnapshot querySnapshot = task.getResult();
//                                                            if (!querySnapshot.isEmpty()) {
//                                                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                                                                    String text_comment=document.get("textComment").toString();
//                                                                    Comment comment = document.toObject(Comment.class);
//                                                                    Log.e("HOAI PHUONG", text_comment);
//                                                                    comments.add(comment);
//                                                                }
//                                                            }
//
//                                                             else {
//                                                            }
//
//                                                        } else {
//                                                        }
//                                                    }
//                                                });
                                    }
//                                    Log.e("COMMENT SIZE",String.valueOf(listOfComments.size()));
//                                    List<Destination> listOfLocations = (List<Destination>) document.get("listOfLocations");
//                                    List<String> passengers = (List<String>) document.get("passengers");
//                                    List<String> listOfLike = (List<String>) document.get("listOfLike");

//                                    plan=new Plan(planId,name,owner_email,sDate,eDate,true,0F,imageLink,listOfLocations,listOfLike,listOfComments,passengers,status)
                                }



                                CommentAdapter commentAdapter=new CommentAdapter(getApplicationContext(),comments);
                                binding.listComment.setAdapter(commentAdapter);
                                binding.listComment.smoothScrollToPosition(commentAdapter.getItemCount());
                            }
                        } else {
                            // no notification
                        }
                    }
                });





    }
    private ActivityResultLauncher<Intent> selectImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if(result.getResultCode() == Activity.RESULT_OK)
            {
                Intent imageData = result.getData();
                if(imageData != null && imageData.getData() != null)
                {
                    selectedImageUri = imageData.getData();
                    Bitmap selectedImage;
                    System.out.println("ImageUri: " + selectedImageUri);
                    try {
                        getContentResolver().takePersistableUriPermission(selectedImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
//                        selectedImage.setHeight(image.getHeight());
//                        selectedImage.setWidth(image.getWidth());
                        binding.demoImage.setVisibility(View.VISIBLE);
                        binding.demoImage.setImageBitmap(selectedImage);
                    } catch (IOException e) {
                        //throw new RuntimeException(e);
                        selectedImageUri = null;
                        binding.demoImage.setImageResource(R.drawable.image_48px);
                    }
                }
                else
                {
                    selectedImageUri = null;
                }
            }
        }
    });

    public void addNewComment(@NotNull Comment newComment, Runnable successfulTask, Runnable failureTask)
    {
        Thread backgroundAddition = new Thread(new Runnable() {
            @Override
            public void run() {
                //add and update data in the cloud database
                DatabaseAccess.getFirestore().runTransaction(new Transaction.Function<String>() {
                    final DocumentReference plansDoc = DatabaseAccess.getFirestore().collection(ACCESS_PLANS_COLLECTION).document(newComment.getTargetPlanId());
                    final DocumentReference commentSetDoc = DatabaseAccess.getFirestore().collection(ACCESS_COMMENT_SET_COLLECTION).document();

                    @Nullable
                    @Override
                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        String newCommentId = commentSetDoc.getId();
                        newComment.setCommentId(newCommentId);
                        //newPlan.setCreatedTime(Timestamp.now());

                        transaction.set(commentSetDoc, newComment);
                        if (newComment.getType().equals("Plan"))
                        {
                            transaction.update(plansDoc, "listOfComments", FieldValue.arrayUnion(newCommentId));
                        }
                        else{
//                            plansDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                    if (task.isSuccessful()) {
//                                        DocumentSnapshot documentSnapshot = task.getResult();
//                                        if (documentSnapshot.exists()) {
//                                            Plan plan = documentSnapshot.toObject(Plan.class);
//
//                                            List<Destination> listOfLocation = (List<Destination>) plan.getListOfLocations();
//
//                                            for (int i=0;i<listOfLocation.size();i++){
//                                                if (listOfLocation.get(i).get)
//                                            }
//                                            if (commentInfoList != null) {
//                                                commentInfoList.add(newComment.getCommentId());
//                                                //transaction.update(plansDoc, "listOfLocations", FieldValue.arrayUnion(newPlanId));
//                                            } else {
//                                                // Mảng "interests" chưa có dữ liệu
//                                            }
//                                        } else {
//                                            // Tài liệu không tồn tại
//                                        }
//                                    } else {
//                                        // Lỗi xảy ra
//                                    }
//                                }
//                            });
////
//                            transaction.update(plansDoc, "listOfLocations", FieldValue.arrayUnion(newPlanId));
                        }
                        return newCommentId;
                    }
                }).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String newCommentIdCreated) {
                        String imageLink = newComment.getImageLink();
                        if (!imageLink.equals("None")) {
                            Uri imageNeedToUpload = Uri.parse(imageLink);
                            String childName = newCommentIdCreated + ".jpg";
                            StorageReference plansImageReference = DatabaseAccess.getFirebaseStorage().getReference().child(ACCESS_COMMENTS_STORAGE + childName);
                            System.out.println("imageLocalUri" + imageNeedToUpload);
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg").build();

                            UploadTask uploadTask = (UploadTask) plansImageReference.putFile(imageNeedToUpload, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    String downloadUrl = ACCESS_COMMENTS_STORAGE + childName;
                                    DatabaseAccess.getFirestore().collection(ACCESS_COMMENT_SET_COLLECTION).document(newCommentIdCreated)
                                            .update("imageLink", downloadUrl);
                                }
                            });
                        }

                        onStart();
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
