package com.example.fit_20clc_hcmus_android_final_project;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.fit_20clc_hcmus_android_final_project.adapter.PostAdapter;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Location;
import com.example.fit_20clc_hcmus_android_final_project.databinding.LocationInfoBinding;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class LocationInfo extends AppCompatActivity implements OnMapReadyCallback{
    private LocationInfoBinding binding;
    private GoogleMap googleMap;

    private double latitude=0;
    private double longitude=0;
    String locationName="";

    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding= LocationInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle= getIntent().getBundleExtra("location search");
        locationName=bundle.get("location address").toString();
        ArrayList<SlideModel> slideModels=new ArrayList<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("location")
                .whereEqualTo("formalName", locationName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (!querySnapshot.isEmpty()) {
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    // Xử lí khi có kết quả trả về
                                    locationName = document.get("formalName").toString();
                                    binding.introLabel.setText("About " + document.get("name").toString());
                                    binding.locationDescription.setText(document.get("description").toString());

                                    List<String> imagesList = (List<String>) document.get("images");
                                    for (String imageUrl : imagesList) {
                                        Log.e("Image URL", imageUrl);
                                        slideModels.add(new SlideModel(imageUrl, ScaleTypes.FIT));
                                    }
                                    binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                                }
                            } else {
                                // Xử lí khi không có kết quả trả về
                                slideModels.add(new SlideModel("https://img.freepik.com/free-vector/happy-family-travelling-by-car-with-camping-equipment-top_74855-10751.jpg", ScaleTypes.FIT));
                                binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.locationDescription.getLayoutParams();
                                params.setMargins(30, 0, 0, 0);
                                binding.locationDescription.setLayoutParams(params);
                                binding.textShort.setVisibility(View.GONE);
                            }

                        } else {
                            // Xử lí khi có lỗi xảy ra
                            slideModels.add(new SlideModel("https://img.freepik.com/free-vector/happy-family-travelling-by-car-with-camping-equipment-top_74855-10751.jpg", ScaleTypes.FIT));
                            binding.imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                        }

                    }
                });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

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
        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            List<Address> addresses = geocoder.getFromLocationName(locationName, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address addressResult = addresses.get(0);
                latitude = addressResult.getLatitude();
                longitude = addressResult.getLongitude();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        googleMap = Map;

        // Set the map type to be normal
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Set the initial position of the map
        LatLng myPosition = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 12.0f));

        // Add a marker to the map
        googleMap.addMarker(new MarkerOptions().position(myPosition).title(locationName));

        // Enable zoom controls
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
}
