package com.cbnu.josimair.ui.home;

import android.graphics.Color;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.IndoorAirGroup;
import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.Model.RestAPIService;
import com.cbnu.josimair.Model.Communication;
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
    private RestAPIService svc;
    private TextView airInfoTextView;
    private TextView airQualityTextView;
    private TextView outdoorAirQualityTextView;
    private LineChart hourChart;

    private ImageView imageView_01;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        svc = MainActivity.svc;
        db = MainActivity.db;

        imageView_01 = (ImageView) root.findViewById(R.id.air_face);
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);
        outdoorAirQualityTextView = (TextView)root.findViewById(R.id.outdoorAirQualityTextView);
        hourChart = (LineChart)root.findViewById(R.id.hourChart);
        setHourChartAttribute();

        setCallback();
        drawHourChart();
        updateOutdoorAirInfo();
        MainActivity.fragment = this;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void setCallback(){
        svc.setOnReceivedEvent(new RestAPIService.ReceivedListener() {
            @Override
            public void onReceivedEvent(final OutdoorAir air) {
               updateOutdoorAirInfo();
            }
        });
        svc.setOnErrorOccurredEvent(new RestAPIService.ErrorOccurredListener() {
            @Override
            public void onErrorOccurredEvent() {
                updateOutdoorAirInfo();
            }
        });
    }

    public void updateOutdoorAirInfo(){
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeViewModel.updateOutdoorAirInfo(outdoorAirQualityTextView, MainActivity.outdoorAir);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateIndoorAirInfo(final IndoorAir indoorAir){
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    homeViewModel.updateAirInfo(airInfoTextView,airQualityTextView, indoorAir,imageView_01);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public void setHourChartAttribute(){
        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.enableGridDashedLine(8, 24, 0);

        YAxis yLAxis = hourChart.getAxisLeft();
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = hourChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        hourChart.setDoubleTapToZoomEnabled(false);
        hourChart.setDrawGridBackground(false);
        hourChart.setDescription(null);
    }

}