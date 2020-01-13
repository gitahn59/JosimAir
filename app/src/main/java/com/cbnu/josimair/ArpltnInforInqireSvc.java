package com.cbnu.josimair;

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
    private static final String svcKey = "lV%2Ba8fVujJYHQ93ii9m4KypofVlGytmpQw5Ufvjaow8NJfJWAI2AXjH71whoFH3y79ZIjdyFvnU0sELB4YiXqQ%3D%3D";
    private static final String version="1.3";
    private String mlocation;

    public interface ReceivedListener{
        void onReceivedEvent(String xml);
    }

    private ReceivedListener mReceivedListener;

    public void setOnReceivedEvent(ReceivedListener listener){
        mReceivedListener = listener;
    }

    public interface ErrorOccurredListener{
        void onReceivedEvent();
    }

    private ErrorOccurredListener mErrorOccurredListener;

    public void setOnErrorOccurredEvent(ErrorOccurredListener listener){
        mErrorOccurredListener = listener;
    }

    ArpltnInforInqireSvc(String city){
        mlocation=city;
    }

    private void rest(){
        try {
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

            URL endpoint = new URL(url);

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
                mReceivedListener.onReceivedEvent(response.toString());
            }else{
                //error
            }
        }catch(MalformedURLException murle){

        }catch(IOException ioe){

        }
    }

    void getAirInfo(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                rest();
            }
        });
    }
}
