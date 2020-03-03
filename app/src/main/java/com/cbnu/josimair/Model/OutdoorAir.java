package com.cbnu.josimair.Model;

public class OutdoorAir {
    private float value;
    private int quality;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGu() {
        return gu;
    }

    public void setGu(String gu) {
        this.gu = gu;
    }

    private String city;
    private String gu;

    public int getQuality() {
        return quality;
    }

    public float getValue() {
        return value;
    }

    public OutdoorAir(String city, String gu, float value, int quality) {
        this.value = value;
        this.quality = quality;
        this.city = city;
        this.gu = gu;
    }
}
