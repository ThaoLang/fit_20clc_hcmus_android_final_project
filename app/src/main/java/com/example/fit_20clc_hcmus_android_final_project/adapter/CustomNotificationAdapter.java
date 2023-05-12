package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Notification;

import java.util.ArrayList;


public class CustomNotificationAdapter extends RecyclerView.Adapter<CustomNotificationAdapter.ViewHolder> {

    private ArrayList<Notification> dataSet;
    Context context;

    private Callbacks listener;
    public void setListener(Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void swapToChat(String planId);
    }

    public CustomNotificationAdapter(Context _context, ArrayList<Notification> dataSet) {
        this.context = _context;
        this.dataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private final TextView title;
        private final TextView content;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }

        @Override
        public boolean onLongClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),true);
            return true;
        }
    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.notification, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.title.setText(dataSet.get(position).getTitle());
        viewHolder.content.setText(dataSet.get(position).getContent());

        viewHolder.setItemClickListener((view, position1, isLongClick) -> {
            if(isLongClick)
                Snackbar.make(view, "Click to move to "+ dataSet.get(position1).getTitle()+"'s chat room! ", Snackbar.LENGTH_SHORT).show();
            else
                listener.swapToChat(dataSet.get(position1).getTripId());
        });
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}