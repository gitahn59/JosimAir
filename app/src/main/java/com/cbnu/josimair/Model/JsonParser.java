package com.cbnu.josimair.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    String json;

    public JsonParser(String json) {
        this.json = json;
    }

    public OutdoorAir getOutdoor(String city, String gu){
        float value = -1;
        int quality = -1;

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jarray = jsonObject.getJSONArray("list");
            for(int i=0; i < jarray.length(); i++){
                JSONObject jObject = jarray.getJSONObject(i);
                String name = jObject.getString("stationName");
                if(!name.equals(gu))
                    continue;
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
            if(value==0)
                return null;
            else
                return new OutdoorAir(city,gu, value, quality);
        }
    }
}
