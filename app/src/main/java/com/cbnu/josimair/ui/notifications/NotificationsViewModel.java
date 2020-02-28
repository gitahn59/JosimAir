package com.cbnu.josimair.ui.notifications;

import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.R;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, TextView quality, IndoorAir air){
        switch (air.getQuality()) {
            case 1:
                info.setText(R.string.air_quality_good);
                break;
            case 2:
                info.setText(R.string.air_quality_normal);
                break;
            case 3:
                info.setText(R.string.air_quality_bad);
                break;
        }
        quality.setText("" + air.getValue());
    }
}