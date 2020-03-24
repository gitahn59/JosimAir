package com.cbnu.josimair.ui.airInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.ui.MainActivity;
import com.cbnu.josimair.R;

public class AirInfomationFragment extends Fragment {

    private AirInformationViewModel airInformationViewModel;
    private Communication communication;
    private TextView airInfoTextView;
    private TextView airQualityTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        airInformationViewModel =
                ViewModelProviders.of(this).get(AirInformationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_airinformation, container, false);

        communication = MainActivity.communication;
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);

        setCallback();
        return root;
    }

    public void setCallback() {
        communication.setReceivedCallback(new Communication.ReceivedListener() {
            @Override
            public void onReceivedEvent() {
                Log.i("AirFragment","실내 공기정보 수신");
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            airInformationViewModel.updateAirInfo(airInfoTextView, airQualityTextView, communication.getReceivedAir());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}