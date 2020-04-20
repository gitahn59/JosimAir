package com.cbnu.josimair.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.ui.MainActivity;
import com.cbnu.josimair.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;

import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private AppDatabase db;
    private TextView airInfoTextView;
    private TextView airQualityTextView;
    private LineChart hourChart;

    private ImageView faceImageView;
    private TextView dustTextView;
    private TextView microDustTextView;
    private TextView no2TextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = MainActivity.db;

        faceImageView = (ImageView) root.findViewById(R.id.air_face);
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);
        hourChart = (LineChart)root.findViewById(R.id.hourChart);

        dustTextView = (TextView) root.findViewById(R.id.dustValueTextView);
        microDustTextView = (TextView) root.findViewById(R.id.microDustValueTextView);
        no2TextView = (TextView) root.findViewById(R.id.No2ValueTextView);

        setChartAttribute(hourChart);
        drawHourChart();
        updateIndoorAirInfo(MainActivity.indoorAir);
        updateOutdoorAirInfo();
        MainActivity.fragment = this;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void updateIndoorAirInfo(final IndoorAir indoorAir){
        homeViewModel.updateAirInfo(airInfoTextView,airQualityTextView, indoorAir, faceImageView);
    }

    public void updateOutdoorAirInfo(){
        homeViewModel.updateOutdoorAirInfo(dustTextView, microDustTextView, no2TextView, MainActivity.outdoorAir);
    }

    public void drawHourChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar to = Calendar.getInstance();
                final List<IndoorAirGroup> li = db.indoorAirDao().getGroupByHourBetweenDates(to.getTime());
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeViewModel.updateHourChart(hourChart,li);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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

        chart.setExtraLeftOffset(10);
        chart.setExtraRightOffset(10);
    }
}