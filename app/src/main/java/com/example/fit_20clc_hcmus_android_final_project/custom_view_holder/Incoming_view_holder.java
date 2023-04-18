package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.adapter.Trips_Incoming_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class Incoming_view_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private Context _context;

    private ItemClickListener itemClickListener;
    private ShapeableImageView trips_incoming_image;
    private MaterialTextView planName, departureDate;

    public Incoming_view_holder(@NonNull View itemView, Context context)
    {
        super(itemView);
        trips_incoming_image = (ShapeableImageView) itemView.findViewById(R.id.trips_incoming_custom_row_image);
        planName = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_plan_name);
        departureDate = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_departure_date);
        _context = context;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public ShapeableImageView getTrips_incoming_image()
    {
        return trips_incoming_image;
    }

    public MaterialTextView getPlanName()
    {
        return planName;
    }

    public MaterialTextView getDepartureDate()
    {
        return departureDate;
    }


    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v)
    {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}
