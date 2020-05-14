package com.cbnu.josimair.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity
public class IndoorAir {
    @PrimaryKey
    @TypeConverters({Converters.class})
    @ColumnInfo(name = "time")
    @NonNull
    public Date time;
    public Date getTime() {
        return time;
    }
    public void setTime(Date time) {
        this.time = time;
    }

    @ColumnInfo(name = "value")
    private float value;
    public float getValue() {
        return this.value;
    }
    public void setValue(float value) {
        this.value = value;
    }

    @ColumnInfo(name = "quality")
    private int quality;
    public int getQuality() {
        return quality;
    }
    public void setQuality(int quality) {
        this.quality = quality;
    }

    private static IndoorAir lastKnownIndoorAir = null;

    public IndoorAir(float value){
        this.time = new Date(System.currentTimeMillis());
        this.value = value;
        if(0<=value && value<=9){
            quality = 1;
        }else if(9<value && value <=25){
            quality = 2;
        }else{
            quality = 3;
        }
    }

    public static synchronized IndoorAir getLastKnownIndoorAir(){
        return lastKnownIndoorAir;
    }

    public static synchronized void setLastKnownIndoorAir(IndoorAir last) {
        lastKnownIndoorAir = last;
    }
}
