package com.mtdaps.obdfuel.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.mtdaps.obdfuel.home.HomeActivity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static androidx.core.content.ContextCompat.startActivity;

public class OBDDeviceConnectThread extends Thread {
    private static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final BluetoothDevice mmDevice;
    private final Context context;
    private BluetoothSocket mmSocket;

    public OBDDeviceConnectThread(BluetoothDevice obdDevice, Context context) {
        this.context = context;
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = obdDevice;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = mmDevice.createRfcommSocketToServiceRecord(UiUtil.DEVICE_UUID);
        } catch (IOException e) {
            Log.e("OBDConnectionError", "Socket's create() method failed", e);
        }
        mmSocket = tmp;
      /*  try {
            mmSocket = createBluetoothSocket(mmDevice);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void run() {

        //if (bluetoothAdapter.isDiscovering()) {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery();
        //}

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            connectException.printStackTrace();
            Log.e("Try Again", "trying fallback...");
            try {
                try {
                    mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", int.class).invoke(mmDevice, 2);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    cancel();
                    return;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    cancel();
                    return;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    cancel();
                    return;
                }
                mmSocket.connect();

                Log.e("Try Again", "Connected");
            } catch (IOException e) {
                Log.e("Try Again", "Failed Again...");
                e.printStackTrace();
                cancel();
                return;
            }
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        ObdSocketHandler.getDefaultObdSocketHandler().setObdSocket(mmSocket);
        Intent homeIntent = new Intent(context, HomeActivity.class);
        startActivity(context, homeIntent, null);
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("OBDConnectionError", "Could not close the client socket", e);
        }
        return;
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)
            throws IOException {
        if (Build.VERSION.SDK_INT >= 10) {
            try {
                return (BluetoothSocket) (BluetoothSocket) device.getClass().getMethod("createRfcommSocket", int.class).invoke(device, 1);
            } catch (Exception e) {
                Log.e("Connection Error", "Could not create Insecure RFComm Connection", e);
            }
        }
        return device.createRfcommSocketToServiceRecord(UiUtil.DEVICE_UUID);
    }
}
