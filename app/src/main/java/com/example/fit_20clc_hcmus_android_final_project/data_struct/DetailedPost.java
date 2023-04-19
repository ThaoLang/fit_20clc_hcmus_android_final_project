package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.CommentActivity;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.LocationInfo;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.Search;
import com.example.fit_20clc_hcmus_android_final_project.adapter.FriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.TripLocationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityDetailedPostBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DetailedPost extends AppCompatActivity {
    private ActivityDetailedPostBinding binding;
    private Plan plan;

    LinearLayoutManager placeLinearLayoutManager;
    LinearLayoutManager memberLinearLayoutManager;
    private boolean isLiked=false;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDetailedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle= getIntent().getBundleExtra("plan post");
        if (bundle!=null){
            plan=(Plan) bundle.getSerializable("plan");

        }

        Random rng=new Random();
        ArrayList<SlideModel> slideModels=new ArrayList<>();
        slideModels.add(new SlideModel(DatabaseAccess.default_image_url[rng.nextInt(DatabaseAccess.default_image_url.length)], ScaleTypes.FIT));
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        String status_text=plan.getStatus();
        binding.status.setText(status_text);

        if(status_text.equals("Ongoing")){
            binding.status.setCompoundDrawablesWithIntrinsicBounds(DetailedPost.this.getResources().getDrawable(R.drawable.ongoing_icon, DetailedPost.this.getTheme()), null, null, null);
            binding.status.setTextColor(DetailedPost.this.getColor(R.color.CustomColor10));
        }
        else if(status_text.equals("Finished")){
            binding.status.setCompoundDrawablesWithIntrinsicBounds(DetailedPost.this.getResources().getDrawable(R.drawable.done_tick_icon, DetailedPost.this.getTheme()), null, null, null);
            binding.status.setTextColor(DetailedPost.this.getColor(R.color.md_theme_light_outline));
        }



        binding.tripTitle.setText(plan.getName());
        binding.tripDate.setText(plan.getDeparture_date()+" - "+ plan.getReturn_date());
        String account_name;
        String account_avatar="";
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
                    menu.removeItem(R.id.action_delete);
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
                                // Xử lý khi click item settings
                                return true;
                            case R.id.action_edit:
                                // Xử lý khi click item help
                                return true;
                            case R.id.action_set_private:
                                // Xử lý khi click item about
                                return true;
                            case R.id.action_delete:
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

        //Recent trips
        if (this.plan.getListOfLocations().size()>0){
            placeLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
            placeLinearLayoutManager.setStackFromEnd(true);
            binding.listTripDay.setLayoutManager(placeLinearLayoutManager);

            binding.listTripDay.setAdapter(new TripLocationAdapter(this,plan.getListOfLocations()));
            binding.listTripDay.smoothScrollToPosition(0);

            binding.emptyPlaceText.setVisibility(View.GONE);
        }

        //member

        int number_member=this.plan.getPassengers().size();
        binding.numberPeople.setText(String.valueOf(number_member+1)+" people");

        if(number_member>0) {
            memberLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
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


        //Comment
        binding.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(DetailedPost.this, CommentActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("plan id","binding.searchView.getQuery().toString()");

                intent.putExtra("plan comment",bundle);
                startActivity(intent);
            }
        });

        //Like
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
                    binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()-1));
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
                    binding.numberLike.setText(String.valueOf(plan.getListOfLike().size()+1));
                }
            }
        });

        int number_like=plan.getListOfLike().size();
        Log.e("EMAIL",DatabaseAccess.getMainUserInfo().getUserEmail());

        for (int i=0;i<number_like;i++){
            Log.e("LIKED",plan.getListOfLike().get(i));
            if (plan.getListOfLike().get(i).equals(DatabaseAccess.getMainUserInfo().getUserEmail())){
                isLiked=false;
                binding.likeBtn.performClick();
//                if(isLiked){
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
}
