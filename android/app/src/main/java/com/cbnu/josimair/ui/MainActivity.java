package com.cbnu.josimair.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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

import com.cbnu.josimair.Model.util.Alarm;
import com.cbnu.josimair.Model.util.Constants;
import com.cbnu.josimair.Model.entity.IndoorAir;
import com.cbnu.josimair.Model.service.LocationFinder;
import com.cbnu.josimair.Model.entity.OutdoorAir;
import com.cbnu.josimair.Model.util.ResourceChecker;
import com.cbnu.josimair.Model.service.RestAPIService;
import com.cbnu.josimair.Model.service.Communication;
import com.cbnu.josimair.dao.AppDatabase;
import com.cbnu.josimair.Model.entity.Timetable;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Communication communication = null;
    RestAPIService svc = null;

    final Handler mCommunicationHandler = new CommunicationHandler();
    final Handler mRestAPIServiceHandler = new RestAPIServiceHandler();
    final Handler mLocationHandler = new LocationHandler();
    final Handler mNetworkHandler = new NetworkHandler();
    final PermissionListener permissionListener = new LocationPermissionListener();

    public static Fragment fragment;
    AlertDialog.Builder builder;

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

        communication = Communication.getInstance(getApplicationContext(), mCommunicationHandler);
        svc = RestAPIService.getInstance(getApplicationContext(), mRestAPIServiceHandler);

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(getApplicationContext());
                if(db.getDao().isRaady()==0){
                    List<Timetable> dates = Timetable.makeTimetalbes();
                    db.getDao().insertAll(dates.toArray(new Timetable[0]));
                }
            }
        }).start();

        ResourceChecker rc = ResourceChecker.getInstance(getApplicationContext(),mNetworkHandler);
        LocationFinder lf = LocationFinder.getInstance(getApplicationContext(),mLocationHandler);
        if(rc.isNetworkEnable() && lf.isEnabled()){
            lf.requestLocationUpdates();
            svc.start(lf.getLocation());
        }

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("실내 공기 나쁨").setMessage("실내공기 상태가 좋지 않습니다.");
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Alarm.cancel();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Alarm.cancel();
            }
        });
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
            /*
            case R.id.action_add_btn:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        IndoorAir[] airs = new IndoorAir [7];
                        for(int i=0; i<7; i++){
                            airs[i] = new IndoorAir((float)Math.random() * 900);
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
                if(communication.getState() == Communication.STATE_CONNECTED) {
                    byte[] bytes = new byte[4];
                    bytes[0] = 1;
                    bytes[1] = 1;
                    bytes[2] = 1;
                    bytes[3] = 1;
                    communication.write(bytes);
                }else{
                    //IndoorAir indoorAir = new IndoorAir((int)(Math.random()*900));
                    for(int i=0;i<10;i++) {
                        IndoorAir indoorAir = new IndoorAir(900);
                        IndoorAir.setLastKnownIndoorAir(indoorAir);
                    }
                    IndoorAir indoorAir = new IndoorAir(900);
                    mCommunicationHandler.obtainMessage(Constants.MESSAGE_READ, indoorAir).sendToTarget();
                }
                return true;
            */
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
     * 실내 대기정보를 수신 했을 때 생성되는 Message를 처리하는 handler
     */
    private class CommunicationHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            String className = fragment.getClass().getSimpleName().trim();
            switch(msg.what){
                case Constants.MESSAGE_READ:
                    final IndoorAir indoorAir = (IndoorAir)msg.obj;
                    IndoorAir.setLastKnownIndoorAir(indoorAir);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppDatabase.getInstance(getApplicationContext()).getDao().insertAll(indoorAir);
                        }
                    }).start();

                    if(IndoorAir.getBadCount()>10){
                        if(Alarm.isNeeded()){
                            Alarm.setLastAsNow();
                            Alarm.notify(getApplicationContext());
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }

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
     * 외부 대기정보를 수신 했을 때 생성되는 Message를 처리하는 handler
     */
    private class RestAPIServiceHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String className = fragment.getClass().getSimpleName().trim();
            OutdoorAir last = (OutdoorAir)msg.obj;
            OutdoorAir.setLastKnownOutdoorAir(last);
            switch (msg.what) {
                case Constants.MESSAGE_READ:
                    if (className.equals("HomeFragment")) {
                        ((HomeFragment) fragment).updateOutdoorAirInfo(last);
                    }else if (className.equals("AirInformationFragment")) {
                        ((AirInformationFragment) fragment).updateOutdoorAirInfo(last);
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
                    Location last = (Location)msg.obj;
                    svc.start(last);
                }
            }
        }
    }

    /**
     * Handler
     *
     * Network 자원의 변화 상태에 따른 Message를 처리하는 handler
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
