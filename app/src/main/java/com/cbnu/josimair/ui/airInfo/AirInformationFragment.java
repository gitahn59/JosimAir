package com.cbnu.josimair.ui.airInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.ui.MainActivity;
import com.cbnu.josimair.R;

public class AirInformationFragment extends Fragment {

    private AirInformationViewModel airInformationViewModel;
    private TextView airInfoTextView;
    private TextView airQualityTextView;

    private ImageView imageView_01;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        airInformationViewModel =
                ViewModelProviders.of(this).get(AirInformationViewModel.class);
        View root = inflater.inflate(R.layout.fragment_airinformation, container, false);
        airInfoTextView = (TextView) root.findViewById(R.id.airInfoTextView);
        airQualityTextView = (TextView) root.findViewById(R.id.airQualityTextView);

        imageView_01 = (ImageView) root.findViewById(R.id.air_face);

        MainActivity.fragment = this;
        return root;
    }

    public void update(final IndoorAir indoorAir){
        airInformationViewModel.updateAirInfo(airInfoTextView, airQualityTextView, indoorAir, imageView_01);
    }
}