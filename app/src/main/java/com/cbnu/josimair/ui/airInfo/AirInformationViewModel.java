package com.cbnu.josimair.ui.airInfo;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.R;

public class AirInformationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AirInformationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, TextView quality, IndoorAir air, ImageView imageView, ImageView green, ImageView red, ImageView yellow){
        switch (air.getQuality()) {
            case 1:
                info.setText(R.string.air_quality_good);
                imageView.setImageResource(R.drawable.smile);
                green.setImageResource(R.drawable.green);
                yellow.setImageResource(0);
                red.setImageResource(0);
                break;
            case 2:
                info.setText(R.string.air_quality_normal);
                imageView.setImageResource(R.drawable.sceptic);
                green.setImageResource(0);
                yellow.setImageResource(R.drawable.yellow);
                red.setImageResource(0);
                break;
            case 3:
                info.setText(R.string.air_quality_bad);
                imageView.setImageResource(R.drawable.sad);
                green.setImageResource(0);
                yellow.setImageResource(0);
                red.setImageResource(R.drawable.red);
                break;
        }
        quality.setText("" + air.getValue());
    }
}