package com.example.fit_20clc_hcmus_android_final_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.example.fit_20clc_hcmus_android_final_project.data_struct.Destination;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ViewMapActivity extends AppCompatActivity implements OnMapReadyCallback, OnMarkerClickListener {

    private GoogleMap mMap;
    private int locationFocusIndex=-1;
    private List<Destination> listOfDestinations;
    private IconGenerator iconGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_map);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            listOfDestinations= (List<Destination>) bundle.getSerializable("All destinations");
            locationFocusIndex=bundle.getInt("destination index");
        }

        iconGenerator= new IconGenerator(this);

        iconGenerator.setColor(Color.parseColor("#FF4081")); // Thiết lập màu nền
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
        // Hiển thị thông tin địa điểm khi click vào Marker
        Toast.makeText(this, marker.getTitle() + "\n" + marker.getSnippet(), Toast.LENGTH_SHORT).show();
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
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

            Bitmap bitmap = iconGenerator.makeIcon(String.valueOf(i+1)); // Tạo Bitmap với số bên trong
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(addWhiteBorder(bitmap, 0)); // Thêm viền trắng cho Bitmap và chuyển thành BitmapDescriptor

            LatLng latLng = new LatLng(latitude, longitude);
            builder.include(latLng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(listOfDestinations.get(i).getAliasName())
                    .snippet(listOfDestinations.get(i).getDescription())
                    .icon(descriptor));

            //BitmapDescriptorFactory.defaultMarker(getMarkerColor(i)))
        }
        LatLngBounds bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        // Đăng ký sự kiện OnMarkerClickListener
        mMap.setOnMarkerClickListener(this);
    }

    private float getMarkerColor(int index) {
        // Thiết lập màu sắc cho từng Marker dựa trên số thứ tự của địa điểm
        switch (index) {
            case 0:
                return BitmapDescriptorFactory.HUE_RED;
            case 1:
                return BitmapDescriptorFactory.HUE_BLUE;
            case 2:
                return BitmapDescriptorFactory.HUE_GREEN;
            case 3:
                return BitmapDescriptorFactory.HUE_ORANGE;
            case 4:
                return BitmapDescriptorFactory.HUE_AZURE;
            case 5:
                return BitmapDescriptorFactory.HUE_CYAN;
            case 6:
                return BitmapDescriptorFactory.HUE_VIOLET;
            case 7:
                return BitmapDescriptorFactory.HUE_ROSE;
            case 8:
                return BitmapDescriptorFactory.HUE_MAGENTA;
            default:
                return BitmapDescriptorFactory.HUE_YELLOW;
        }
    }
}
