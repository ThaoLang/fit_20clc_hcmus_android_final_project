package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.app.Activity;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Detailed_plan_destination_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Detailed_Plan_Destination_Adapter extends RecyclerView.Adapter<Detailed_plan_destination_view_holder>{
    private Context _context;
    private List<Destination> _data;

    private String vnLocale = "vi-VN";


    public Detailed_Plan_Destination_Adapter(Context context, List<Destination> data)
    {
        _context = context;
        _data = data;
    }

    @NonNull
    @Override
    public Detailed_plan_destination_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        View customView = layoutInflater.inflate(R.layout.detailed_plan_custom_row, parent, false);
        Detailed_plan_destination_view_holder itemView = new Detailed_plan_destination_view_holder(customView);
        return itemView;
    }

    @Override
    public void onBindViewHolder(@NonNull Detailed_plan_destination_view_holder holder, int position) {
        Destination destination = _data.get(position);
        Locale locale = new Locale(vnLocale);
        LocalDate presentStartDate = LocalDate.parse(destination.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
        LocalDate presentEndDate = LocalDate.parse(destination.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
        LocalTime presentStartTime = LocalTime.parse(destination.getStartTime(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
        LocalTime presentEndTime = LocalTime.parse(destination.getEndTime(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));

        holder.getDestname().setText(destination.getName());
        holder.getStartdate().setText(presentStartDate.toString());
        holder.getEnddate().setText(presentEndDate.toString());
        holder.getStarttime().setText(presentStartTime.toString());
        holder.getEndtime().setText(presentEndTime.toString());
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }
}
