package com.cbnu.josimair.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cbnu.josimair.Model.ArpltnInforInqireSvc;
import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.R;
import com.cbnu.josimair.ui.bluetooth.DeviceListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

public class MainBtmActivity extends AppCompatActivity {

    public static Communication communication = null;
    public static ArpltnInforInqireSvc svc = null;
    public static AppDatabase db = null;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            if(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_ON){
                // Bluetooth is disconnected, do handling here
                Log.v("JosimAir","BT ON");
            }
        }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_btm);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //싱글톤
        if(communication == null) communication = new Communication(this,mHandler);
        if(svc == null) {
            svc = new ArpltnInforInqireSvc(this);
            svc.setlocation("서울");
            svc.start();
        }
        if(db == null) db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "josimAirTest").build();
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case Communication.REQUEST_CODE_ENABLE:
                if(resultCode == Activity.RESULT_OK){
                    //communication.start("test");
                    //startActivity(new Intent(MainBtmActivity.this,DeviceListActivity.class));
                }
                break;
        }

        switch(resultCode){
            case DeviceListActivity.BT_SELECTED:
                String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                communication.start(address);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("JosimAir","destroy");
        communication.end();
        finish();
    }
}
