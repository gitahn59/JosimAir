package com.cbnu.josimair.ui.home;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.entity.IndoorAir;
import com.cbnu.josimair.Model.entity.IndoorAirGroup;
import com.cbnu.josimair.Model.entity.OutdoorAir;
import com.cbnu.josimair.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;



import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    android.text.format.DateFormat df = new android.text.format.DateFormat();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, IndoorAir air, ImageView imageView){
        if(air==null) return;
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
    }

    public void updateOutdoorAirInfo(TextView dust, TextView microDust, TextView no2, TextView date, OutdoorAir air){
        if(air==null) {
            dust.setText("등급");
            microDust.setText("등급");
            no2.setText("등급");
            date.setText("측정시간");
        }
        else{
            switch(air.getPm25Grade()){
                case 1:
                    dust.setText(" 좋음 ");
                    break;
                case 2:
                    dust.setText(" 보통 ");
                    break;
                case 3:
                    dust.setText(" 나쁨 ");
                    break;
                case 4:
                    dust.setText(" 매우 나쁨 ");
                    break;
            }
            switch(air.getPm10Grade()){
                case 1:
                    microDust.setText(" 좋음 ");
                    break;
                case 2:
                    microDust.setText(" 보통 ");
                    break;
                case 3:
                    microDust.setText(" 나쁨 ");
                    break;
                case 4:
                    microDust.setText(" 매우 나쁨 ");
                    break;
            }
            switch(air.getNo2Grade()){
                case 1:
                    no2.setText(" 좋음 ");
                    break;
                case 2:
                    no2.setText(" 보통 ");
                    break;
                case 3:
                    no2.setText(" 나쁨 ");
                    break;
                case 4:
                    no2.setText(" 매우 나쁨 ");
                    break;
            }
            date.setText(df.format("MM/dd",air.getDataTime()).toString());
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