package com.cbnu.josimair.ui.home;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.R;
import com.cbnu.josimair.ui.MainActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, TextView quality, IndoorAir air, ImageView imageView){
        switch (air.getQuality()) {
            case 1:
                info.setText(R.string.air_quality_good);
                imageView.setImageResource(R.drawable.smile);
                break;
            case 2:
                info.setText(R.string.air_quality_normal);
                imageView.setImageResource(R.drawable.sceptic);
                break;
            case 3:
                info.setText(R.string.air_quality_bad);
                imageView.setImageResource(R.drawable.sad);
                break;
        }
        quality.setText("" + air.getValue());
    }

    public void updateOutdoorAirInfo(TextView quality, OutdoorAir air){
        if(air.getStation()==null) {
            quality.setText(R.string.network_receiving);
            if(!MainActivity.resourceChecker.isNetworkEnable()){
                quality.setText(R.string.network_error);
            }else if(!MainActivity.locationFinder.isEnabled()){
                quality.setText(R.string.gps_error);
            }
        }
        else{
            quality.setText(air.getStation() + " : " + air.getValue());

        }
    }

    public void setLineDataSetAttribute(LineDataSet lineDataSet){
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);
    }

    public void updateHourChart(LineChart lineChart, List<IndoorAirGroup> airs){
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0;i<airs.size();i++){
            entries.add(new Entry(airs.get(i).getValue(),i));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "평균값");
        setLineDataSetAttribute(lineDataSet);

        ArrayList<String> labels = new ArrayList<String>();
        for(int i=0;i<airs.size();i++){
            labels.add(airs.get(i).getDay());
        }

        LineData lineData = new LineData(labels, lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}