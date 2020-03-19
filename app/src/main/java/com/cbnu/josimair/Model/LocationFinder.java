package com.cbnu.josimair.Model;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.cbnu.josimair.ui.MainBtmActivity;

import java.util.List;

public class LocationFinder extends Service implements LocationListener {
    // error occurred event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface locationFindedListener {
        void onlocationFindedEvent(Location location);
    }
    private locationFindedListener mlocationFindedListener;

    public void setlocationFindedEvent(locationFindedListener listener){ mlocationFindedListener = listener; }
    ///////////////////////////////////////////////////////////////////////////////////////////


    private final Context context;

    public boolean isEnabled() {
        if(context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER))
                return true;
        }
        return false;
    }

    Location location;
    private LocationManager locationManager;
    public LocationFinder(Context context) {
        this.context = context;
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setLocation(){
        try {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 10, this);
            }
            else if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 60, 10, this);
            }
        } catch (SecurityException se) {

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        MainBtmActivity.svc.setLocation(location);
        MainBtmActivity.svc.start();
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
}
