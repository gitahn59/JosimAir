package com.cbnu.josimair.Model;

public class OutdoorAir {
    private float value;
    private int quality;

    public int getQuality() {
        return quality;
    }

    public float getValue() {
        return value;
    }

    public OutdoorAir(float value, int quality) {
        this.value = value;
        this.quality = quality;
    }
}
