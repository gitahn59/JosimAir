package com.cbnu.josimair.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.Model.StatisticsService;
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
    private TextView airInfoTextView;
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
        updateIndoorAirInfo(IndoorAir.getLastKnownIndoorAir());
        updateOutdoorAirInfo(OutdoorAir.getLastKnownOutdoorAir());
        MainActivity.fragment = this;
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * view 에 실내 대기정보를 전달하여 화면을 업데이트 한다
     *
     * @param indoorAir 실내 대기정보
     */
    public void updateIndoorAirInfo(final IndoorAir indoorAir){
        homeViewModel.updateAirInfo(airInfoTextView, indoorAir, faceImageView);
    }

    /**
     * view 에 실외 대기정보를 전달하여 화면을 업데이트 한다
     *
     * @param outdoorAir 실외 대기정보
     */
    public void updateOutdoorAirInfo(OutdoorAir outdoorAir){
        homeViewModel.updateOutdoorAirInfo(dustTextView, microDustTextView, no2TextView, dateTextView, outdoorAir);
    }

    /**
     * 시간 통계 그래프를 그린다
     */
    public void drawHourChart(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                StatisticsService ss = new StatisticsService(getContext());
                final List<IndoorAirGroup> fli =  ss.getHourStatisticsData();

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

    /**
     * 통계 그래프의 속성을 설정한다
     */
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