package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.material.textview.MaterialTextView;

public class Detailed_plan_destination_view_holder extends RecyclerView.ViewHolder {
    private MaterialTextView destname, startdate, enddate, starttime, endtime;

    public Detailed_plan_destination_view_holder(@NonNull View itemView) {
        super(itemView);
        destname = itemView.findViewById(R.id.detailed_plan_custom_row_destination_name);
        startdate = itemView.findViewById(R.id.detailed_plan_custom_row_startdate);
        enddate = itemView.findViewById(R.id.detailed_plan_custom_row_enddate);
        starttime = itemView.findViewById(R.id.detailed_plan_custom_row_starttime);
        endtime = itemView.findViewById(R.id.detailed_plan_custom_row_endtime);
    }

    public MaterialTextView getDestname()
    {
        return destname;
    }

    public MaterialTextView getStartdate()
    {
        return startdate;
    }

    public MaterialTextView getEnddate()
    {
        return enddate;
    }

    public MaterialTextView getStarttime()
    {
        return starttime;
    }

    public MaterialTextView getEndtime()
    {
        return endtime;
    }

}
