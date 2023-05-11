package com.example.fit_20clc_hcmus_android_final_project.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class GPSService extends Service{
    String GPS_FILTER = "matos.action.GPSFIX";
    Thread serviceThread;
    LocationManager lm;
    GPSListener myLocationListener;

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e("<<MyGpsService-onStart>>", "I am alive-GPS!");
        serviceThread = new Thread(this::getGPS);
        serviceThread.start();
    }

    public void getGPS() {
        try {
            Looper.prepare();
            // try to get your GPS location using the LOCATION.SERVICE provider
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            // This listener will catch and disseminate location updates
            myLocationListener = new GPSListener();
            // define update frequency for GPS readings
            long minTime = 2000; // 2 seconds
            float minDistance = 0; // 0 meter
            // request GPS updates
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(new Activity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                return;
            }
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, myLocationListener);
            Looper.loop();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class GPSListener implements LocationListener {
        public void onLocationChanged(Location location) {
            // capture location data sent by current provider
            double latitude = location.getLatitude(), longitude = location.getLongitude();
            // assemble data bundle to be broadcast
            Intent myFilteredResponse = new Intent(GPS_FILTER);
            myFilteredResponse.putExtra("latitude", latitude);
            myFilteredResponse.putExtra("longitude", longitude);
            myFilteredResponse.putExtra("provider", location.getProvider());
            Log.e(">>GPS_Service<<", "Lat:" + latitude + " lon:" + longitude);
            // send the location data out
            sendBroadcast(myFilteredResponse);
        }
        public void onProviderDisabled(String provider) { }
        public void onProviderEnabled(String provider) { }
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }
    // GPSListener class
}