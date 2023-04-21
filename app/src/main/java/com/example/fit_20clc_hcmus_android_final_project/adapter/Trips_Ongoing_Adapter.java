package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.MainActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Incoming_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Ongoing_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Plan;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Trips_Ongoing_Adapter extends RecyclerView.Adapter<Ongoing_view_holder> {
    @DrawableRes
    private int DEMO_IMAGE = R.drawable.hoi_an_ancient_town_hoi_an_private_taxi_2;
    private Context _context;
    private List<Plan> _data = null;

    public Trips_Ongoing_Adapter(Context context, List<Plan> data)
    {
        _context = context;
        if(_data != null)
        {
            _data.clear();
        }
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
        String imageLink = plan.getImageLink();
        if(imageLink.equals("None"))
        {
            holder.getTrips_Ongoing_image().setImageResource(R.drawable.image_48px);
        }
        else
        {
            StorageReference storageReference = DatabaseAccess.getFirebaseStorage().getReference().child(imageLink);
            final long MAX_BYTE = 1024*2*1024;
            storageReference.getBytes(MAX_BYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.getTrips_Ongoing_image().setImageBitmap(image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    holder.getTrips_Ongoing_image().setImageResource(R.drawable.image_48px);
                }
            });
        }
        holder.getPlanName().setText(plan.getName());
        holder.getEndDate().setText("Departure date:" + plan.getDeparture_date());
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Log.i("Trip_incoming", "Item is clicked");
                Intent intent = new Intent(_context, DetailedPlan.class);
                intent.putExtra(DetailedPlan.DETAILED_PLAN_ID, _data.get(position).getPlanId());
                DatabaseAccess.runForegroundTask(((MainActivity)_context).startSpecificActivity(intent));
            }
        });
    }

    public String getPlanByIndex(@NotNull int index)
    {
        if(index < 0 || index > _data.size())
        {
            return null;
        }
        return _data.get(index).getPlanId();
    }
    @Override
    public int getItemCount()
    {
        return _data.size();
    }

}
