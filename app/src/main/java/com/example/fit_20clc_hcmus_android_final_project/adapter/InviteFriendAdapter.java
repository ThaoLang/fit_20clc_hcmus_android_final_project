package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.DataAccessBufferItem;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunction;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunctionBooleanParam;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunctionIntParam;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunction_Int_Bool_Param;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.InviteFriends;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.InviteFriendViewHolder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendViewHolder> {

    private int mode;
    private List<User> userList = new ArrayList<>();
    private Context context;

    private VoidFunctionIntParam OnInviteButtonClick;
    private VoidFunction_Int_Bool_Param OnSetSelectedUserRole;
    private VoidFunctionIntParam OnRemoveSelectedUser;


    public InviteFriendAdapter(Context inputContext, @NotNull List<User> inputUser, @NotNull int inputMode)
    {
        mode = inputMode;
        context = inputContext;
        userList = inputUser;
        OnInviteButtonClick = null;
        OnSetSelectedUserRole = null;
        OnRemoveSelectedUser = null;
    }

    @NonNull
    @Override
    public InviteFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View customView = inflater.inflate(R.layout.detailed_plan_traveler_custom_row, parent, false);
        InviteFriendViewHolder holder = new InviteFriendViewHolder(customView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull InviteFriendViewHolder holder, int position) {
        User user = userList.get(position);
        holder.getTraveler_name().setText(user.getName());
        if(mode == InviteFriends.SEARCHED_RESULT)
        {
            holder.getMainButton().setText("Invite");
        }
        else if (mode == InviteFriends.SELECTED_RESULT)
        {
            holder.getMainButton().setText("View only");
        }

        holder.getMainButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode == InviteFriends.SEARCHED_RESULT)
                {
                    if(OnInviteButtonClick != null)
                    {
                        OnInviteButtonClick.apply(holder.getAdapterPosition());
                    }
                }
                else if(mode == InviteFriends.SELECTED_RESULT)
                {
                    PopupMenu popupMenu = new PopupMenu(context, view);
                    popupMenu.inflate(R.menu.invite_friends_selected_users_popup_menu);

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.invite_friends_selected_users_popup_menu_viewonly)
                            {
                                OnSetSelectedUserRole.apply(holder.getAdapterPosition(), InviteFriends.VIEW_ONLY);
                                holder.getMainButton().setText("view only");
                            }
                            else if(item.getItemId() == R.id.invite_friends_selected_users_popup_menu_editor)
                            {
                                OnSetSelectedUserRole.apply(holder.getAdapterPosition(), InviteFriends.EDITOR);
                                holder.getMainButton().setText("editor");
                            }
                            else if(item.getItemId() == R.id.invite_friends_selected_users_popup_menu_remove)
                            {
                                OnRemoveSelectedUser.apply(holder.getAdapterPosition());
                            }

                            return false;
                        }
                    });

                    popupMenu.show();
                }
            }
        });

        if(userList.get(position).getAvatarUrl() != null)
        {
            DatabaseAccess.getFirebaseStorage().getReference().child(userList.get(position).getAvatarUrl())
                            .getBytes(2*1024*1024)
                            .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            holder.getTraveler_image().setImageBitmap(image);
                                        }
                                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Glide.with(context).load(userList.get(holder.getAdapterPosition()).getAvatarUrl()).into(holder.getTraveler_image());
                                }
                            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setOnInviteButtonClick(VoidFunctionIntParam newOnInviteButtonClick)
    {
        OnInviteButtonClick = newOnInviteButtonClick;
    }

    public void setOnSetSelectedUserRole(VoidFunction_Int_Bool_Param newOnSetSelectedUserRole)
    {
        OnSetSelectedUserRole = newOnSetSelectedUserRole;
    }

    public void setOnRemoveSelectedUser(VoidFunctionIntParam newOnRemoveSelectedUser)
    {
        OnRemoveSelectedUser = newOnRemoveSelectedUser;
    }

}
