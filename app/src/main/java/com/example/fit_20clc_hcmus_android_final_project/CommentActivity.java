package com.example.fit_20clc_hcmus_android_final_project;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.adapter.CommentAdapter;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityCommentBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;

import java.util.ArrayList;

public class CommentActivity extends AppCompatActivity {
    private ActivityCommentBinding binding;
    LinearLayoutManager mLinearLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        mLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,false);
        mLinearLayoutManager.setStackFromEnd(true);

        binding.listComment.setLayoutManager(mLinearLayoutManager);

        CommentAdapter commentAdapter=new CommentAdapter(this);
        binding.listComment.setAdapter(commentAdapter);
        binding.listComment.smoothScrollToPosition(commentAdapter.getItemCount());

    }
}
