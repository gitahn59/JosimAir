package com.cbnu.josimair.Model;

public class IndoorAir {
    private float value;
    private int quality;

    public IndoorAir(float value){
        this.value = value;
        if(0<=value && value<=9){
            quality = 1;
        }else if(9<value && value <=25){
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
