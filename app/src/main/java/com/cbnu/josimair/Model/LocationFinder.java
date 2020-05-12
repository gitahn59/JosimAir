package com.cbnu.josimair.Model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class LocationFinder {
    private static LocationFinder INSTANCE;
    private final Context context;
    private final Handler handler;

    public Location getLocation() {
        try {
            Location location = null;
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if(location!=null) return location;

            if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if(location!=null) return location;
        } catch (SecurityException se) {
        }
        return null;
    }

    LocationManager locationManager;

    private LocationFinder(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static LocationFinder getInstance(Context context, Handler handler){
        if(INSTANCE == null){
            INSTANCE = new LocationFinder(context, handler);
        }
        return INSTANCE;
    }

    public boolean isEnabled() {
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER))
                return true;
        }
        return false;
    }

    public void requestLocationUpdates(){
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 20, 0, locationListener);
            }
            if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 20, 0, locationListener);
            }
        } catch (SecurityException se) {
        }
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            handler.obtainMessage(Constants.GPS_UPDATED,location).sendToTarget();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
