package com.cbnu.josimair.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.cbnu.josimair.Model.LocationFinder;
import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.Model.ResourceChecker;
import com.cbnu.josimair.Model.RestAPIService;
import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.R;
import com.cbnu.josimair.ui.bluetooth.DeviceListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static Communication communication = null;
    public static RestAPIService svc = null;
    public static AppDatabase db = null;

    public static LocationFinder locationFinder;
    public static ResourceChecker resourceChecker;
    public static OutdoorAir outdoorAir;

    public Timer airUpdateTimer;

    public PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
        }
    };


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

        if(communication == null) communication = new Communication(this,mHandler);
        if(db == null) db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "josimAirTest").build();
        locationFinder = new LocationFinder(this);
        resourceChecker = new ResourceChecker(this);
        outdoorAir = OutdoorAir.getInstance();
        svc = new RestAPIService(this);

        TedPermission.with(this)
            .setPermissionListener(permissionListener)
            .setRationaleMessage("외부 대기정보를 가져오기 위해 위치 권한이 필요합니다")
            .setDeniedMessage("[설정] > [권한] 에서 다시 권한을 허용할 수 있습니다.")
            .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .check();
        setOpeningTimerTask();
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
            case 1:

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

    /*
    메뉴를 액션 바에 추가
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    /*
    메뉴가 선택 되었을 떄의 callback
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.setting_btn:
                Intent intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setOpeningTimerTask(){
        airUpdateTimer = new Timer();
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                          if(resourceChecker.isNetworkEnable() && locationFinder.isEnabled()){
                              Location loc = locationFinder.getLocation();
                              if(loc==null) return;
                              svc.setLocation(loc);
                              svc.start();
                              airUpdateTimer.cancel();
                              setUpdateTask();
                          }
                      }
                  }
                );
            }
        };
        airUpdateTimer.schedule(timerTask,0,1000);
    }

    private void setUpdateTask(){
        airUpdateTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(resourceChecker.isNetworkEnable() && locationFinder.isEnabled()){
                            svc.setLocation(locationFinder.getLocation());
                            svc.start();
                        }else{
                            airUpdateTimer.cancel();
                            setOpeningTimerTask();
                        }
                    }
                });
            }
        };
        airUpdateTimer.schedule(timerTask,30*60*1000,30*60*1000);
    }
}
