package com.cbnu.josimair.ui.airInfo;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.entity.IndoorAir;
import com.cbnu.josimair.Model.entity.OutdoorAir;
import com.cbnu.josimair.R;

public class AirInformationViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    android.text.format.DateFormat df = new android.text.format.DateFormat();

    public AirInformationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public void updateAirInfo(TextView info, TextView quality, IndoorAir air, ImageView imageView){
        if(air==null) return;
        switch (air.getQuality()) {
            case 1:
                info.setText(R.string.air_quality_good);
                imageView.setImageResource(R.drawable.smile);
                break;
            case 2:
                info.setText(R.string.air_quality_normal);
                imageView.setImageResource(R.drawable.sceptic);
                break;
            case 3:
                info.setText(R.string.air_quality_bad);
                imageView.setImageResource(R.drawable.sad);
                break;
        }
        quality.setText("" + air.getValue());
    }

    public void updateOutdoorAirInfo(TextView dust, TextView microDust, TextView no2, TextView date, OutdoorAir air){

        if(air==null) {
            dust.setText("㎍/㎥");
            microDust.setText("㎍/㎥");
            no2.setText("ppm");
            date.setText("측정시간");
        }
        else{
            dust.setText(air.getPm25Value()+" ㎍/㎥");
            microDust.setText(air.getPm10Value()+" ㎍/㎥");
            no2.setText(air.getNo2Value()+" ppm");
            date.setText(df.format("MM/dd",air.getDataTime()).toString());
        }
    }

    public void updateVentilation(TextView ventilation_status, TextView ventilation_time, IndoorAir indoorAir, OutdoorAir outdoorAir){
        if(outdoorAir.getPm10Grade() == 3 || outdoorAir.getPm25Grade() == 3){
            switch (indoorAir.getQuality()){
                case 1:
                    ventilation_status.setText("환기가 필요없습니다");
                    ventilation_time.setText("0분");
                    break;
                case 2:
                    ventilation_status.setText("환기가 필요합니다");
                    ventilation_time.setText("5분");
                    break;
                case 3:
                    ventilation_status.setText("환기가 필요합니다");
                    ventilation_time.setText("10분");
                    break;
            }
        }
        else if(outdoorAir.getPm10Grade() == 4 || outdoorAir.getPm25Grade() == 4){
            switch (indoorAir.getQuality()){
                case 1:
                    ventilation_status.setText("환기가 필요없습니다");
                    ventilation_time.setText("0분");
                    break;
                case 2:
                    ventilation_status.setText("환기가 필요없습니다");
                    ventilation_time.setText("0분");
                    break;
                case 3:
                    ventilation_status.setText("환기가 필요합니다");
                    ventilation_time.setText("10분");
                    break;
            }
        }
        else{
            switch (indoorAir.getQuality()){
                case 1:
                    ventilation_status.setText("환기가 필요없습니다");
                    ventilation_time.setText("0분");
                    break;
                case 2:
                    ventilation_status.setText("환기가 필요합니다");
                    ventilation_time.setText("15분");
                    break;
                case 3:
                    ventilation_status.setText("환기가 필요합니다");
                    ventilation_time.setText("30분");
                    break;
            }
        }
    }


}