package com.cbnu.josimair;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Communication {

    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;
    private BluetoothSocket btSocket;


    public final static int REQUEST_CODE_ENABLE = 2001;
    public final static int RESULT_CODE_BTLIST = 2002;

    public Communication(Activity ac, Handler h){
        mActivity = ac;
        mHandler = h;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    String [] getDevices() {
        return null;
    }

    public boolean enable(){
        if (btAdapter != null)
            if(btAdapter.isEnabled())
                return true;
        return false;
    }

    public void showDialog(){
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mActivity.startActivityForResult(i, REQUEST_CODE_ENABLE);
            }
        }
    }

    public boolean start(String address){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        BluetoothDevice device=btAdapter.getRemoteDevice(address);
        BluetoothSocket tmp=null;
        try{
            tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
        }catch(IOException e){

        }
        btSocket=tmp;
        return true;
    }

    public void end(){

    }

}
