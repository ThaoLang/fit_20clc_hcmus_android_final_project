package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class Incoming_view_holder extends RecyclerView.ViewHolder {

    private ShapeableImageView trips_incoming_image;
    private MaterialTextView planName, departureDate;

    public Incoming_view_holder(@NonNull View itemView)
    {
        super(itemView);
        trips_incoming_image = (ShapeableImageView) itemView.findViewById(R.id.trips_incoming_custom_row_image);
        planName = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_plan_name);
        departureDate = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_departure_date);
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

}
