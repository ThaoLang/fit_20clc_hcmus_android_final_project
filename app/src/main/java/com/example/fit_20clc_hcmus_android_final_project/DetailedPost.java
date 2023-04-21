package com.example.fit_20clc_hcmus_android_final_project;

import static com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess.ACCESS_PLANS_COLLECTION;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.adapter.FriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.TripLocationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityDetailedPostBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class DetailedPost extends AppCompatActivity {
    private ActivityDetailedPostBinding binding;
    private Plan plan;
    private String planId="";

    LinearLayoutManager placeLinearLayoutManager;
    LinearLayoutManager memberLinearLayoutManager;
    private boolean isLiked = false;
    private FirebaseFirestore db;
    private boolean prevLiked=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDetailedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getBundleExtra("plan post");
        if (bundle != null) {
            planId = bundle.get("plan id").toString();

        }





        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        binding.popupMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                popupMenu.inflate(R.menu.post_menu);
                Menu menu = popupMenu.getMenu();

                //different account
                if (!DatabaseAccess.getMainUserInfo().getUserEmail().equals(plan.getOwner_email()))
                {
                    menu.removeItem(R.id.action_remove);
                    menu.removeItem(R.id.action_edit);
                    menu.removeItem(R.id.action_set_private);

                    if(!plan.getStatus().equals("Upcoming")){
                        menu.removeItem(R.id.action_join);
                    }
                }
                else{
                    menu.removeItem(R.id.action_join);

                    if(!plan.getStatus().equals("Upcoming")){
                        menu.removeItem(R.id.action_edit);
                    }
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_copy:
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DetailedPost.this, "Copy plan successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                Runnable failedRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DetailedPost.this, "Fail to copy plan!", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                Plan newPlan = plan;
                                newPlan.setPlanId("");
                                newPlan.setPassengers(new ArrayList<String>());
                                newPlan.setListOfLike(new ArrayList<String>());
                                newPlan.setListOfComments(new ArrayList<String>());
                                newPlan.setOwner_email(DatabaseAccess.getMainUserInfo().getUserEmail());
                                newPlan.setSet_of_editors(new ArrayList<String>());
                                DatabaseAccess.addNewPlan(newPlan, runnable, failedRunnable);
                                // Xử lý khi click item settings
                                return true;
                            case R.id.action_edit:
                                // Xử lý khi click item help
                                Intent intent = new Intent(DetailedPost.this, CreatePlan.class);
                                intent.putExtra("SETTING_MODE", TripsPage.EDIT_PLAN_MODE);
                                intent.putExtra(DetailedPlan.DETAILED_PLAN_ID, planId);
                                startActivity(intent);
                                return true;
                            case R.id.action_set_private:
                                DatabaseAccess.getFirestore().runTransaction(new Transaction.Function<String>() {
                                    final DocumentReference plansDoc = DatabaseAccess.getFirestore().collection(ACCESS_PLANS_COLLECTION).document(planId);

                                    @Nullable
                                    @Override
                                    public String apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                                        //transaction.set(plansDoc, newPlan);
                                        transaction.update(plansDoc, "publicAttribute", Boolean.valueOf("false"));
                                        return "Successfully";
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String newPlanIdCreated) {
                                        Toast.makeText(DetailedPost.this, "Set private mode successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(DetailedPost.this, "Fail to set private mode", Toast.LENGTH_SHORT).show();

                                    }
                                });



                                // Xử lý khi click item about
                                return true;
                            case R.id.action_remove:
                                Runnable successRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DetailedPost.this, "Remove plan successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                Runnable failRunnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(DetailedPost.this, "Fail to remove plan!", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                DatabaseAccess.leaveATrip(planId,successRunnable,failRunnable);
                                // Xử lý khi click item about
                                return true;

                            default:
                                return false;

                        }
                    }
                });
                popupMenu.show();
            }
        });




        //Comment
        binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(DetailedPost.this, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("plan id", plan.getPlanId());
                Log.e("HELLO",plan.getPlanId());
                bundle.putString("location id", "None");
                bundle.putString("comment type", "Plan");
                intent.putExtra("comment",bundle);
                startActivity(intent);
                onStart();
            }
        });

        //Like haha
        binding.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLiked){
                    db.collection("plans")
                            .document(plan.getPlanId())
                            .update("listOfLike",FieldValue.arrayRemove(DatabaseAccess.getCurrentUser().getEmail()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("DELETE LTT", "Xóa giá trị khỏi mảng thành công!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("DELETE LTT", "Lỗi khi xóa giá trị khỏi mảng", e);
                                }
                            });

                    if (!prevLiked){
                        binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()));
                    }
                    else{
                        binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()-1));
                    }
                    binding.likeBtn.setImageResource(R.drawable.like_icon);
                    isLiked=!isLiked;
                }
                else{
                    db.collection("plans")
                            .document(plan.getPlanId())
                            .update("listOfLike", FieldValue.arrayUnion(DatabaseAccess.getCurrentUser().getEmail()))
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("ADD LTT", "Thêm giá trị vào mảng thành công!");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w("ADD LTT", "Lỗi khi thêm giá trị vào mảng", e);
                                }
                            });
                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar_icon, 0, 0, 0);
                    //viewHolder.add_Favorite.setDra
                    binding.likeBtn.setImageResource(R.drawable.red_heart_icon);
                    isLiked=!isLiked;
                    if (prevLiked){
                        binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()));
                    }
                    else{
                        binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()+1));
                    }                }
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
        DatabaseAccess.getFirestore().collection("plans")
                .whereEqualTo("planId",planId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    plan = document.toObject(Plan.class);

                                    if (plan.getImageLink().equals("None")) {
                                        Random rng = new Random();
                                        Glide.with(DetailedPost.this)
                                                .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                                                .into(binding.imageSlider);
                                        //slideModels.add(new SlideModel(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)], ScaleTypes.FIT));
                                    } else {
                                        final long MAX_BYTE = 1024 * 2 * 1024 * 2;
                                        StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(plan.getImageLink());
                                        storageReference.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                binding.imageSlider.setImageBitmap(image);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Random rng = new Random();
                                                Glide.with(DetailedPost.this)
                                                        .load(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)])
                                                        .into(binding.imageSlider);
                                            }
                                        });
                                    }

                                    String status_text = plan.getStatus();
                                    binding.status.setText(status_text);
                                    binding.numberComment.setText(String.valueOf(plan.getListOfComments().size()));


                                    if (status_text.equals("Ongoing")) {
                                        binding.status.setCompoundDrawablesWithIntrinsicBounds(DetailedPost.this.getResources().getDrawable(R.drawable.ongoing_icon, DetailedPost.this.getTheme()), null, null, null);
                                        binding.status.setTextColor(DetailedPost.this.getColor(R.color.CustomColor10));
                                    } else if (status_text.equals("Finished")) {
                                        binding.status.setCompoundDrawablesWithIntrinsicBounds(DetailedPost.this.getResources().getDrawable(R.drawable.done_tick_icon, DetailedPost.this.getTheme()), null, null, null);
                                        binding.status.setTextColor(DetailedPost.this.getColor(R.color.md_theme_light_outline));
                                    }


                                    binding.tripTitle.setText(plan.getName());
                                    binding.tripDate.setText(plan.getDeparture_date() + " - " + plan.getReturn_date());
                                    String account_name;
                                    String account_avatar = "";
                                    //get account
                                    db = FirebaseFirestore.getInstance();
                                    db.collection("account")
                                            .whereEqualTo("userEmail", plan.getOwner_email())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        QuerySnapshot querySnapshot = task.getResult();
                                                        if (!querySnapshot.isEmpty()) {
                                                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                                // Xử lí khi có kết quả trả về
                                                                binding.profileName.setText(document.get("name").toString());
                                                                Glide.with(DetailedPost.this)
                                                                        .load(document.get("avatarUrl").toString())
                                                                        .into(binding.profileImage);
                                                            }
                                                        } else {

                                                        }

                                                    } else {

                                                    }

                                                }
                                            });

//
                                }
                                //Recent trips
                                if (plan.getListOfLocations().size()>0){
                                    placeLinearLayoutManager = new LinearLayoutManager(DetailedPost.this, RecyclerView.VERTICAL,false);
                                    placeLinearLayoutManager.setStackFromEnd(true);
                                    binding.listTripDay.setLayoutManager(placeLinearLayoutManager);

                                    binding.listTripDay.setAdapter(new TripLocationAdapter(DetailedPost.this,plan.getListOfLocations()));
                                    binding.listTripDay.smoothScrollToPosition(0);

                                    binding.emptyPlaceText.setVisibility(View.GONE);
                                }

                                //member

                                int number_member=plan.getPassengers().size();
                                binding.numberPeople.setText(String.valueOf(number_member+1)+" people");

                                if(number_member>0) {
                                    memberLinearLayoutManager = new LinearLayoutManager(DetailedPost.this, RecyclerView.VERTICAL, false);
                                    memberLinearLayoutManager.setStackFromEnd(true);
                                    binding.listMember.setLayoutManager(memberLinearLayoutManager);

                                    ArrayList<User> members= new ArrayList<User>();
                                    for (int i = 0; i < number_member; i++) {
                                        db.collection("account")
                                                .whereEqualTo("userEmail", plan.getPassengers().get(i))
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot querySnapshot = task.getResult();
                                                            if (!querySnapshot.isEmpty()) {
                                                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                                                    // Xử lí khi có kết quả trả về
                                                                    User member=document.toObject(User.class);
                                                                    members.add(member);
                                                                }
                                                            } else {

                                                            }

                                                        } else {

                                                        }
                                                        binding.listMember.setAdapter(new FriendAdapter(DetailedPost.this, members));
                                                        binding.listMember.smoothScrollToPosition(0);

                                                    }
                                                });
                                    }

                                    binding.emptyMemberText.setVisibility(View.GONE);
                                }
                                int number_like=plan.getListOfLike().size();
                                Log.e("EMAIL",DatabaseAccess.getMainUserInfo().getUserEmail());

                                for (int i=0;i<number_like;i++){
                                    Log.e("LIKED",plan.getListOfLike().get(i));
                                    if (plan.getListOfLike().get(i).equals(DatabaseAccess.getMainUserInfo().getUserEmail())){
                                        isLiked=false;
                                        prevLiked=true;
                                        binding.likeBtn.performClick();
//                if (isLiked){
//                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
//                    binding.likeBtn.setImageResource(R.drawable.like_icon);
//                    isLiked=!isLiked;
//                }
//                else{
//                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar_icon, 0, 0, 0);
//                    //viewHolder.add_Favorite.setDra
//                    binding.likeBtn.setImageResource(R.drawable.red_heart_icon);
//                    isLiked=!isLiked;
//                }

                                        break;
                                    }
                                }
                                binding.numberLike.setText(String.valueOf(number_like));


                            }
                        } else {
                            // no notification
                        }
                    }
                });

    }
}
