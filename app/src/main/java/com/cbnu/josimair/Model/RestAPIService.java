package com.cbnu.josimair.Model;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class RestAPIService {
    private static final String api = "openapi.airkorea.or.kr";
    private static final String svcKey = "Ykpt%2Ffoyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN%2F7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q%3D%3D";
    private static final String version="1.3";
    private String location;
    private Activity activity;
    private OutdoorAir air;

    public OutdoorAir getAir() {
        return air;
    }

    private static boolean isNetworkConnected=false;

    public RestAPIService(Activity activity){
        this.activity = activity;
        airInfo = "";

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(Network network) {
                        RestAPIService.isNetworkConnected = true; // Global Static Variable
                    }
                    @Override
                    public void onLost(Network network) {
                        RestAPIService.isNetworkConnected = false; // Global Static Variable
                    }
                }
            );
            RestAPIService.isNetworkConnected = false;
        }catch (Exception e){
            RestAPIService.isNetworkConnected = false;
        }
    }

    public boolean isArived() {
        return isArived;
    }

    public void setArived(boolean arived) {
        isArived = arived;
    }

    private boolean isArived;

    public String getlocation() {
        return location;
    }

    public void setlocation(String location) {
        this.location = location;
    }

    private String airInfo;

    // received event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface ReceivedListener{
        void onReceivedEvent(String xml);
    }
    private ReceivedListener mReceivedListener;
    public void setOnReceivedEvent(ReceivedListener listener){
        mReceivedListener = listener;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////

    // error occurred event
    ///////////////////////////////////////////////////////////////////////////////////////////
    public interface ErrorOccurredListener{
        void onReceivedEvent();
    }
    private ErrorOccurredListener mErrorOccurredListener;

    public void setOnErrorOccurredEvent(ErrorOccurredListener listener){ mErrorOccurredListener = listener; }
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void start(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                isArived = false;
                rest();
            }
        });
    }

    private String makeUrl(){
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
                .appendQueryParameter("ver",version);
        String url = builder.build().toString();
        url += ("&ServiceKey=" + svcKey);
        url += ("&sidoName=" + location);

        return url;
    }

    private void rest(){
        try {
            do{
                if(isNetworkConnected) break;
                try {
                    Thread.sleep(500);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }while(true);

            URL endpoint = new URL(makeUrl());
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestProperty("Accept", "application/xml");
            if(connection.getResponseCode()==200){
                //Sucess
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                try {
                    XmlParser parser = new XmlParser("서울","송파구",response.toString());
                    this.air = parser.parse();
                }catch(Exception e){
                    e.printStackTrace();
                }
                isArived = true;
                mReceivedListener.onReceivedEvent(response.toString());
            }
            else{
                //error
                mErrorOccurredListener.onReceivedEvent();
            }
        }
        catch(MalformedURLException murle){
            mErrorOccurredListener.onReceivedEvent();
        }
        catch(IOException ioe){
            mErrorOccurredListener.onReceivedEvent();
        }
    }

}
