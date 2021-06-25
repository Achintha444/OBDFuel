package com.mtdaps.obdfuel.util;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class used to handle BluetoothSocket calls.
 *
 * This is a singleton
 */
public class ObdSocketHandler {
    private BluetoothSocket obdSocket;
    private static ObdSocketHandler obdSocketHandler;

    private ObdSocketHandler() {}

    /**
     * Used to return the singleton ObdSocketHandler object
     * @param obdSocket
     * @return obdSocketHandler
     */
    public static ObdSocketHandler getDefaultObdSocketHandler(BluetoothSocket obdSocket){
        if (obdSocketHandler == null)
        {
            //synchronized block to remove overhead
            synchronized (ObdSocketHandler.class)
            {
                if(obdSocketHandler==null)
                {
                    // if instance is null, initialize
                    obdSocketHandler = new ObdSocketHandler();
                }

            }
        }
        return obdSocketHandler;
    }

    public void setObdSocket(BluetoothSocket obdSocket) {
        this.obdSocket = obdSocket;
    }

    public InputStream getInputStream() throws IOException {
        return this.obdSocket.getInputStream();
    }

    public OutputStream getIutputStream() throws IOException {
        return this.obdSocket.getOutputStream();
    }

    public void closeSocket() throws IOException {
        this.obdSocket.close();
    }
}
