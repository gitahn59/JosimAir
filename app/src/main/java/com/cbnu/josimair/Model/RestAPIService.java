package com.cbnu.josimair.Model;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ResultReceiver;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;


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

    private static final String airkorea_api = "openapi.airkorea.or.kr";
    private static final String airkorea_key = "yourKey";

    private static final String kakao_api = "dapi.kakao.com";
    private static final String kakao_key = "yourKey";

    private OutdoorAir air=null;
    private Context context;
    private Timer timer;
    private int period=3600000;
    private String stationName;
    private ResourceChecker checker;
    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public RestAPIService(Activity context){
        this.context = context;
        this.air=null;
        this.checker = new ResourceChecker(context);
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
                .authority(airkorea_api)
                .appendPath("openapi")
                .appendPath("services")
                .appendPath("rest")
                .appendPath("ArpltnInforInqireSvc")
                .appendPath("getMsrstnAcctoRltmMesureDnsty")
                .appendQueryParameter("pageNo","1")
                .appendQueryParameter("numOfRows","1")
                .appendQueryParameter("ver","1.3")
                .appendQueryParameter("_returnType","json");
        String url = builder.build().toString();
        url += ("&ServiceKey=" + airkorea_key);
        url += ("&stationName=" + stationName);
        url += ("&dataTerm=DAILY");
        return url;
    }

    private String makeNearbyStationUrl(Pair<Double,Double> tmLocation){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority(airkorea_api)
                .appendPath("openapi")
                .appendPath("services")
                .appendPath("rest")
                .appendPath("MsrstnInfoInqireSvc")
                .appendPath("getNearbyMsrstnList")
                .appendQueryParameter("pageNo","1")
                .appendQueryParameter("numOfRows","10")
                .appendQueryParameter("_returnType","json");
        String url = builder.build().toString();
        url += ("&ServiceKey=" + airkorea_key);
        url += ("&tmX=" + tmLocation.first);
        url += ("&tmY=" + tmLocation.second);

        return url;
    }

    public String makeTMLocationUrl(Double log, Double lat){
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(kakao_api)
                .appendPath("v2")
                .appendPath("local")
                .appendPath("geo")
                .appendPath("transcoord.json")
                .appendQueryParameter("x",log.toString())
                .appendQueryParameter("y",lat.toString())
                .appendQueryParameter("input_coord","WGS84")
                .appendQueryParameter("output_coord","TM");
        String url = builder.build().toString();
        return url;
    }

    private Pair<Double,Double> getTMLocation(){
        try {
            String json=getJsonResult(makeTMLocationUrl(location.getLongitude(),location.getLatitude()),kakao_key);
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
            this.stationName = jp.getNearByStation();
            return true;
        }catch(Exception e){
            return false;
        }
    }

    private void getOutdoorAir(){
        try {
            String json=getJsonResult(makeOutdoorAirUrl());
            JsonParser jp = new JsonParser(json);
            this.air = jp.getOutdoor(this.stationName);
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

    public String getJsonResult(String url, String Key){
        try {
            URL endpoint = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) endpoint.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization","KakaoAK "+Key);
            if(connection.getResponseCode()!=200)
                return null;

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
