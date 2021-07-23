package com.mtdaps.obdfuel.util;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class used to handle BluetoothSocket calls.
 *
 * This is a singleton
 */
public class OBDSocketHandler {
    private BluetoothSocket obdSocket;
    private static OBDSocketHandler obdSocketHandler;

    private OBDSocketHandler() {}

    /**
     * Used to return the singleton ObdSocketHandler object
     * @return obdSocketHandler
     */
    public static OBDSocketHandler getDefaultObdSocketHandler(){
        if (obdSocketHandler == null)
        {
            //synchronized block to remove overhead
            synchronized (OBDSocketHandler.class)
            {
                if(obdSocketHandler==null)
                {
                    // if instance is null, initialize
                    obdSocketHandler = new OBDSocketHandler();
                }

            }
        }
        return obdSocketHandler;
    }

    protected void setObdSocket(BluetoothSocket obdSocket) {
        Log.println(Log.INFO, "ObdSocketHandler", "Socket is set");
        this.obdSocket = obdSocket;
    }

    public InputStream getInputStream() throws IOException {
        return this.obdSocket.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.obdSocket.getOutputStream();
    }

    // close the socket from OBDDeviceConnectThread
}
