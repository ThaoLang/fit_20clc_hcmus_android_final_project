package com.example.fit_20clc_hcmus_android_final_project.custom_view_holder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.zip.Inflater;

public class InviteFriendViewHolder extends RecyclerView.ViewHolder {

    private ShapeableImageView traveler_image;
    private MaterialTextView traveler_name;
    private MaterialButton mainButton;

    public InviteFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        traveler_image = itemView.findViewById(R.id.detailed_plan_traveler_custom_row_image);
        traveler_name = itemView.findViewById(R.id.detailed_plan_traveler_custom_row_travelername);
        mainButton = itemView.findViewById(R.id.detailed_plan_traveler_custom_row_main_button);


    }

    public ShapeableImageView getTraveler_image()
    {
        return traveler_image;
    }

    public MaterialTextView getTraveler_name()
    {
        return traveler_name;
    }

    public MaterialButton getMainButton()
    {
        return mainButton;
    }
}
