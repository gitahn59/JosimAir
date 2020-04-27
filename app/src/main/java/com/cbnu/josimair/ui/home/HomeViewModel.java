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

    android.text.format.DateFormat df = new android.text.format.DateFormat();

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, TextView quality, IndoorAir air, ImageView imageView){
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
        quality.setText("" + air.getValue());
    }

    public void updateOutdoorAirInfo(TextView dust, TextView microDust, TextView no2, TextView mang, TextView date, OutdoorAir air){
        if(air==null) {
            dust.setText("㎍/㎥");
            microDust.setText("㎍/㎥");
            no2.setText("ppm");
            mang.setText("관측소 명");
            date.setText("측정시간");
        }
        else{
            dust.setText(air.getPm25Value()+" ㎍/㎥");
            microDust.setText(air.getPm10Value()+" ㎍/㎥");
            no2.setText(air.getNo2Value()+" ppm");
            mang.setText("측정소 : "+air.getMangName());
            date.setText(df.format("dd/MM",air.getDataTime()).toString());
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