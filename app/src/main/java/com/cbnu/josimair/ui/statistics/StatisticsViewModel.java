package com.cbnu.josimair.ui.statistics;

import android.graphics.Color;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.Converters;
import com.cbnu.josimair.Model.IndoorAir;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
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

    void setLineChartAttribute(LineChart lineChart){

    }



    public void updateStatistics(LineChart lineChart, List<IndoorAir> airs){
        ArrayList<Entry> entries = new ArrayList<>();
        int cnt=0;
        for(int i=0;i<7;i++){
            if(airs.size()==i) break;
            entries.add(new Entry(airs.get(i).getQuality(),cnt++));
        }

        LineDataSet lineDataSet = new LineDataSet(entries, "속성명1");
        lineDataSet.setLineWidth(2);
        //lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.parseColor("#FFA1B4DC"));
        //lineDataSet.setCircleColorHole(Color.BLUE);
        lineDataSet.setColor(Color.parseColor("#FFA1B4DC"));
        lineDataSet.setDrawCircleHole(true);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);

        ArrayList<String> labels = new ArrayList<String>();
        Calendar calendar = Calendar.getInstance();
        String [] days = {" ","일","월","화","수","목","금","토"};
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        for(int i = day; i<=7;i++){
            labels.add(days[i]);
        }
        for(int i= 1; i<day;i++){
            labels.add(days[i]);
        }

        LineData lineData = new LineData(labels, lineDataSet);
        lineChart.setData(lineData);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = lineChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = lineChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        //Description description = new Description();
        //description.setText("");

        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.setDescription(null);
        //lineChart.animateY(2000, Easing.EasingOption.EaseInCubic);
        lineChart.invalidate();
    }
}