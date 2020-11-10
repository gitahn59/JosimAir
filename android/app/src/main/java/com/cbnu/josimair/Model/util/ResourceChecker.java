package com.cbnu.josimair.Model.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Handler;


public class ResourceChecker {
    private static ResourceChecker INSTANCE;
    private static boolean isNetworkEnable = false;
    private static Handler mHandler;
    private ResourceChecker(Handler handler){
        mHandler = handler;
    }

    public boolean isNetworkEnable() {
        return isNetworkEnable;
    }

    public static ResourceChecker getInstance(Context context, Handler handler){
        if(INSTANCE==null){
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
                NetworkRequest.Builder builder = new NetworkRequest.Builder();
                // NetworkCallback을 등록함
                connectivityManager.registerNetworkCallback(builder.build(), new NetworkCallback());
                INSTANCE = new ResourceChecker(handler);
            }catch (Exception e){
                return INSTANCE = null;
            }
        }
        return INSTANCE;
    }

    /**
     * ConnectivityManager.NetworkCallback 를 상속한 Class
     * Network State의 상태 변화에 따라 Callback을 호출함
     */
    private static class NetworkCallback extends ConnectivityManager.NetworkCallback{
        @Override
        public void onAvailable(Network network) {
            isNetworkEnable = true;
            mHandler.obtainMessage(Constants.NETWORK_AVAILABLE).sendToTarget();
        }
        @Override
        public void onLost(Network network) {
            isNetworkEnable = false;
        }
    }

}