package com.cbnu.josimair.ui.statistics;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.IndoorAir;

import java.util.List;

public class StatisticsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StatisticsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateStatistics(TextView statistics, List<IndoorAir> airs){
        String info="";
        for(IndoorAir air : airs){
            info +=(air.getTime() +" : " + air.getValue() + " : " + air.getQuality() +"\n");
        }
        statistics.setText(info);
    }
}