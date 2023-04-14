package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.CommentActivity;
import com.example.fit_20clc_hcmus_android_final_project.LocationInfo;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.Search;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.TripLocationAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityDetailedPostBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;

import java.util.ArrayList;

public class DetailedPost extends AppCompatActivity {
    private ActivityDetailedPostBinding binding;

    LinearLayoutManager mLinearLayoutManager;
    private boolean isLiked=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityDetailedPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<SlideModel> slideModels=new ArrayList<>();
        slideModels.add(new SlideModel("https://img.freepik.com/free-vector/happy-family-travelling-by-car-with-camping-equipment-top_74855-10751.jpg", ScaleTypes.FIT));
        binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);

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
        mLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        mLinearLayoutManager.setStackFromEnd(true);

        binding.listTripDay.setLayoutManager(mLinearLayoutManager);

        binding.listTripDay.setAdapter(new TripLocationAdapter(this));
        binding.listTripDay.smoothScrollToPosition(0);

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
                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like_icon, 0, 0, 0);
                    binding.likeBtn.setImageResource(R.drawable.heart_icon);
                    isLiked=!isLiked;
                }
                else{
                    //viewHolder.add_Favorite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.calendar_icon, 0, 0, 0);
                    //viewHolder.add_Favorite.setDra
                    binding.likeBtn.setImageResource(R.drawable.red_heart_icon);
                    isLiked=!isLiked;
                }
            }
        });

    }
}
