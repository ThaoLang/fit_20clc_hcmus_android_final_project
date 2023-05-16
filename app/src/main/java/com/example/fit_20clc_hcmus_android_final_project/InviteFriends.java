package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.DataAccessBufferItem;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunctionBooleanParam;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunctionIntParam;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunction_Int_Bool_Param;
import com.example.fit_20clc_hcmus_android_final_project.adapter.InviteFriendAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.CloudNotification;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class InviteFriends extends AppCompatActivity
{
    private MaterialToolbar toolbar;
    private TextInputEditText search;
    private RecyclerView suggestions, selected;
    private MaterialTextView no_searched_result, no_selected_friends;

    private FloatingActionButton sendInviteButton;

    private ShapeableImageView searchModeIcon;

    private MaterialButton searchButton;

    private int search_mode;
    private boolean isSelecting;
    private String specPlanId;

    public static final int SEARCHED_RESULT = 0;
    public static final int SELECTED_RESULT = 1;
    public static final boolean VIEW_ONLY = false;
    public static final boolean EDITOR = true;


    private final int SEARCH_BY_RELATIVE_NAME = 0;
    private final int SEARCH_BY_EMAIL = 1;
    private final int SEARCH_BY_PHONE = 2;

    private List<User> searched_result;
    private List<User> selected_result;

    private List<Boolean> roleOfSelected; //true: editor, false: view only

    private boolean isTransacting;


    private VoidFunctionIntParam OnInviteButtonClick = (int itemPosition) ->
    {
        if(searched_result.isEmpty() == false)
        {
            User item = searched_result.remove(itemPosition);
            selected_result.add(item);
            roleOfSelected.add(VIEW_ONLY);
            onStart();
        }
    };

    private VoidFunction_Int_Bool_Param OnSetSelectedUserRole = (int position, Boolean role) ->
    {
        if(roleOfSelected.isEmpty() == false)
        {
            roleOfSelected.set(position, role);
        }
    };

    private VoidFunctionIntParam OnRemoveSelectedUser = (int position) ->
    {
        if(selected_result.isEmpty() == false)
        {
            selected_result.remove(position);
            roleOfSelected.remove(position);
            onStart();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friend);

        toolbar = findViewById(R.id.invite_friend_toolbar);
        search = findViewById(R.id.invite_friend_search);
        searchButton = findViewById(R.id.invite_friend_search_button);
        suggestions = findViewById(R.id.invite_friend_suggestions);
        selected = findViewById(R.id.invite_friend_invited);
        no_searched_result = findViewById(R.id.invite_friend_no_searched_results);
        no_selected_friends = findViewById(R.id.invite_friend_no_selected);
        searchModeIcon = findViewById(R.id.invite_friend_search_mode_icon);
        sendInviteButton = findViewById(R.id.invite_friends_fab);

        search_mode = 0;
        Intent initIntent = getIntent();
        specPlanId = initIntent.getStringExtra("PLAN_ID");
        isSelecting = false;
        searched_result = new ArrayList<>();
        selected_result = new ArrayList<>();
        roleOfSelected = new ArrayList<>();
        isTransacting = true;

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = search.getText().toString();
                if(input.isEmpty())
                {
                    Toast.makeText(InviteFriends.this, "Please provide keywords for searching!", Toast.LENGTH_LONG).show();
                    return;
                }

                Runnable successfulTask = new Runnable() {
                    @Override
                    public void run() {
                        //List<DataAccessBufferItem> list = DatabaseAccess.getCloneInnerBuffer();
                        searched_result.clear();
                        searched_result = DatabaseAccess.getCloneSearchedUserBuffer();
                        onStart();
                    }
                };

                Runnable failureTask = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InviteFriends.this, "Cannot find the name provided! Please check again!", Toast.LENGTH_LONG).show();
                    }
                };
                String keyword = input.trim();
                switch(search_mode)
                {
                    case SEARCH_BY_RELATIVE_NAME:
                    {
                        DatabaseAccess.getUserByRelativeName(keyword, successfulTask, failureTask);
                        break;
                    }

                    case SEARCH_BY_EMAIL:
                    {
                        DatabaseAccess.getUserByRelativeEmail(keyword, successfulTask, failureTask);
                        break;
                    }

                    case SEARCH_BY_PHONE:
                    {
                        DatabaseAccess.getUserByRelativePhoneNumber(keyword, successfulTask, failureTask);
                        break;
                    }
                }

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isTransacting == true)
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
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
        {
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
                if(isTransacting == true)
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
            }
        };

        sendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_result.isEmpty())
                {
                    finish();
                    return;
                }

                Runnable successfulTask = new Runnable() {
                    @Override
                    public void run() {
                        selected_result.clear();
                        searched_result.clear();
                        Toast.makeText(getApplicationContext(), "Send invitations successfully!", Toast.LENGTH_LONG).show();
                    }
                };

                Runnable failureTask = new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Send invitations failed!", Toast.LENGTH_LONG).show();
                    }
                };

                CloudNotification notification = new CloudNotification();
                notification.setTitle("Invitation");
                notification.setContent(CloudNotification.CONTENT_INVITE_FRIEND);
                notification.setSender_email(DatabaseAccess.getMainUserInfo().getUserEmail());
                notification.setTopic(CloudNotification.TOPIC_INVITE_FRIENDS);

                List<String> targets = new ArrayList<>();
                targets.add(specPlanId);

                notification.setTargets(targets);
                DatabaseAccess.pushCloudNotification_UserList(notification, selected_result, successfulTask, failureTask);

                //selected_result.clear();
                onStart();
            }
        });

        getOnBackPressedDispatcher().addCallback(backPressedCallback);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        suggestions.setVisibility(View.VISIBLE);
        selected.setVisibility(View.VISIBLE);
        no_searched_result.setVisibility(View.GONE);
        no_selected_friends.setVisibility(View.GONE);
        if(searched_result.isEmpty() == true)
        {
            suggestions.setVisibility(View.GONE);
            no_searched_result.setVisibility(View.VISIBLE);
        }
        if(selected_result.isEmpty() == true)
        {
            selected.setVisibility(View.GONE);
            no_selected_friends.setVisibility(View.VISIBLE);
        }

        suggestions.removeAllViews();
        selected.removeAllViews();

        InviteFriendAdapter searched_adapter = new InviteFriendAdapter(InviteFriends.this, searched_result, SEARCHED_RESULT);
        searched_adapter.setOnInviteButtonClick(OnInviteButtonClick);
        InviteFriendAdapter selected_adapter = new InviteFriendAdapter(InviteFriends.this, selected_result, SELECTED_RESULT);
        selected_adapter.setOnSetSelectedUserRole(OnSetSelectedUserRole);
        selected_adapter.setOnRemoveSelectedUser(OnRemoveSelectedUser);

        suggestions.setAdapter(searched_adapter);
        selected.setAdapter(selected_adapter);

    }
}
