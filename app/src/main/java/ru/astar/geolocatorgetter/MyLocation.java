package ru.astar.geolocatorgetter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * Created by Astar on 20.10.2017.
 */

public class MyLocation {

    public static final String TAG = "My Location";

    private LocationManager locationManager;

    private Context context;
    private LatLng latLng;


    public MyLocation(Context context) {
        this.context = context;
        latLng = new LatLng(0, 0);
        reloadListener();
    }

    public void reloadListener() {
        if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000L, 15, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000L, 15, locationListener);
    }

    public void removeListener() {
        locationManager.removeUpdates(locationListener);
    }

    public LatLng getLocation() {
        LatLng latLng = this.latLng;

        if (latLng == null || latLng.getLatitude() == 0 || latLng.getLongitude() == 0) {

            if (ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return latLng;
            }

            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            double latitude;
            double longitude;

            try {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } catch (NullPointerException e) {
                Log.d(TAG, "" + e.getMessage());
                latitude = 0;
                longitude = 0;
            }

            if (latitude == 0 || longitude == 0) {
                latLng = new LatLng(latitude, longitude);
            }
        }

        return latLng;
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

}