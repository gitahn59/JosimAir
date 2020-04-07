package com.cbnu.josimair.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.ui.MainActivity;
import com.cbnu.josimair.R;
import com.cbnu.josimair.Model.AppDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private Communication communication;
    private AppDatabase db;
    private LineChart dayChart;
    private LineChart weekChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        db = MainActivity.db;
        communication = MainActivity.communication;
        communication.setReceivedCallback(new Communication.ReceivedListener() {
            @Override
            public void onReceivedEvent() {
                Log.i("StatisticsFragment","실내 공기정보 수신");
            }
        });

        dayChart = (LineChart)root.findViewById(R.id.dayChart);
        setChartAttribute(dayChart);
        weekChart = (LineChart)root.findViewById(R.id.weekChart);
        setChartAttribute(weekChart);

        drawDayChart();
        drawWeekChart();
        return root;
    }

    public void setChartAttribute(LineChart chart){
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = chart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = chart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDescription(null);
    }

    public void drawDayChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar to = Calendar.getInstance();
                to.add(Calendar.DATE,-1);
                to.set(Calendar.HOUR_OF_DAY,23);
                to.set(Calendar.MINUTE,59);
                to.set(Calendar.SECOND,59);
                final List<IndoorAirGroup> li = db.indoorAirDao().getGroupByDayBetweenDates(to.getTime());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsViewModel.updateDayChart(dayChart,li);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void drawWeekChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar to = Calendar.getInstance();
                final List<IndoorAirGroup> li = db.indoorAirDao().getGroupByWeekBetweenDates(to.getTime());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsViewModel.updateWeekChart(weekChart,li);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

}