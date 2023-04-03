package com.example.fit_20clc_hcmus_android_final_project;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomNotificationAdapter extends RecyclerView.Adapter<CustomNotificationAdapter.ViewHolder> {

    //    Notification dataset here vvv
    private String[] localDataSet ={
            "Click 1",
            "Click 2",
            "Click 3",
            "Click 4",
            "Click 1",
            "Click 2",
            "Click 3",
            "Click 4",
            "Click 1",
            "Click 2",
            "Click 3",
            "Click 4",
    };
    private ArrayList<String> dataSet = new ArrayList<>(Arrays.asList(localDataSet));

    Context context;

    private Callbacks listener;
    public void setListener(Callbacks listener) {
        this.listener = listener;
    }
    // nesting it inside MyAdapter makes the path MyAdapter.Callbacks, which makes it clear
    // exactly what it is and what it relates to, and kinda gives the Adapter "ownership"
    public interface Callbacks {
        void sendNotification();

        void swapToTrips();
    }

    /**
     * Initialize the dataset of the Adapter.
     * <p>
     * param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */

    public CustomNotificationAdapter(Context _context) {
        this.context = _context;
    }

//    public CustomNotificationAdapter(Context _context, ArrayList<String> dataSet) {
//        this.context = _context;
//        this.dataSet = dataSet;
//    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{
        private final TextView textView;

        private ItemClickListener itemClickListener;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        // Constructor
//        public TextView getTextView() {
//            return textView;
//        }

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

        //DEBUG: Send notification on create
        listener.sendNotification();

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.textView.setText(dataSet.get(position));

//        Toast.makeText(context, "Hello " + dataSet[position], Toast.LENGTH_SHORT).show();
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if(isLongClick)
                    Toast.makeText(context, "Long Click: "+ dataSet.get(position), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, " "+ dataSet.get(position), Toast.LENGTH_SHORT).show();

                //DEBUG: Send notification here
                listener.sendNotification();

                listener.swapToTrips();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}