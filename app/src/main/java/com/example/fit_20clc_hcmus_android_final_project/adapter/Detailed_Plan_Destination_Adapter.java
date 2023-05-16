package com.example.fit_20clc_hcmus_android_final_project.adapter;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.AddDestination;
import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunction;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.example.fit_20clc_hcmus_android_final_project.DetailedPlan;
import com.example.fit_20clc_hcmus_android_final_project.ItemClickListener;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.ViewProgressTripMap;
import com.example.fit_20clc_hcmus_android_final_project.custom_view_holder.Detailed_plan_destination_view_holder;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Detailed_Plan_Destination_Adapter extends RecyclerView.Adapter<Detailed_plan_destination_view_holder> {
    private Context _context;
    private List<Destination> _data;

    private String vnLocale = "vi-VN";

    private String _planId;

    private ActivityResultLauncher<Intent> _launcher;

    private final static String PERMISSION_CODE = "X31aLjuNNm4j1d";
    private boolean _isOngoing=false;

    VoidFunction voidFunction;

    public Detailed_Plan_Destination_Adapter(Context context, List<Destination> data, ActivityResultLauncher<Intent> inputLauncher) {
        _context = context;
        _data = data;
        _launcher = inputLauncher;
        _planId = "";
    }

    public Detailed_Plan_Destination_Adapter(Context context, List<Destination> data, ActivityResultLauncher<Intent> inputLauncher, String planId, boolean isOngoing) {
        _context = context;
        _data = data;
        _launcher = inputLauncher;
        _planId = planId;
        _isOngoing=isOngoing;
    }

    @NonNull
    @Override
    public Detailed_plan_destination_view_holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(_context);
        View customView = layoutInflater.inflate(R.layout.detailed_plan_custom_row, parent,false);
        Detailed_plan_destination_view_holder itemView = new Detailed_plan_destination_view_holder(customView);
        return itemView;
    }

    @Override
    public void onBindViewHolder(@NonNull Detailed_plan_destination_view_holder holder, int position) {
        Destination destination = _data.get(position);
        Locale locale = new Locale(vnLocale);
//        LocalDate presentStartDate = LocalDate.parse(destination.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
//        LocalDate presentEndDate = LocalDate.parse(destination.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
//        LocalTime presentStartTime = LocalTime.parse(destination.getStartTime(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
//        LocalTime presentEndTime = LocalTime.parse(destination.getEndTime(), DateTimeFormatter.ISO_LOCAL_DATE.withLocale(locale));
//
//        holder.getDestname().setText(destination.getName());
//        holder.getStartdate().setText(presentStartDate.toString());
//        holder.getEnddate().setText(presentEndDate.toString());
//        holder.getStarttime().setText(presentStartTime.toString());
//        holder.getEndtime().setText(presentEndTime.toString());

        holder.getDestname().setText(destination.getAliasName());
        holder.getStartdate().setText(destination.getStartDate());
        holder.getEnddate().setText(destination.getEndDate());
        holder.getStarttime().setText(destination.getStartTime());
        holder.getEndtime().setText(destination.getEndTime());
        holder.getCheckin_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> data = new HashMap<>();
                data.put("email", DatabaseAccess.getMainUserInfo().getUserEmail());
                data.put("currentDestination", position+1);
                CollectionReference roomCol=DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_ROOM_COLLECTION);
                DocumentReference planDoc=roomCol.document(_planId);
                CollectionReference passengerCol=planDoc.collection(DatabaseAccess.ACCESS_SUB_PASSENGER_COLLECTION);
                passengerCol.document(DatabaseAccess.getMainUserInfo().getUserEmail())
                                .update(data);

                Log.e("POS",String.valueOf(position));
                if (position+1==_data.size()){
                    DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_PLANS_COLLECTION)
                            .document(_planId)
                            .update("status","Finished");
                }
//                holder.getCheckin_btn().setBackgroundColor(Color.parseColor("#4fb355"));
//                holder.getCheckin_btn().setEnabled(false);
                if (voidFunction!=null){
                    voidFunction.apply();
                    Log.e("VOID FUNC","YA");
                }

            }
        });
        Log.e("LIST DESTINATION HERE","CHECK CHECK");
        if (_isOngoing)
        {
            holder.getCheckin_btn().setVisibility(View.VISIBLE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick)
            {
                if (_launcher!=null){
                    Intent intent = new Intent(_context, AddDestination.class);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray(DetailedPlan.SPEC_DESTINATION, Destination.toByteArray(_data.get(position)));
                    bundle.putString("SETTING_MODE", AddDestination.VIEW_DESTINATION);
                    bundle.putString("PLAN_ID", _planId);
                    bundle.putInt("INDEX", position);
                    intent.putExtra(DetailedPlan.SPEC_DESTINATION, bundle);
                    _launcher.launch(intent);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public void setOnStartActivity(VoidFunction func){
        voidFunction=func;
    }
}
