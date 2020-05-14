package com.cbnu.josimair.Model;

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

public class RestAPIService {
    private static final String airkorea_key = "Ykpt/foyi49PDgJhtLVWnE1QB8R1t08idlq1Yieti3brksGN/7qszre1MeWYvX3uNXGx4V8PkUSzkeVU0g837Q==";
    private static final String kakao_key = "8d5d55fd6dd1923ff7dafb8f2ffc2bd2";

    private Context mContext;
    private Handler mHandler;

    /**
     * 생성자
     *
     * @param context Application context
     * @param handler message를 처리할 handler
     */
    public RestAPIService(Context context, Handler handler){
        this.mContext = context;
        mHandler = handler;
    }

    /**
     * last로 부터 최근접 관측소에서 측정된 외부 대기정보를 가져온다
     *
     * @param last GPS Location
     */
    public void start(final Location last){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TMLocation tmLocation = getTMLocation(last);
                if(tmLocation!=null) {
                    String stationName = getNearByStationInfo(tmLocation);
                    if (stationName!=null){
                        OutdoorAir outdoorAir = getOutdoorAir(stationName);
                        if(outdoorAir != null){
                            mHandler.obtainMessage(Constants.MESSAGE_READ, outdoorAir).sendToTarget();
                            return;
                        }
                    }
                }
                // 외부 대기정보 수신 실패
                mHandler.obtainMessage(Constants.MESSAGE_FAILED).sendToTarget();
            }
        });
    }

    /**
     * TM Location 을 얻기 위한 Rest API URL을 생성한다
     *
     * @param log GPS log
     * @param lat GPS lat
     */
    public String makeTMLocationUrl(Double log, Double lat){
        String url = (String)mContext.getResources().getText(R.string.tmLocationAPI);
        url = String.format(url,log.toString(),lat.toString());
        return url;
    }

    /**
     * tmLocation으로부터 가장 가까운 관측소의 이름을 얻기 위한 Rest API URL을 생성한다
     *
     * @param tmLocation GPS로 부터 변환 된 Location
     */
    private String makeNearbyStationUrl(TMLocation tmLocation){
        String url = (String)mContext.getResources().getText(R.string.nearbyStationAPI);
        url = String.format(url, airkorea_key, tmLocation.getX().toString(),tmLocation.getY().toString());
        return url;
    }


    /**
     * 관측소에서 측정된 실외 대기정보를 가져오기 위한 Rest API URL을 생성한다
     *
     * @param stationName 최근접 관측소의 이름
     */
    private String makeOutdoorAirUrl(String stationName) {
        String url = (String)mContext.getResources().getText(R.string.airInfoAPI);
        url = String.format(url, airkorea_key, stationName);
        return url;
    }

    /**
     * Rest API를 요청하여 GPS 좌표를 TM 좌표로 변환한다
     *
     * @param last GPS Location
     */
    private TMLocation getTMLocation(final Location last){
        try {
            String json=getJsonResult(makeTMLocationUrl(last.getLongitude(),last.getLatitude()),"KakaoAK "+ kakao_key);
            JsonParser jp = new JsonParser(json);
            return jp.getTMLocation();
        }catch(Exception e){
            return null;
        }
    }

    /**
     * Rest API를 요청하여 TM 좌표로 부터 최근접 관측소의 이름을 가져온다
     *
     * @param tmLocation TM Location
     */
    private String getNearByStationInfo(TMLocation tmLocation){
        try {
            String json=getJsonResult(makeNearbyStationUrl(tmLocation),null);
            JsonParser jp = new JsonParser(json);
            return jp.getNearByStation();
        }catch(Exception e){
            return null;
        }
    }

    /**
     * Rest API를 stationName 관측소에서 측정된 외부 대기정보를 가져온다
     *
     * @param stationName 관측소 이름
     */
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

    /**
     * Rest API url 과 key를 이용하여 http 로 Json Result를 요청한다
     *
     * @param url Rest API url
     * @param key authorization key
     */
    public String getJsonResult(String url, String key){
        try {
            URL endpoint = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestProperty("Accept", "application/json");

            // 별도의 key가 있으면 key 등록
            if(key!=null) connection.setRequestProperty("Authorization",key);

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

    /**
     * TMLocation 의 좌표를 기록하는 클래스
     */
    public static class TMLocation{
        Double x;
        Double y;

        public TMLocation(Double x, Double y){
            this.x = x;
            this.y = y;
        }

        public Double getX() {
            return x;
        }

        public Double getY() {
            return y;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void setY(double y) {
            this.y = y;
        }
    }
}
