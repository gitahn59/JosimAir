package com.cbnu.josimair.ui.statistics;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.ui.MainActivity;
import com.cbnu.josimair.R;
import com.cbnu.josimair.Model.AppDatabase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.Calendar;
import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private LineChart dayChart;
    private LineChart weekChart;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        dayChart = (LineChart)root.findViewById(R.id.dayChart);
        setChartAttribute(dayChart);
        weekChart = (LineChart)root.findViewById(R.id.weekChart);
        setChartAttribute(weekChart);

        drawDayChart();

        MainActivity.fragment = this;

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
                Calendar from = Calendar.getInstance();
                from.add(Calendar.DATE,-7);
                from.set(Calendar.HOUR_OF_DAY,0);
                from.set(Calendar.MINUTE,0);
                from.set(Calendar.SECOND,0);

                Calendar to = Calendar.getInstance();
                to.add(Calendar.DATE,-1);
                to.set(Calendar.HOUR_OF_DAY,23);
                to.set(Calendar.MINUTE,59);
                to.set(Calendar.SECOND,59);
                final List<IndoorAirGroup> li = AppDatabase.getInstance(getContext()).indoorAirDao().getGroupByDayBetweenDatesWithTimeTable(from.getTime(), to.getTime());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsViewModel.updateDayChart(dayChart, li);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                drawWeekChart();
            }
        }).start();
    }

    public void drawWeekChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar from = Calendar.getInstance();
                from.add(Calendar.WEEK_OF_YEAR,-7);
                from.set(Calendar.HOUR_OF_DAY,0);
                from.set(Calendar.MINUTE,0);
                from.set(Calendar.SECOND,0);

                Calendar to = Calendar.getInstance();

                final List<IndoorAirGroup> li = AppDatabase.getInstance(getContext()).indoorAirDao().getGroupByDayBetweenWeeksWithTimeTable(from.getTime(), to.getTime());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsViewModel.updateWeekChart(weekChart, li);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}