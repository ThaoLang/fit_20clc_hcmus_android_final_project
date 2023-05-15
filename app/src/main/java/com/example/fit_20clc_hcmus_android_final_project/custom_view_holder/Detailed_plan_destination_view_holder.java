package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

public class Detailed_plan_destination_view_holder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private MaterialTextView destname, startdate, enddate, starttime, endtime;
    private MaterialButton checkin_btn;

    private ItemClickListener itemClickListener;

    public Detailed_plan_destination_view_holder(@NonNull View itemView) {
        super(itemView);
        destname = itemView.findViewById(R.id.detailed_plan_custom_row_destination_name);
        startdate = itemView.findViewById(R.id.detailed_plan_custom_row_startdate);
        enddate = itemView.findViewById(R.id.detailed_plan_custom_row_enddate);
        starttime = itemView.findViewById(R.id.detailed_plan_custom_row_starttime);
        endtime = itemView.findViewById(R.id.detailed_plan_custom_row_endtime);
        checkin_btn=itemView.findViewById(R.id.checkin_btn);

/*        checkin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkin_btn.setBackgroundColor(Color.parseColor("#4fb355"));
                checkin_btn.setEnabled(false);
            }
        });*/

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
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

    public MaterialButton getCheckin_btn(){ return checkin_btn;};

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
