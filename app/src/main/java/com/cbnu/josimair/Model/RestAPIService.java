package com.cbnu.josimair.Model;

import android.app.Activity;
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


public class RestAPIService {
    private static final String api = "openapi.airkorea.or.kr";
    private static final String key = "Ykpt%2Ffoyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN%2F7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q%3D%3D";
    private String city;
    private String gu;
    private OutdoorAir air=null;

    public OutdoorAir getAir() {
        return air;
    }

    public RestAPIService(Activity activity, String city, String gu){
        this.city = city;
        this.gu = gu;
    }

    public boolean isArived() {
        return isArived;
    }

    private boolean isArived;

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

    public void start(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                isArived = false;
                Pair<Double,Double> tmLocation = getTMLocation();
                if(tmLocation!=null) {
                    if (getNearByStationInfo(tmLocation)){
                        getOutdoorAir();
                    }
                }else{
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
