package com.cbnu.josimair.Model;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.cbnu.josimair.Model.IndoorAir;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Communication {

    private BluetoothAdapter btAdapter;
    private Activity mActivity;
    private Handler mHandler;
    private BluetoothSocket btSocket;
    private String mAddress;
    private IndoorAir receivedAir;
    private AsyncTask task;
    private boolean isEnded;

    public final static int REQUEST_CODE_ENABLE = 2001;
    public final static int RESULT_CODE_BTLIST = 2002;

    public interface ConnectedListener{
        void onReceivedEvent();
    }
    private ConnectedListener mConnectedListener;

    public void setConnectedCallback(ConnectedListener listener){
        mConnectedListener = listener;
    }

    public interface ReceivedListener{
        void onReceivedEvent();
    }
    private ReceivedListener mReceivedListener;
    public void setReceivedCallback(ReceivedListener listener){
        mReceivedListener = listener;
    }

    public Communication(Activity ac, Handler h){
        mActivity = ac;
        mHandler = h;
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                return null;
            }
        };
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

    public void start(String address){
        mAddress=address;
        task.execute(new Runnable() {
            @Override
            public void run() {
                BluetoothDevice device=btAdapter.getRemoteDevice(mAddress);
                BluetoothSocket tmp=null;
                try{
                    tmp = device.createRfcommSocketToServiceRecord(UUID.randomUUID());
                }catch(IOException e){

                }
                btSocket=tmp;

                //connected event occurred
                mConnectedListener.onReceivedEvent();
            }
        });
    }

    public void start_Test_Using_RandomData() {
        isEnded = false;
        task.execute(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        receivedAir = new IndoorAir((float) Math.random() * 15);
                        //connected event occurred
                        mReceivedListener.onReceivedEvent();
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }while(!isEnded);
            }
        });
    }

    public void end(){
        try {
            task.cancel(true);
            isEnded=true;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public IndoorAir getReceivedAir(){
        return this.receivedAir;
    }

}
