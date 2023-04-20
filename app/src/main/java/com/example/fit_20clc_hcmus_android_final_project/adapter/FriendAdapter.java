package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.User;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private ArrayList<User> dataSet;

    Context context;

    private FriendAdapter.Callbacks listener;

    public void setListener(FriendAdapter.Callbacks listener) {
        this.listener = listener;
    }

    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToChat(String phone);
    }

    public FriendAdapter(Context _context, ArrayList<User> _dataset) {
        this.context = _context;
        this.dataSet = _dataset;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView avatar;
        private  final TextView name;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            name = (TextView) view.findViewById(R.id.username);
            avatar = (ImageView) view.findViewById(R.id.profile_image);

            view.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }

    }


    // Create new views (invoked by the layout manager)
    @Override
    public FriendAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_friend_item, viewGroup, false);

        return new FriendAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FriendAdapter.ViewHolder viewHolder, final int position) {

        // Get element from your posts at this position and replace the
        // contents of the view with that element
        viewHolder.name.setText(dataSet.get(position).getName());
        if (dataSet.get(position).getAvatarUrl()!=null) {
            Glide.with(context.getApplicationContext())
                    .load(dataSet.get(position).getAvatarUrl())
                    .into(viewHolder.avatar);
        }
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                String email = dataSet.get(position).getUserEmail();
                listener.swapToChat(email);
            }
        });
    }

    // Return the size of your posts (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (dataSet!=null)
            return dataSet.size();
        else return 0;
    }
}
