package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Incoming_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Location;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Trips_Incoming_Adapter extends RecyclerView.Adapter<Incoming_view_holder> {
    @DrawableRes
    private int DEMO_IMAGE = R.drawable.hoi_an_ancient_town_hoi_an_private_taxi_2;
    private Context _context;
    private List<Plan> _data;

    public Trips_Incoming_Adapter(Context context, List<Plan> data)
    {
        _context = context;
        _data = data;
    }


    @NonNull
    @Override
    public Incoming_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View CustomRowView = inflater.inflate(R.layout.trips_incoming_custom_row, parent, false);
        Incoming_view_holder viewHolder = new Incoming_view_holder(CustomRowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Incoming_view_holder holder, int position) {
        Plan plan = _data.get(position);
        holder.getPlanName().setText(plan.getName());
        holder.getDepartureDate().setText("Departure date:" + plan.getDeparture_date());
        holder.getTrips_incoming_image().setImageResource(DEMO_IMAGE);
    }


    @Override
    public int getItemCount()
    {
        return _data.size();
    }
}
