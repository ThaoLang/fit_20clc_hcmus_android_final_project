package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

public class Ongoing_view_holder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
    private ShapeableImageView trips_ongoing_image;
    private MaterialTextView planName, endDate;

    private ItemClickListener itemClickListener;

    public Ongoing_view_holder(@NonNull View itemView)
    {
        super(itemView);
        trips_ongoing_image = (ShapeableImageView) itemView.findViewById(R.id.trips_incoming_custom_row_image);
        planName = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_plan_name);
        endDate = (MaterialTextView) itemView.findViewById(R.id.trips_incoming_custom_row_departure_date);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public ShapeableImageView getTrips_Ongoing_image()
    {
        return trips_ongoing_image;
    }

    public MaterialTextView getPlanName()
    {
        return planName;
    }

    public MaterialTextView getEndDate()
    {
        return endDate;
    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View view)
    {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}
