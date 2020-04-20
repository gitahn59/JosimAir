package com.cbnu.josimair.Model;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;

public class ResourceChecker {
    private Context context;
    private boolean isNetworkEnable;
    public boolean isNetworkEnable() {
        return isNetworkEnable;
    }

    public ResourceChecker(Context context) {
        this.context = context;
        setCheckNetworkState();
    }

    public void setCheckNetworkState(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        isNetworkEnable = true;
                    }
                    @Override
                    public void onLost(Network network) {
                        isNetworkEnable = false;
                    }
              }
            );
        }catch (Exception e){
            isNetworkEnable = false;
        }
    }

}
