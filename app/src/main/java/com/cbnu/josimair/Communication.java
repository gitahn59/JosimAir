package com.cbnu.josimair;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.util.Set;


public class Communication {

    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;

    public final static int REQUEST_CODE_ENABLE = 201;

    Communication(Activity ac, Handler h){
        mActivity = ac;
        mHandler = h;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    String [] getDevices() {
        return null;
    }

    boolean enable(){
        if (btAdapter != null) {
            if(!btAdapter.isEnabled()){
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(i, REQUEST_CODE_ENABLE);
                return false;
            }else{
                return true;
            }
        }
        return false;
    }

    boolean start(String devic){
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        if(pairedDevices.size()>0){
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                //String deviceName = device.getName();
                //String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.d(device.getName(),device.getAddress());
            }
        }

        return true;
    }

    void end(){

    }

}
