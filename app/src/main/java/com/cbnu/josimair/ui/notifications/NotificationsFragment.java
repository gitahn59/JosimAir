package com.cbnu.josimair.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Communication;
import com.cbnu.josimair.MainBtmActivity;
import com.cbnu.josimair.R;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private Communication communication;
    private TextView airInfoTextView;
    private TextView airQualityTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);

        communication = MainBtmActivity.communication;
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);

        setCallback();
        return root;
    }

    public void setCallback() {
        communication.setReceivedCallback(new Communication.ReceivedListener() {
            @Override
            public void onReceivedEvent() {
                Log.i("AirFragment","공기정보 수신");
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            notificationsViewModel.updateAirInfo(airInfoTextView, airQualityTextView, communication.getReceivedAir());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}