package com.example.fit_20clc_hcmus_android_final_project;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class LocationInfo extends AppCompatActivity implements OnMapReadyCallback{
    private LocationInfoBinding binding;
    private GoogleMap googleMap;

    LinearLayoutManager mLinearLayoutManager;

//    private Post[] posts={
//        new Post(R.drawable.bali,"Lang Thao","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Khanh Nguyen","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Hoai Phuong","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Minh Quang","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Toan Hao","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Minh Tri","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//        new Post(R.drawable.bali,"Minh Thong","22/2/2022",R.drawable.bali,"Bali 5N4D hot Instagram from HCM",5,5000000);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= LocationInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setBackgroundColor(Color.TRANSPARENT);

        //Image Slider
        ArrayList<SlideModel> slideModels=new ArrayList<>();

        slideModels.add(new SlideModel("https://c4.wallpaperflare.com/wallpaper/179/915/685/photography-water-reflection-bali-wallpaper-preview.jpg", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://media.istockphoto.com/id/675172642/photo/pura-ulun-danu-bratan-temple-in-bali.jpg?b=1&s=170667a&w=0&k=20&c=i6eVZIrC53B4jl-I4p3YIn9ZRViyVoMbRdp-NznLDUE=", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT1hb-8L0Oq5cv-Vitl1Ik-gNDNgFvft3kVDA&usqp=CAU", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSyV19E5ub10XLIFUzqB5HI3Slvhe7p5_tkb4sKoZybnlUyW6wW6Uu2wdhYkP0DDpACzow&usqp=CAU", ScaleTypes.FIT));
        slideModels.add(new SlideModel("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT1hb-8L0Oq5cv-Vitl1Ik-gNDNgFvft3kVDA&usqp=CAU", ScaleTypes.FIT));

        binding.imageSlider.setImageList(slideModels,ScaleTypes.FIT);

        //Introduction text
//        binding.readMoreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (binding.locationDescription.getMaxLines() == 4) {
//                    binding.locationDescription.setMaxLines(Integer.MAX_VALUE);
//                    binding.readMoreBtn.setText(R.string.read_less);
//                } else {
//                    binding.locationDescription.setMaxLines(4);
//                    binding.readMoreBtn.setText(R.string.read_more);
//                }
//            }
//        });

        binding.textShort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.locationDescription.getMaxLines() == 4) {
                    binding.locationDescription.setMaxLines(Integer.MAX_VALUE);
                    binding.textShort.setText("...View less");
                } else {
                    binding.locationDescription.setMaxLines(4);
                    binding.textShort.setText("...View more");
                }
            }
        });

        //Small map
        binding.mapView.getMapAsync(this);
        binding.mapView.onCreate(savedInstanceState);

        //Recent trips
        mLinearLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL,false);
        mLinearLayoutManager.setStackFromEnd(true);

        binding.listPost.setLayoutManager(mLinearLayoutManager);

        binding.listPost.setAdapter(new PostAdapter(this));
        binding.listPost.smoothScrollToPosition(0);

    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState,outPersistentState);
        binding.mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap Map) {
        googleMap = Map;

        // Set the map type to be normal
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set the initial position of the map
        LatLng myPosition = new LatLng(-8.4095, 115.1889);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 12.0f));

        // Add a marker to the map
        googleMap.addMarker(new MarkerOptions().position(myPosition).title("Bali"));

        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
