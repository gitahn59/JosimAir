package com.cbnu.josimair.ui.statistics;

import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.entity.IndoorAirGroup;
import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
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

    public void updateDayChart(LineChart lineChart, List<IndoorAirGroup> airs){
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0;i<airs.size();i++){
            entries.add(new Entry(airs.get(i).getValue(),i));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "PPM");
        setLineDataSetAttribute(lineDataSet);

        ArrayList<String> labels = new ArrayList<String>();
        for(int i=0;i<airs.size();i++){
            labels.add(airs.get(i).getDay());
        }

        LineData lineData = new LineData(labels, lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public void updateWeekChart(LineChart lineChart, List<IndoorAirGroup> airs){
        ArrayList<Entry> entries = new ArrayList<>();
        for(int i=0;i<airs.size();i++){
            entries.add(new Entry(airs.get(i).getValue(),i));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "PPM");
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