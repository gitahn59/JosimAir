package com.cbnu.josimair.ui.statistics;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.ui.MainBtmActivity;
import com.cbnu.josimair.R;
import com.cbnu.josimair.Model.AppDatabase;

import java.util.List;

public class StatisticsFragment extends Fragment {

    private StatisticsViewModel statisticsViewModel;
    AppDatabase db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                ViewModelProviders.of(this).get(StatisticsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_statistics, container, false);

        db = MainBtmActivity.db;
        Log.e("statistics","opend");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                IndoorAir indoorAir = new IndoorAir(3.5f);
                db.indoorAirDao().insertAll(indoorAir);
                List<IndoorAir> li = db.indoorAirDao().getAll();
            }
        });



        return root;
    }
}