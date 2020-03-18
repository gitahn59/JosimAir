package com.cbnu.josimair.Model;

public class OutdoorAir {
    private float value;
    private int quality;

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

    public OutdoorAir(String station, float value, int quality) {
        this.value = value;
        this.quality = quality;
        this.station = station;
    }
}
