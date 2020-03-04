package com.cbnu.josimair.ui.home;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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

import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.Model.RestAPIService;
import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.ui.MainBtmActivity;
import com.cbnu.josimair.R;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;
    private Communication communication;
    private RestAPIService svc;
    private TextView airInfoTextView;
    private TextView airQualityTextView;
    private TextView outdoorAirQualityTextView;

    private Button btBtn;
    private Button locationBtn;


    Geocoder mGeoCoder;
    LocationManager locationManager;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        communication = MainBtmActivity.communication;
        svc = MainBtmActivity.svc;

        btBtn = (Button) root.findViewById(R.id.btBtn);
        locationBtn = (Button) root.findViewById(R.id.locationBtn);
        mGeoCoder = new Geocoder(root.getContext());
        locationManager = (LocationManager) root.getContext().getSystemService(Context.LOCATION_SERVICE);

        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);
        outdoorAirQualityTextView = (TextView)root.findViewById(R.id.outdoorAirQualityTextView);

        if(communication.enable()){
            btBtn.setText(R.string.bluetooth_enabled_btn);
        }else{
            btBtn.setText(R.string.bluetooth_connect_btn);
        }

        homeViewModel.updateOutdoorAirInfo(outdoorAirQualityTextView,svc.getAir());

        setCallback();
        return root;
    }

    public void setCallback(){
        btBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        if(!communication.enable()) {
                            communication.showDialog();
                        }else{ // 여기서 블루투스 리스트에서 디바이스를 선택하여 연결
                            //Communication thread 시작
                            communication.start_Test_Using_RandomData();
                            //startActivityForResult(new Intent(v.getContext(), DeviceListActivity.class),Communication.RESULT_CODE_BTLIST);
                        }
                    }
                }
        );

        locationBtn.setOnClickListener(
                new Button.OnClickListener(){
                    public void onClick(View v){
                        try {
                            List<Address> list = mGeoCoder.getFromLocation(37.5082, 127.1179, 1);
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
        );

        communication.setReceivedCallback(new Communication.ReceivedListener() {
            @Override
            public void onReceivedEvent() {
                Log.i("HomeFragment","실내 공기정보 수신");
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeViewModel.updateAirInfo(airInfoTextView,airQualityTextView,communication.getReceivedAir());
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        svc.setOnReceivedEvent(new RestAPIService.ReceivedListener() {
            @Override
            public void onReceivedEvent(final OutdoorAir air) {
                Log.i("HomeFragment", "실외 공기정보 수신");
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeViewModel.updateOutdoorAirInfo(outdoorAirQualityTextView, svc.getAir());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        svc.setOnErrorOccurredEvent(new RestAPIService.ErrorOccurredListener() {
            @Override
            public void onErrorOccurredEvent() {
                Log.i("HomeFragment","실외 공기정보 수신 실패");
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeViewModel.updateOutdoorAirInfo(outdoorAirQualityTextView,null);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}