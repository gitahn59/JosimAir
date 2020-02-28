package com.cbnu.josimair.Model;

public class IndoorAir {
    private float value;
    private int quality;

    public IndoorAir(float value){
        this.value = value;
        if(0<=value && value<=7){
            quality = 1;
        }else if(7<value && value <12){
            quality = 2;
        }else{
            quality = 3;
        }
    }

    public int getQuality(){
        return this.quality;
    }

    public float getValue(){
        return this.value;
    }
}
