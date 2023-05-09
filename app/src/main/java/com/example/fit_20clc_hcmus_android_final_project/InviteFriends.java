package com.example.fit_20clc_hcmus_android_final_project;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

public class InviteFriends extends AppCompatActivity
{
    private MaterialToolbar toolbar;
    private TextInputEditText search;
    private RecyclerView suggestions, invited;
    private MaterialTextView no_searched_result, no_selected_friends;

    private ShapeableImageView searchModeIcon;

    private MaterialButton searchButton;

    private int search_mode;

    private final int SEARCH_BY_RELATIVE_NAME = 0;
    private final int SEARCH_BY_EMAIL = 1;
    private final int SEARCH_BY_PHONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        toolbar = findViewById(R.id.invite_friend_toolbar);
        search = findViewById(R.id.invite_friend_search);
        searchButton = findViewById(R.id.invite_friend_search_button);
        suggestions = findViewById(R.id.invite_friend_suggestions);
        invited = findViewById(R.id.invite_friend_invited);
        no_searched_result = findViewById(R.id.invite_friend_no_searched_results);
        no_selected_friends = findViewById(R.id.invite_friend_no_selected);
        searchModeIcon = findViewById(R.id.invite_friend_search_mode_icon);

        search_mode = 0;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(InviteFriends.this);
                builder.setMessage("Do you want to leave? Your process will be unsaved");
                builder.setTitle("Inviting is interrupted");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });

                Dialog dialog = builder.create();
                dialog.show();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.invite_friend_toolbar_search_filter)
                {
                    PopupMenu filterMenu = new PopupMenu(InviteFriends.this, toolbar.getChildAt(2));
                    filterMenu.inflate(R.menu.invite_friends_toolbar_menu_filter_popup);
                    filterMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.invite_friends_toolbar_menu_filter_popup_search_by_relative_name)
                            {
                                searchModeIcon.setForeground(getDrawable(R.drawable.badge_48px));
                                search_mode = SEARCH_BY_RELATIVE_NAME;
                            }
                            else if(item.getItemId() == R.id.invite_friends_toolbar_menu_filter_popup_search_by_email)
                            {
                                searchModeIcon.setForeground(getDrawable(R.drawable.mail_48px));
                                search_mode = SEARCH_BY_EMAIL;
                            }
                            else if(item.getItemId() == R.id.invite_friends_toolbar_menu_filter_popup_search_by_phone)
                            {
                                searchModeIcon.setForeground(getDrawable(R.drawable.call_48px));
                                search_mode = SEARCH_BY_PHONE;
                            }

                                return false;
                        }
                    });

                    filterMenu.show();
                }
                return false;
            }
        });


        OnBackPressedCallback backPressedCallback = new OnBackPressedCallback(true)
        {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder builder = new AlertDialog.Builder(InviteFriends.this);
                builder.setMessage("Do you want to leave? Your process will be unsaved");
                builder.setTitle("Inviting is interrupted");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });

                Dialog dialog = builder.create();
                dialog.show();
            }
        };

        getOnBackPressedDispatcher().addCallback(backPressedCallback);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

    }
}
