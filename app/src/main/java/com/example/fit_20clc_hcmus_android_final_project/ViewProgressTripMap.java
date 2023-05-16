package com.example.fit_20clc_hcmus_android_final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.VoidFunction;
import com.example.fit_20clc_hcmus_android_final_project.adapter.Detailed_Plan_Destination_Adapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ViewMapBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ViewProgressTripMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.ui.IconGenerator;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ViewProgressTripMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private int locationFocusIndex=-1;
    private List<Destination> listOfDestinations;
    private IconGenerator iconGenerator;
    private ViewProgressTripMapBinding binding;
    private int defaultMarkerSize;
    private Marker selectedMarker=null;
    private LatLngBounds.Builder builder;
    private String planId="";

    private Marker lastMarker=null;
    private LinearLayoutManager travelerLinearLayoutManager;
    private int markerWidthSize=0;
    private int markerHeightSize=0;
    private Bitmap lastBitmap=null;
    private int currentDestinationIndex=0;

    VoidFunction OnStart=()->{
        onStart();
        Log.e("VOID FUNCTION","HELLO");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_progress_trip_map);
        binding = ViewProgressTripMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            planId=bundle.getString("PLAN_ID");
            listOfDestinations= (List<Destination>) bundle.getSerializable("All destinations");
            Log.e("SIZE DESTINATION MAP",String.valueOf(listOfDestinations.size()));
        }

        iconGenerator= new IconGenerator(this);

        // Thiết lập màu nền
        iconGenerator.setTextAppearance(R.style.MarkerText); // Thiết lập kiểu chữ
        iconGenerator.setContentPadding(8, 8, 8, 0); // Thiết lập khoảng cách nội dung và biên

        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        binding.refreshMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(lastBitmap, markerWidthSize , markerHeightSize , false)));

                lastMarker.setAnchor(0.5f, 1f);
            }
        });


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int halfScreenHeight = displayMetrics.heightPixels / 2;
        bottomSheetBehavior.setPeekHeight(halfScreenHeight);


        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheetBehavior.setPeekHeight(100);
                } else if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
                    bottomSheetBehavior.setPeekHeight(halfScreenHeight);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Không cần xử lý trong trường hợp này
            }



        });

    }

    private Bitmap addWhiteBorder(Bitmap bmp, int borderSize) {
        Bitmap result = Bitmap.createBitmap(bmp.getWidth() + borderSize * 2, bmp.getHeight() + borderSize * 2, bmp.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bmp, borderSize, borderSize, null);
        return result;
    }




    @Override
    public boolean onMarkerClick(Marker marker) {

        LatLng position = marker.getPosition();
        // Di chuyển camera tới vị trí của marker
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));

        iconGenerator.setColor(Color.parseColor(getMarkerColor(Integer.parseInt(marker.getSnippet())-1)));
        Bitmap bitmap = iconGenerator.makeIcon(marker.getSnippet());
        markerWidthSize=bitmap.getWidth();
        markerHeightSize=bitmap.getHeight();

//        if (lastMarker!=null){
//            lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, markerWidthSize , markerHeightSize , false)));
//        }
        lastMarker=marker;
        lastBitmap=bitmap;
        //lastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(lastBitmap, markerWidthSize , markerHeightSize , false)));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, markerWidthSize * 2, markerHeightSize * 2, false)));

        // Thiết lập anchor cho Marker khi phóng to
        marker.setAnchor(0.5f, 1.0f);

        //binding.locationIcon.setImageBitmap(bitmap);
//        binding.cardContainer.setVisibility(View.VISIBLE);
//
//        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                LatLngBounds bounds = builder.build();
//                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
//                binding.cardContainer.setVisibility(View.GONE);
//                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, markerWidthSize , markerHeightSize , false)));
//
//                marker.setAnchor(0.5f, 1f);
//            }
//        });


        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from( binding.standardBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);




        //binding.detailedPlanRecyclerviewDestinations.getI

        View itemView = binding.detailedPlanRecyclerviewDestinations.getChildAt(Integer.parseInt(marker.getSnippet())-1);


        if (itemView != null) {
            itemView.requestFocus();
            binding.detailedPlanRecyclerviewDestinations.smoothScrollToPosition(Integer.parseInt(marker.getSnippet())-1);
            //itemView.setBackgroundColor(Color.parseColor("#0F7764"));
        }

        //binding.standardBottomSheet.setPeekHeight
        return true;
    }
    @Override
    public void onStart() {
        super.onStart();
        Log.e("LIST DESTINATION IDDD",String.valueOf(listOfDestinations.size()));
        Detailed_Plan_Destination_Adapter adapter = new Detailed_Plan_Destination_Adapter(ViewProgressTripMap.this, listOfDestinations, null, planId,true);
        adapter.setOnStartActivity(OnStart);
        binding.detailedPlanRecyclerviewDestinations.setAdapter(adapter);

        //get current destination
        CollectionReference roomCol=DatabaseAccess.getFirestore().collection(DatabaseAccess.ACCESS_ROOM_COLLECTION);
        DocumentReference planDoc=roomCol.document(planId);
        CollectionReference passengerCol=planDoc.collection(DatabaseAccess.ACCESS_SUB_PASSENGER_COLLECTION);
        passengerCol.whereEqualTo("email",DatabaseAccess.getMainUserInfo().getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@androidx.annotation.NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    currentDestinationIndex = Integer.parseInt(document.get("currentDestination").toString());
                                }

                                Log.e("Last POST",String.valueOf(currentDestinationIndex));
                                for (int i=0;i<listOfDestinations.size();i++){
                                    View itemView = binding.detailedPlanRecyclerviewDestinations.getChildAt(i);

                                    if (i <currentDestinationIndex) {
                                        itemView.setBackgroundColor(Color.parseColor("#999494"));
                                        itemView.findViewById(R.id.checkin_btn).setBackgroundColor(Color.parseColor("#4fb355"));
                                        itemView.findViewById(R.id.checkin_btn).setEnabled(false);
                                    }
                                    else if(i==currentDestinationIndex) {
                                        itemView.requestFocus();
                                        binding.detailedPlanRecyclerviewDestinations.smoothScrollToPosition(currentDestinationIndex);
                                        itemView.setBackgroundColor(Color.parseColor("#5EAC8B"));
                                    }
                                    else{
                                        itemView.findViewById(R.id.checkin_btn).setEnabled(false);
                                    }

                                    if(currentDestinationIndex==listOfDestinations.size()){
                                        binding.doneTxt.setVisibility(View.VISIBLE);
                                        binding.doneImg.setVisibility(View.VISIBLE);
                                    }

                                }


                            }
                        }
                        else
                        {
                            // no notification
                        }
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onLowMemory();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Thiết lập giới hạn tọa độ hiển thị của bản đồ
        builder = new LatLngBounds.Builder();
        for (int i=0;i<listOfDestinations.size();i++) {
            double latitude=0;
            double longitude=0;
            Geocoder geocoder = new Geocoder(getApplicationContext());

            try {
                List<Address> addresses = geocoder.getFromLocationName(listOfDestinations.get(i).getFormalName(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address addressResult = addresses.get(0);
                    latitude = addressResult.getLatitude();
                    longitude = addressResult.getLongitude();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            iconGenerator.setColor(Color.parseColor(getMarkerColor(i)));
            Bitmap bitmap = iconGenerator.makeIcon(String.valueOf(i+1)); // Tạo Bitmap với số bên trong
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap); // Thêm viền trắng cho Bitmap và chuyển thành BitmapDescriptor

            LatLng latLng = new LatLng(latitude, longitude);
            builder.include(latLng);
            Marker marker=mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(listOfDestinations.get(i).getFormalName())
                    .snippet(String.valueOf(i+1))
                    .icon(descriptor));

            if (locationFocusIndex==i){
                selectedMarker=marker;
            }

            defaultMarkerSize = bitmap.getWidth();
            marker.setTag(defaultMarkerSize);
        }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        // Đăng ký sự kiện OnMarkerClickListener
        mMap.setOnMarkerClickListener(this);

        if(locationFocusIndex!=-1){
            onMarkerClick(selectedMarker);
        }
    }

    private String getMarkerColor(int index) {
        // Thiết lập màu sắc cho từng Marker dựa trên số thứ tự của địa điểm
        switch (index) {
            case 0:
                return "#FF4081";
            case 1:
                return "#4fb355";
            case 2:
                return "#2292d4";
            case 3:
                return "#048781";
            case 4:
                return "#e86417";
            case 5:
                return "#5EAC8B";
            case 6:
                return "#1294ab";
            case 7:
                return "#6750A4";
            case 8:
                return "#34acc7";
            case 9:
                return "#4463ad";
            default:
                return "#6c73e0";
        }
    }

    // Phương thức để thay đổi kích thước của Bitmap
    public Bitmap resizeBitmap(Bitmap bitmap, float newSize) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, (int) newSize, (int) newSize, false);
        return resizedBitmap;
    }
}
