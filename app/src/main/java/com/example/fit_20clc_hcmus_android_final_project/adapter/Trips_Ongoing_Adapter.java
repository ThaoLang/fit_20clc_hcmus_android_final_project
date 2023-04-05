package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Incoming_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Ongoing_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;

import java.util.List;

public class Trips_Ongoing_Adapter extends RecyclerView.Adapter<Ongoing_view_holder> {
    @DrawableRes
    private int DEMO_IMAGE = R.drawable.hoi_an_ancient_town_hoi_an_private_taxi_2;
    private Context _context;
    private List<Plan> _data;

    public Trips_Ongoing_Adapter(Context context, List<Plan> data)
    {
        _context = context;
        _data = data;
    }


    @NonNull
    @Override
    public Ongoing_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View CustomRowView = inflater.inflate(R.layout.trips_incoming_custom_row, parent, false);
        Ongoing_view_holder viewHolder = new Ongoing_view_holder(CustomRowView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Ongoing_view_holder holder, int position) {
        Plan plan = _data.get(position);
        holder.getPlanName().setText(plan.getName());
        holder.getEndDate().setText("Departure date:" + plan.getDeparture_date());
        holder.getTrips_Ongoing_image().setImageResource(DEMO_IMAGE);
    }


    @Override
    public int getItemCount()
    {
        return _data.size();
    }
}
