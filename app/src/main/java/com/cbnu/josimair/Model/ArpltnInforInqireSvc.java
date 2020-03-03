package com.cbnu.josimair.Model;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ArpltnInforInqireSvc {
    private static final String api = "openapi.airkorea.or.kr";
    private static final String svcKey = "Ykpt%2Ffoyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN%2F7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q%3D%3D";
    private static final String version="1.3";
    private String location;
    private Activity activity;
    private static boolean isNetworkConnected=false;

    public ArpltnInforInqireSvc(Activity activity){
        this.activity = activity;
        airInfo = "";

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            connectivityManager.registerNetworkCallback(builder.build(),new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(Network network) {
                            ArpltnInforInqireSvc.isNetworkConnected = true; // Global Static Variable
                        }
                        @Override
                        public void onLost(Network network) {
                            ArpltnInforInqireSvc.isNetworkConnected = false; // Global Static Variable
                        }
                    }
            );
            ArpltnInforInqireSvc.isNetworkConnected = false;
        }catch (Exception e){
            ArpltnInforInqireSvc.isNetworkConnected = false;
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


    public String getAirInfo(){ return this.airInfo; }

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
                InputStream responseBody = connection.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) response.append(inputLine);
                in.close();

                try {
                    List<Item> li = parse(new StringReader(response.toString()));
                    for(Item item : li){
                        if(item.stationName.equals("송파구"))
                            airInfo+=item.stationName + " : "+item.coValue+"\n";
                    }
                }catch(XmlPullParserException e){
                    e.printStackTrace();
                }catch(IOException ioe){
                    ioe.printStackTrace();
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

    public static class Item{
        public final String stationName;
        public final float coValue;

        public Item(String stationName, float coValue) {
            this.stationName = stationName;
            this.coValue = coValue;
        }
    }

    public List parse(StringReader in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in);
            parser.nextTag();
            return readBody(parser);
        } finally {
            in.close();
        }
    }

    private List<Item> readBody(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> items = new ArrayList();
        parser.require(XmlPullParser.START_TAG, null, "response");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            String name = parser.getName();

            if (name.equals("body")) {
                parser.require(XmlPullParser.START_TAG, null, "body");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                    name = parser.getName();
                    if (name.equals("items")) {
                        items = readItems(parser);
                        return items;
                    }
                    else{
                        skip(parser);
                    }
                }
            }
            else {
                skip(parser);
            }
        }
        return items;
    }

    private List<Item> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Item> li = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, null, "items");

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("item")) {
                Item item = readItem(parser);
                li.add(item);
            }
            else {
                skip(parser);
            }
        }
        return li;
    }

    private Item readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, null, "item");
        String statationName = null;
        String coValue = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("stationName")) {
                statationName = readUsingName(parser,"stationName");
            }
            else if(name.equals("coValue")){
                coValue = readUsingName(parser,"coValue");
            }else{
                skip(parser);
            }
        }
        try {
            return new Item(statationName, Float.valueOf(coValue));
        }catch(Exception e){
            return new Item(statationName, -1f);
        }
    }

    private String readUsingName(XmlPullParser parser, String name) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null,name);
        String stationName = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, name);
        return stationName;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
