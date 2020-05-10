package com.cbnu.josimair.Model;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Pair;

import com.cbnu.josimair.R;
import com.cbnu.josimair.ui.MainActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class RestAPIService {
    private static final String airkorea_key = "your_key";
    private static final String kakao_key = "your_key";

    private Context mContext;
    private Location location;
    private Handler mHandler;

    public void setLocation(Location location) {
        this.location = location;
    }

    public RestAPIService(Context context, Handler handler){
        this.mContext = context;
        mHandler = handler;
    }

    public void start(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Pair<Double,Double> tmLocation = getTMLocation();
                if(tmLocation!=null) {
                    String stationName = getNearByStationInfo(tmLocation);
                    if (stationName!=null){
                        OutdoorAir outdoorAir = getOutdoorAir(stationName);
                        if(outdoorAir != null){
                            // 외부 대기정보 수신 성공
                            synchronized (this){
                                MainActivity.outdoorAir = outdoorAir;
                            }
                            mHandler.obtainMessage(Constants.MESSAGE_READ).sendToTarget();
                            return;
                        }
                    }
                }
                // 외부 대기정보 수신 실패
                mHandler.obtainMessage(Constants.MESSAGE_FAILED).sendToTarget();
            }
        });
    }

    public String makeTMLocationUrl(Double log, Double lat){
        String url = (String)mContext.getResources().getText(R.string.tmLocationAPI);
        url = String.format(url,log.toString(),lat.toString());
        return url;
    }

    private String makeNearbyStationUrl(Pair<Double,Double> tmLocation){
        String url = (String)mContext.getResources().getText(R.string.nearbyStationAPI);
        url = String.format(url, airkorea_key, tmLocation.first.toString(),tmLocation.second.toString());
        return url;
    }

    private String makeOutdoorAirUrl(String stationName) {
        String url = (String)mContext.getResources().getText(R.string.airInfoAPI);
        url = String.format(url, airkorea_key, stationName);
        return url;
    }

    private Pair<Double,Double> getTMLocation(){
        try {
            String json=getJsonResult(makeTMLocationUrl(location.getLongitude(),location.getLatitude()),"KakaoAK "+ kakao_key);
            JsonParser jp = new JsonParser(json);
            return jp.getTMLocation();
        }catch(Exception e){
            return null;
        }
    }

    private String getNearByStationInfo(Pair<Double,Double> tmLocation){
        try {
            String json=getJsonResult(makeNearbyStationUrl(tmLocation),null);
            JsonParser jp = new JsonParser(json);
            return jp.getNearByStation();
        }catch(Exception e){
            return null;
        }
    }

    private OutdoorAir getOutdoorAir(String stationName){
        try {
            String json=getJsonResult(makeOutdoorAirUrl(stationName), null);
            JsonParser jp = new JsonParser(json);
            return jp.getOutdoorAir(stationName);
        }
        catch(Exception e){
            return null;
        }
    }

    public String getJsonResult(String url, String Key){
        try {
            URL endpoint = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestProperty("Accept", "application/json");

            // 별도의 key가 있으면 key 등록
            if(Key!=null) connection.setRequestProperty("Authorization",Key);

            // request가 실패하면 null 리턴
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
