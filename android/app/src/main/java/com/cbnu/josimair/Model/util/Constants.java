package com.cbnu.josimair.Model.util;

public interface Constants {
    // Communication Handler를 통해 생성되는 Message types
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Communication Handler를 통해 생성되는 Message types
    public static final int MESSAGE_FAILED = 6;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "JosimAir";
    public static final String TOAST = "toast";

    public static final int GPS_UPDATED = 100;
    public static final int NETWORK_AVAILABLE = 200;
}
