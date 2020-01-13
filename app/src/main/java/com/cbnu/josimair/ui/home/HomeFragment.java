package com.cbnu.josimair.ui.home;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.cbnu.josimair.DeviceListActivity;
import com.cbnu.josimair.MainBtmActivity;
import com.cbnu.josimair.R;

public class HomeFragment extends Fragment {
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Button btn = (Button) root.findViewById(R.id.button2);
        btn.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){
                    if(!MainBtmActivity.communication.enable()) {
                        MainBtmActivity.communication.showDialog();
                    }else{
                        startActivityForResult(new Intent(v.getContext(), DeviceListActivity.class),Communication.RESULT_CODE_BTLIST);
                    }

                /*
                ArpltnInforInqireSvc svc = new ArpltnInforInqireSvc("서울");
                svc.setOnReceivedEvent(new ArpltnInforInqireSvc.ReceivedListener() {
                    @Override
                    public void onReceivedEvent(String xml) {
                        Log.v("result",xml);
                    }
                });
                svc.getAirInfo();
                 */
                }
            }
        );




        return root;
    }


}