package com.cbnu.josimair.Model;

import java.util.Date;

public class IndoorAirGroup {
    private String time;
    private float value;

    public IndoorAirGroup(String time, float value) {
        this.time = time;
        this.value = value;
    }

    public String getDay() {
        return time;
    }

    public float getValue() {
        return value;
    }
}
