package com.example.fit_20clc_hcmus_android_final_project;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class InviteFriends extends AppCompatActivity
{
    private MaterialToolbar toolbar;
    private TextInputEditText search;
    private RecyclerView suggestions, invited;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        toolbar = findViewById(R.id.invite_friend_toolbar);
        search = findViewById(R.id.invite_friend_search);
        suggestions = findViewById(R.id.invite_friend_suggestions);
        invited = findViewById(R.id.invite_friend_invited);

        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }
}
