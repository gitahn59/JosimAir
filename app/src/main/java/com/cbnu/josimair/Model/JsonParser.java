package com.cbnu.josimair.Model;

import android.location.Geocoder;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class JsonParser {
    String json;

    public JsonParser(String json) {
        this.json = json;
    }

    public OutdoorAir getOutdoor(String stationName){
        float value = -1;
        int quality = -1;

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jarray = jsonObject.getJSONArray("list");
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);
                String name = jObject.getString("stationName");
                try {
                    value = Float.parseFloat(jObject.getString("coValue"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    quality = Integer.parseInt(jObject.getString("coGrade"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            if(value==-1)
                return null;
            else
                return new OutdoorAir(stationName, value, quality);
        }
    }


    public Pair<Double, Double> getTMLocation(){
        Double first=0.0;
        Double second=0.0;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jarray = jsonObject.getJSONArray("documents");
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);
                first= Double.parseDouble(jObject.getString("x"));
                second= Double.parseDouble(jObject.getString("y"));
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            if(first==0)
                return null;
            else
                return new Pair<Double, Double>(first,second);
        }
    }

    public String getNearByStation(){
        String stationName=null;
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jarray = jsonObject.getJSONArray("list");
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);
                stationName = jObject.getString("stationName");
                break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally{
            return stationName;
        }
    }
}
