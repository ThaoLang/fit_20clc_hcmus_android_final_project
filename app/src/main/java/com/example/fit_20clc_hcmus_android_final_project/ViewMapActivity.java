package com.example.fit_20clc_hcmus_android_final_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ActivityDetailedPostBinding;
import com.example.fit_20clc_hcmus_android_final_project.databinding.ViewMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback, OnMarkerClickListener {

    private GoogleMap mMap;
    private int locationFocusIndex=-1;
    private List<Destination> listOfDestinations;
    private IconGenerator iconGenerator;
    private ViewMapBinding binding;
    private int defaultMarkerSize;
    private Marker selectedMarker=null;
    private LatLngBounds.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_map);
        binding = ViewMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            listOfDestinations= (List<Destination>) bundle.getSerializable("All destinations");
            locationFocusIndex=bundle.getInt("destination index");

        }

        iconGenerator= new IconGenerator(this);

         // Thiết lập màu nền
        iconGenerator.setTextAppearance(R.style.MarkerText); // Thiết lập kiểu chữ
        iconGenerator.setContentPadding(8, 8, 8, 0); // Thiết lập khoảng cách nội dung và biên

        MapView mapView = (MapView) findViewById(R.id.map_view);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


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

        AtomicReference<Destination> selectedDestination= new AtomicReference<>(new Destination());
        listOfDestinations.forEach(des->{
            if (des.getFormalName().equals(marker.getTitle())){
                selectedDestination.set(des);
                binding.locationName.setText(des.getAliasName());
                binding.locationTripTime.setText(des.getStartTime()+" "+
                        des.getStartDate()+" - "+
                        des.getEndTime()+" "+
                        des.getEndDate());
                binding.description.setText(des.getDescription());
                binding.destination.setText(des.getFormalName());
            }
        });
        iconGenerator.setColor(Color.parseColor(getMarkerColor(Integer.parseInt(marker.getSnippet())-1)));
        Bitmap bitmap = iconGenerator.makeIcon(marker.getSnippet());
        int markerWidthSize=bitmap.getWidth();
        int markerHeightSize=bitmap.getHeight();

        marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, markerWidthSize * 2, markerHeightSize * 2, false)));

        // Thiết lập anchor cho Marker khi phóng to
        marker.setAnchor(0.5f, 1.0f);

        binding.locationIcon.setImageBitmap(bitmap);
        binding.cardContainer.setVisibility(View.VISIBLE);

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLngBounds bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                binding.cardContainer.setVisibility(View.GONE);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bitmap, markerWidthSize , markerHeightSize , false)));

                marker.setAnchor(0.5f, 1f);
            }
        });

        return true;
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
