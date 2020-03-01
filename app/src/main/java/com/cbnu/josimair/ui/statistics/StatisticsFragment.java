package com.cbnu.josimair.ui.statistics;

import android.os.AsyncTask;
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
import com.cbnu.josimair.ui.MainBtmActivity;
import com.cbnu.josimair.R;
import com.cbnu.josimair.Model.AppDatabase;

import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    private Communication communication;
    AppDatabase db;

    private TextView statisticsTextView;
    private Button updateBtn;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        communication = MainBtmActivity.communication;
        communication.setReceivedCallback(new Communication.ReceivedListener() {
            @Override
            public void onReceivedEvent() {
                Log.i("StatisticsFragment","실내 공기정보 수신");
            }
        });

        statisticsTextView = (TextView)root.findViewById(R.id.statisticsTextView);
        updateBtn = (Button) root.findViewById(R.id.updateBtn);

        db = MainBtmActivity.db;

        setCallback();
        setStatisticsViews();

        return root;
    }

    public void setCallback(){
        updateBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IndoorAir ia = new IndoorAir((float) Math.random() * 15);
                        db.indoorAirDao().insertAll(ia);
                    }
                }).start();
            }
        });
    }

    public void setStatisticsViews(){
         new Thread(new Runnable() {
            @Override
            public void run() {
                final List<IndoorAir> li = db.indoorAirDao().getAll();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statisticsViewModel.updateStatistics(statisticsTextView,li);
                        }
                    });
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}