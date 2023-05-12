package com.example.fit_20clc_hcmus_android_final_project.chat_fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.fit_20clc_hcmus_android_final_project.ChatActivity;
import com.example.fit_20clc_hcmus_android_final_project.R;
import com.example.fit_20clc_hcmus_android_final_project.data_struct.Location;
import com.example.fit_20clc_hcmus_android_final_project.databinding.FragmentChatMapBinding;
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
import com.google.maps.android.ui.IconGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private static final String ARG_PARAM1 = "param1";

    private ChatActivity chat_activity;
    private Context context;
    private String currentTripId;

    private FragmentChatMapBinding binding;
    private static LocalBroadcastManager localBroadcastManager;

    private GoogleMap mMap;
//    private int locationFocusIndex=-1;
    private List<Location> listOfFriendLocations;
    private IconGenerator iconGenerator;

    BroadcastReceiver locationReceiver;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentTripId = getArguments().getString(ARG_PARAM1);
        }

        try {
            context = getActivity();
            chat_activity = (ChatActivity) getActivity();
            if (localBroadcastManager == null) {
                localBroadcastManager = LocalBroadcastManager.getInstance(context.getApplicationContext());
            }
        } catch (IllegalStateException e) {
            throw new IllegalStateException("ChatActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatMapBinding.inflate(inflater, container, false);

        listOfFriendLocations = getFriendsLocation();

        iconGenerator= new IconGenerator(context);

        iconGenerator.setColor(Color.parseColor("#FF4081")); // Thiết lập màu nền
        iconGenerator.setTextAppearance(R.style.MarkerText); // Thiết lập kiểu chữ
        iconGenerator.setContentPadding(8, 8, 8, 0); // Thiết lập khoảng cách nội dung và biên

        binding.mapView.onCreate(savedInstanceState);
        binding.mapView.getMapAsync(this);

        return binding.getRoot();
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
        Toast.makeText(context, marker.getTitle() + "\n" + marker.getSnippet(), Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.mapView.onLowMemory();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Register the BroadcastReceiver with the correct action string
        localBroadcastManager.registerReceiver(locationReceiver, new IntentFilter("com.example.ACTION_UPDATE_CAMERA_CENTER"));
    }

    @Override
    public void onStop() {
        super.onStop();
        // Unregister the BroadcastReceiver when the fragment is stopped
        localBroadcastManager.unregisterReceiver(locationReceiver);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (listOfFriendLocations.size()==0) return;

        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if(ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(chat_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            ActivityCompat.requestPermissions(chat_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 44);
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().isCompassEnabled();
        mMap.getUiSettings().isMapToolbarEnabled();

        // Thiết lập giới hạn tọa độ hiển thị của bản đồ
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i=0; i<listOfFriendLocations.size(); i++) {
            double latitude = Double.parseDouble(listOfFriendLocations.get(i).getLatitude());
            double longitude = Double.parseDouble(listOfFriendLocations.get(i).getLongitude());
//            Geocoder geocoder = new Geocoder(chat_activity.getApplicationContext());
//
//            try {
//                List<Address> addresses = geocoder.getFromLocationName(listOfFriendLocations.get(i).getFormalName(), 1);
//                if (addresses != null && !addresses.isEmpty()) {
//                    Address addressResult = addresses.get(0);
//                    latitude = Double.valueOf(addressResult.getLatitude());
//                    longitude = Double.valueOf(addressResult.getLongitude());
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }

            Bitmap bitmap = iconGenerator.makeIcon(String.valueOf(i+1)); // Tạo Bitmap với số bên trong
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(addWhiteBorder(bitmap, 0)); // Thêm viền trắng cho Bitmap và chuyển thành BitmapDescriptor

            LatLng latLng = new LatLng(latitude, longitude);
            builder.include(latLng);
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(listOfFriendLocations.get(i).getName())
                    .snippet(listOfFriendLocations.get(i).getFormalName())
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

    private List<Location> getFriendsLocation(){
        List<Location> list = new ArrayList<>();
        locationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get the updated camera center data from the intent
                double lat = Double.parseDouble(intent.getStringExtra("latitude"));
                double lng = Double.parseDouble(intent.getStringExtra("longitude"));
                String provider = String.valueOf(intent.getStringExtra("longitude"));
                String locationDetail = String.valueOf(lat) + String.valueOf(lng);

                Location location = new Location();
                location.setLatitude(String.valueOf(lat));
                location.setLongitude(String.valueOf(lng));

                location.setName(provider);
                location.setFormalName(locationDetail);

                list.add(location);
            }
        };
        return list;
    }
}
