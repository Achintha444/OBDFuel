package com.mtdaps.obdfuel.activities.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.mtdaps.obdfuel.util.UiUtil;

import java.io.IOException;

public class OBDDeviceConnectThread extends Thread{
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    private static final BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();

    protected OBDDeviceConnectThread(BluetoothDevice obdDevice) {
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
    }

    public void run() {

        if(bluetoothAdapter.isDiscovering()){
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
        }

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
        } catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e("OBDConnectionError", "Could not close the client socket", closeException);
            }
            return;
        }

        // The connection attempt succeeded. Perform work associated with
        // the connection in a separate thread.
        try {
            Log.println(Log.DEBUG, "Bluetooth Device Stream", mmSocket.getInputStream().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("OBDConnectionError", "Could not close the client socket", e);
        }
    }
}
