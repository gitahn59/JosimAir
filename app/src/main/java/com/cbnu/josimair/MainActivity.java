package com.cbnu.josimair;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Communication btService = null;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button)findViewById(R.id.button);
        btn.setOnClickListener(
            new Button.OnClickListener(){
                public void onClick(View v){
                    //Intent intent = new Intent(MainActivity.this,AirInfoActivity.class);
                    //startActivity(intent);
                    if(btService.enable()) {
                        startActivityForResult(new Intent(MainActivity.this, DeviceListActivity.class),123);
                    }
                }
            }
        );


        if(btService == null){
            btService = new Communication(this,mHandler);
        }

        Log.e("hi","hi");

    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case Communication.REQUEST_CODE_ENABLE:
                if(resultCode == Activity.RESULT_OK){
                    btService.start("test");
                    startActivity(new Intent(MainActivity.this,DeviceListActivity.class));
                }
                break;
        }

        switch(resultCode){
            case DeviceListActivity.BT_SELECTED:
                String s = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                Log.e("mac",s);
                break;
        }
    }

}
