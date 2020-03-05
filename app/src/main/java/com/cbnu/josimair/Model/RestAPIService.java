package com.cbnu.josimair.Model;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;


public class RestAPIService {
    // received event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface ReceivedListener{
        void onReceivedEvent(OutdoorAir air);
    }
    private ReceivedListener mReceivedListener;
    public void setOnReceivedEvent(ReceivedListener listener){
        mReceivedListener = listener;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////

    // error occurred event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface ErrorOccurredListener{
        void onErrorOccurredEvent();
    }
    private ErrorOccurredListener mErrorOccurredListener;

    public void setOnErrorOccurredEvent(ErrorOccurredListener listener){ mErrorOccurredListener = listener; }
    ///////////////////////////////////////////////////////////////////////////////////////////

    // error occurred event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface preparedListener {
        void onPreparedEvent();
    }
    private preparedListener mPreparedListener;

    public void setPreparedEvent(preparedListener listener){ mPreparedListener = listener; }
    ///////////////////////////////////////////////////////////////////////////////////////////

    private static final String api = "openapi.airkorea.or.kr";
    private static final String key = "Ykpt%2Ffoyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN%2F7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q%3D%3D";
    private String city;
    private String gu;
    private OutdoorAir air=null;
    private static boolean isNetworkConnected=false;
    private Activity activity;
    private Timer timer;
    private int period=3600000;

    public void setLocation(String city, String gu){
        this.city = city;
        this.gu = gu;
    }

    public RestAPIService(Activity activity){
        this.activity=activity;
        this.air=null;
        setCheckNetworkState();
    }

    public void setCheckNetworkState(){
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            RestAPIService.isNetworkConnected = true; // Global Static Variable
                            checkPrepared();
                        }
                        @Override
                        public void onLost(Network network) {
                            RestAPIService.isNetworkConnected = false; // Global Static Variable
                        }
                    }
            );
        }catch (Exception e){
            RestAPIService.isNetworkConnected = false;
        }
    }

    public void checkPrepared(){
        if(isGranted() && RestAPIService.isNetworkConnected) {
            if(mPreparedListener!=null)
                mPreparedListener.onPreparedEvent();
        }
    }

    public boolean isGranted() {
        if(activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    public OutdoorAir getAir() {
        return air;
    }

    public void start(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                task();
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,period);
    }

    public void task(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Pair<Double,Double> tmLocation = getTMLocation();
                if(tmLocation!=null) {
                    if (getNearByStationInfo(tmLocation)){
                        getOutdoorAir();
                    }
                }else{
                    timer.cancel();
                    mErrorOccurredListener.onErrorOccurredEvent();
                }
            }
        });
    }

    private String makeOutdoorAirUrl(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(api)
                .appendPath("openapi")
                .appendPath("services")
                .appendPath("rest")
                .appendPath("ArpltnInforInqireSvc")
                .appendPath("getCtprvnRltmMesureDnsty")
                .appendQueryParameter("pageNo","1")
                .appendQueryParameter("numOfRows","50")
                .appendQueryParameter("ver","1.3")
                .appendQueryParameter("_returnType","json");
        String url = builder.build().toString();
        url += ("&ServiceKey=" + key);
        url += ("&sidoName=" + city);

        return url;
    }

    private String makeTMLocationUrl(){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(api)
                .appendPath("openapi")
                .appendPath("services")
                .appendPath("rest")
                .appendPath("MsrstnInfoInqireSvc")
                .appendPath("getTMStdrCrdnt")
                .appendQueryParameter("pageNo","1")
                .appendQueryParameter("numOfRows","50")
                .appendQueryParameter("_returnType","json");
        String url = builder.build().toString();
        url += ("&ServiceKey=" + key);
        url += ("&umdName=" + gu);

        return url;
    }

    private String makeNearbyStationUrl(Pair<Double,Double> tmLocation){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(api)
                .appendPath("openapi")
                .appendPath("services")
                .appendPath("rest")
                .appendPath("MsrstnInfoInqireSvc")
                .appendPath("getNearbyMsrstnList")
                .appendQueryParameter("pageNo","1")
                .appendQueryParameter("numOfRows","10")
                .appendQueryParameter("_returnType","json");
        String url = builder.build().toString();
        url += ("&ServiceKey=" + key);
        url += ("&tmX=" + tmLocation.first);
        url += ("&tmY=" + tmLocation.second);

        return url;
    }

    private Pair<Double,Double> getTMLocation(){
        try {
            //checkNetwork();
            String json=getJsonResult(makeTMLocationUrl());
            JsonParser jp = new JsonParser(json);
            return jp.getTMLocation();
        }catch(Exception e){
            return null;
        }
    }

    private boolean getNearByStationInfo(Pair<Double,Double> tmLocation){
        try {
            String json=getJsonResult(makeNearbyStationUrl(tmLocation));
            JsonParser jp = new JsonParser(json);
            this.gu = jp.getNearByStation();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private void getOutdoorAir(){
        try {
            String json=getJsonResult(makeOutdoorAirUrl());
            JsonParser jp = new JsonParser(json);
            this.air = jp.getOutdoor(this.city,this.gu);
            mReceivedListener.onReceivedEvent(this.air);
        }
        catch(Exception e){
            mErrorOccurredListener.onErrorOccurredEvent();
        }
    }

    private String getJsonResult(String url){
        try {
            URL endpoint = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestProperty("Accept", "application/json");

            if(connection.getResponseCode()!=200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) response.append(inputLine);
            in.close();
            return response.toString();
        }catch(Exception e) {
            return null;
        }
    }

}
