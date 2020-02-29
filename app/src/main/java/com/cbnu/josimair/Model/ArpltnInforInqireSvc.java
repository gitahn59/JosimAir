package com.cbnu.josimair.Model;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class ArpltnInforInqireSvc {
    private static final String api = "openapi.airkorea.or.kr";
    private static final String svcKey = "Ykpt%2Ffoyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN%2F7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q%3D%3D";
    private static final String version="1.3";
    private String mlocation;
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


    public ArpltnInforInqireSvc(String city){
        mlocation=city;
    }
    public String getAirInfo(){ return this.airInfo; }

    public void start(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
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
                .appendQueryParameter("numOfRows","1")
                .appendQueryParameter("ver",version);
        String url = builder.build().toString();
        url += ("&ServiceKey=" + svcKey);
        url += ("&sidoName=" + mlocation);

        return url;
    }

    private void rest(){
        try {
            URL endpoint = new URL(makeUrl());
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestProperty("Accept", "application/xml");
            if(connection.getResponseCode()==200){
                //Sucess
                InputStream responseBody = connection.getInputStream();
                InputStreamReader responseBodyReader = new InputStreamReader(responseBody, "UTF-8");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                airInfo = response.toString();
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
