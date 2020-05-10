package com.cbnu.josimair.ui.home;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private AppDatabase db;
    private TextView airInfoTextView;
    private TextView airQualityTextView;
    private LineChart hourChart;

    private ImageView faceImageView;
    private ImageView dustImageView;
    private ImageView microdustImageView;
    private ImageView NO2ImageView;
    private TextView dustTextView;
    private TextView microDustTextView;
    private TextView no2TextView;
    private TextView dateTextView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        db = MainActivity.db;

        faceImageView = (ImageView) root.findViewById(R.id.air_face);
        dustImageView = (ImageView) root.findViewById(R.id.dust);
        microdustImageView = (ImageView) root.findViewById(R.id.micro_dust);
        NO2ImageView = (ImageView) root.findViewById(R.id.NO2);
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        hourChart = (LineChart)root.findViewById(R.id.hourChart);

        dustTextView = (TextView) root.findViewById(R.id.dustValueTextView);
        microDustTextView = (TextView) root.findViewById(R.id.microDustValueTextView);
        no2TextView = (TextView) root.findViewById(R.id.No2ValueTextView);
        dateTextView = (TextView) root.findViewById(R.id.date);

        // 디스플레이 사이즈 받기
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ViewGroup.LayoutParams face_params = (ViewGroup.LayoutParams)  faceImageView.getLayoutParams();
        ViewGroup.LayoutParams dust_params = (ViewGroup.LayoutParams)  dustImageView.getLayoutParams();
        ViewGroup.LayoutParams microdust_params = (ViewGroup.LayoutParams)  microdustImageView.getLayoutParams();
        ViewGroup.LayoutParams NO2_params = (ViewGroup.LayoutParams)  NO2ImageView.getLayoutParams();

        face_params.height = metrics.heightPixels/5;
        face_params.width = face_params.height;

        dust_params.height = metrics.heightPixels/20;
        dust_params.width = dust_params.height;

        microdust_params.height = metrics.heightPixels/20;
        microdust_params.width = dust_params.height;

        NO2_params.height = metrics.heightPixels/20;
        NO2_params.width = dust_params.height;

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
        homeViewModel.updateAirInfo(airInfoTextView, indoorAir, faceImageView);
    }

    public void updateOutdoorAirInfo(){
        homeViewModel.updateOutdoorAirInfo(dustTextView, microDustTextView, no2TextView, dateTextView, MainActivity.outdoorAir);
    }

    private ArrayList<IndoorAirGroup> sortOrderbyTime(List<IndoorAirGroup> src, int now){
        ArrayList<IndoorAirGroup> li = new ArrayList<IndoorAirGroup>();
        ArrayList<IndoorAirGroup> temp = new ArrayList<IndoorAirGroup>();
        for(IndoorAirGroup i : src){
            int h = Integer.parseInt(i.getDay());
            if(h<=now) temp.add(i);
            else li.add(i);
        }

        for(IndoorAirGroup i : temp) {
            li.add(i);
        }
        return li;
    }

    public void drawHourChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Calendar to = Calendar.getInstance();
                Calendar from = Calendar.getInstance();
                from.add(Calendar.HOUR, -24);
                List<IndoorAirGroup> li = db.indoorAirDao().getGroupByHourBetweenDatesWithTimeTable(from.getTime(),to.getTime());
                final List<IndoorAirGroup> fli = sortOrderbyTime(li,to.get(Calendar.HOUR_OF_DAY));
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeViewModel.updateHourChart(hourChart, fli);
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