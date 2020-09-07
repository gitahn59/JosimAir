package com.cbnu.josimair.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OutdoorAir {
    //Outdoor Air Infomation factor - s
    private String stationName;

    private String mangName;
    private Date dataTime;

    private float so2Value;
    private float coValue;
    private float o3Value;
    private float no2Value;

    private int so2Grade;
    private int coGrade;
    private int o3Grade;
    private int no2Grade;

    private float pm10Value;
    private float pm10Value24;
    private float pm25Value;
    private float pm25Value24;

    private int pm10Grade;
    private int pm25Grade;
    private int pm10Grade1h;
    private int pm25Grade1h;

    private float khaiValue;
    private int khaiGrade;
    //Outdoor Air Infomation factor - e

    /**
     * 가장 최근에 요청한 외부대기 정보
     */
    private static OutdoorAir lastKnownOutdoorAir;
    public static synchronized OutdoorAir getLastKnownOutdoorAir(){
        return lastKnownOutdoorAir;
    }

    public static synchronized void setLastKnownOutdoorAir(OutdoorAir last){
        lastKnownOutdoorAir = last;
    }

    public OutdoorAir(String stationName){
        this.stationName = stationName;
    }
    public String getStationName() {
        return stationName;
    }

    public String getMangName() {
        return mangName;
    }

    public void setMangName(String mangName) {
        this.mangName = mangName;
    }

    public Date getDataTime() {
        return dataTime;
    }

    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    public float getSo2Value() {
        return so2Value;
    }

    public void setSo2Value(float so2Value) {
        this.so2Value = so2Value;
    }

    public float getCoValue() {
        return coValue;
    }

    public void setCoValue(float coValue) {
        this.coValue = coValue;
    }

    public float getO3Value() {
        return o3Value;
    }

    public void setO3Value(float o3Value) {
        this.o3Value = o3Value;
    }

    public float getNo2Value() {
        return no2Value;
    }

    public void setNo2Value(float no2Value) {
        this.no2Value = no2Value;
    }

    public int getSo2Grade() {
        return so2Grade;
    }

    public void setSo2Grade(int so2Grade) {
        this.so2Grade = so2Grade;
    }

    public int getCoGrade() {
        return coGrade;
    }

    public void setCoGrade(int coGrade) {
        this.coGrade = coGrade;
    }

    public int getO3Grade() {
        return o3Grade;
    }

    public void setO3Grade(int o3Grade) {
        this.o3Grade = o3Grade;
    }

    public int getNo2Grade() {
        return no2Grade;
    }

    public void setNo2Grade(int no2Grade) {
        this.no2Grade = no2Grade;
    }

    public float getPm10Value() {
        return pm10Value;
    }

    public void setPm10Value(float pm10Value) {
        this.pm10Value = pm10Value;
    }

    public float getPm10Value24() {
        return pm10Value24;
    }

    public void setPm10Value24(float pm10Value24) {
        this.pm10Value24 = pm10Value24;
    }

    public float getPm25Value() {
        return pm25Value;
    }

    public void setPm25Value(float pm25Value) {
        this.pm25Value = pm25Value;
    }

    public float getPm25Value24() {
        return pm25Value24;
    }

    public void setPm25Value24(float pm25Value24) {
        this.pm25Value24 = pm25Value24;
    }

    public int getPm10Grade() {
        return pm10Grade;
    }

    public void setPm10Grade(int pm10Grade) {
        this.pm10Grade = pm10Grade;
    }

    public int getPm25Grade() {
        return pm25Grade;
    }

    public void setPm25Grade(int pm25Grade) {
        this.pm25Grade = pm25Grade;
    }

    public int getPm10Grade1h() {
        return pm10Grade1h;
    }

    public void setPm10Grade1h(int pm10Grade1h) {
        this.pm10Grade1h = pm10Grade1h;
    }

    public int getPm25Grade1h() {
        return pm25Grade1h;
    }

    public void setPm25Grade1h(int pm25Grade1h) {
        this.pm25Grade1h = pm25Grade1h;
    }

    public float getKhaiValue() {
        return khaiValue;
    }

    public void setKhaiValue(float khaiValue) {
        this.khaiValue = khaiValue;
    }

    public int getKhaiGrade() {
        return khaiGrade;
    }

    public void setKhaiGrade(int khaiGrade) {
        this.khaiGrade = khaiGrade;
    }
}
