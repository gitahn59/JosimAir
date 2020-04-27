package com.cbnu.josimair.ui.airInfo;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.OutdoorAir;
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

    public void updateOutdoorAirInfo(TextView dust, TextView microDust, TextView no2, TextView mang, TextView date, OutdoorAir air){
        if(air==null) {
            dust.setText("㎍/㎥");
            microDust.setText("㎍/㎥");
            no2.setText("ppm");
            mang.setText("관측소 명");
            date.setText("측정시간");
        }
        else{
            dust.setText(air.getPm25Value()+" ㎍/㎥");
            microDust.setText(air.getPm10Value()+" ㎍/㎥");
            no2.setText(air.getNo2Value()+" ppm");
            mang.setText("측정소 : "+air.getMangName());
            date.setText(df.format("dd/MM",air.getDataTime()).toString());
        }
    }

}