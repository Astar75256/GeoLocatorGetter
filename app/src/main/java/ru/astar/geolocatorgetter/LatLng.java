package ru.astar.geolocatorgetter;

/**
 * Created by molot on 27.10.2017.
 */

public class LatLng {
    private double latitude = 0;
    private double longitude = 0;

    public LatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
