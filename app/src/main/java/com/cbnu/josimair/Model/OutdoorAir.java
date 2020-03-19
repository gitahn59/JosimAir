package com.cbnu.josimair.Model;

import java.util.Date;

public class OutdoorAir {
    private float value;
    private int quality;
    private Date time;

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    private String station;

    public int getQuality() {
        return quality;
    }

    public float getValue() {
        return value;
    }

    private OutdoorAir(){

    }

    public void setValues(String station, float value, int quality) {
        this.value = value;
        this.quality = quality;
        this.station = station;
        this.time = new Date(System.currentTimeMillis());
    }

    private static class Constructor{
        public static final OutdoorAir INSTANCE = new OutdoorAir();
    }

    public static OutdoorAir getInstance(){
        return Constructor.INSTANCE;
    }
}
