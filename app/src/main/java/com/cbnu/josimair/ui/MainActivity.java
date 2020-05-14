package com.cbnu.josimair.ui;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.cbnu.josimair.Model.Constants;
import com.cbnu.josimair.Model.IndoorAir;
import com.cbnu.josimair.Model.LocationFinder;
import com.cbnu.josimair.Model.OutdoorAir;
import com.cbnu.josimair.Model.ResourceChecker;
import com.cbnu.josimair.Model.RestAPIService;
import com.cbnu.josimair.Model.Communication;
import com.cbnu.josimair.Model.AppDatabase;
import com.cbnu.josimair.Model.Timetable;
import com.cbnu.josimair.R;
import com.cbnu.josimair.ui.airInfo.AirInformationFragment;
import com.cbnu.josimair.ui.bluetooth.DeviceListActivity;
import com.cbnu.josimair.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public Communication communication = null;
    public static RestAPIService svc = null;
    public static OutdoorAir outdoorAir = null;
    public static Fragment fragment;

    final Handler mCommunicationHandler = new CommunicationHandler();
    final Handler mRestAPIServiceHandler = new RestAPIServiceHandler();
    final Handler mLocationHandler = new LocationHandler();
    final Handler mNetworkHandler = new NetworkHandler();
    public final PermissionListener permissionListener = new LocationPermissionListener();

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

        if(communication == null) communication = new Communication(this, mCommunicationHandler);
        svc = new RestAPIService(this, mRestAPIServiceHandler);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                if(db.indoorAirDao().isRaady()==0){
                    List<Timetable> dates = Timetable.makeTimetalbes();
                    db.indoorAirDao().insertAll(dates.toArray(new Timetable[0]));
                }
            }
        }).start();

        ResourceChecker rc = ResourceChecker.getInstance(getApplicationContext(),mNetworkHandler);
        LocationFinder lf = LocationFinder.getInstance(getApplicationContext(),mLocationHandler);
        if(rc.isNetworkEnable() && lf.isEnabled()){
            lf.requestLocationUpdates();
            svc.setLocation(lf.getLocation());
            svc.start();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        checkPermission();
    }

    public void checkPermission(){
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage("외부 대기정보를 가져오기 위해 위치 권한이 필요합니다")
                .setDeniedMessage("[설정] > [권한] 에서 다시 권한을 허용할 수 있습니다.")
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .check();
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch(requestCode){
            case Communication.REQUEST_CODE_ENABLE:
                if(resultCode == Activity.RESULT_OK){
                    communication.showDeviceList();
                }
                break;
            case 1:
                break;
        }

        switch(resultCode){
            case DeviceListActivity.BT_SELECTED:
                String address = data.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                communication.connect(address);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("JosimAir","destroy");
        communication.stop();
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
        Intent intent;
        switch (item.getItemId()) {
            case R.id.setting_btn:
                intent = new Intent(this, SettingsActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.management_btn:
                intent = new Intent(this, ManagementActivity.class);
                this.startActivity(intent);
                return true;
            case R.id.action_add_btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IndoorAir[] airs = new IndoorAir [7];
                        for(int i=0; i<7; i++){
                            airs[i] = new IndoorAir((float)Math.random() * 40);
                            Calendar now = Calendar.getInstance();
                            now.add(Calendar.DATE,-i);
                            Date d = now.getTime();
                            airs[i].setTime(d);
                        }
                        AppDatabase.getInstance(getApplicationContext()).indoorAirDao().insertAll(airs);
                    }
                }).start();
                return true;
            case R.id.action_btServer_btn:
                communication.start();
                return true;
            case R.id.action_btServer_Sign_btn:
                byte[] bytes = new byte[4];
                bytes[0] = 1;
                bytes[1] = 1;
                bytes[2] = 1;
                bytes[3] = 1;
                if(communication.getState() == Communication.STATE_CONNECTED) {
                    communication.write(bytes);
                }else{
                    IndoorAir indoorAir = new IndoorAir((int)(Math.random()*40));
                    mCommunicationHandler.obtainMessage(Constants.MESSAGE_READ,indoorAir).sendToTarget();
                }
                return true;
            case R.id.action_btClient_btn:
                if(!communication.enable()){
                    communication.showDialog();
                }else{
                    startActivityForResult(new Intent(this, DeviceListActivity.class),Communication.RESULT_CODE_BTLIST);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Handler
     *
     * 실내 대기정로를 했을 때 생성되는 Message를 처리하는 handler
     */
    private class CommunicationHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            String className = fragment.getClass().getSimpleName().trim();
            switch(msg.what){
                case Constants.MESSAGE_READ:
                    IndoorAir indoorAir = (IndoorAir)msg.obj;
                    IndoorAir.setLastKnownIndoorAir(indoorAir);
                    if(className.equals("HomeFragment")){
                        ((HomeFragment)fragment).updateIndoorAirInfo(indoorAir);
                    }else if(className.equals("AirInformationFragment")){
                        ((AirInformationFragment)fragment).updateIndoorAirInfo(indoorAir);
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    /**
     * Handler
     *
     * 외부 대기정로를 했을 때 생성되는 Message를 처리하는 handler
     */
    private class RestAPIServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String className = fragment.getClass().getSimpleName().trim();
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    if (className.equals("HomeFragment")) {
                        ((HomeFragment) fragment).updateOutdoorAirInfo();
                    }else if (className.equals("AirInformationFragment")) {
                        ((AirInformationFragment) fragment).updateOutdoorAirInfo();
                    }
                    break;
                case Constants.MESSAGE_FAILED:
                    break;
            }
        }
    }

    /**
     * Handler
     *
     * GPS 위치 정보를 수신 했을 때 생성되는 Message를 처리하는 handler
     */
    private class LocationHandler extends  Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==Constants.GPS_UPDATED) {
                ResourceChecker rc = ResourceChecker.getInstance(getApplicationContext(), mNetworkHandler);
                LocationFinder locationFinder = LocationFinder.getInstance(getApplicationContext(), mLocationHandler);
                if (rc.isNetworkEnable()) {
                    svc.setLocation((Location) msg.obj);
                    svc.start();
                }
            }
        }
    }

    /**
     * Handler
     *
     * Network자원의 변화 상태에 따른 Message를 처리하는 handler
     */
    private class NetworkHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == Constants.NETWORK_AVAILABLE) {
                LocationFinder locationFinder = LocationFinder.getInstance(getApplicationContext(), mLocationHandler);
                if (locationFinder.isEnabled()) {
                    locationFinder.requestLocationUpdates();
                }
            }
        }
    }

    /**
     * Listener
     *
     * 위치 정보에 대한 Permission의 변화를 처리하는 Listener
     */
    private class LocationPermissionListener implements PermissionListener{
        @Override
        public void onPermissionGranted() {
            LocationFinder locationFinder = LocationFinder.getInstance(getApplicationContext(), mLocationHandler);
            if(locationFinder.isEnabled()) {
                locationFinder.requestLocationUpdates();
            }
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) { }
    }
}
