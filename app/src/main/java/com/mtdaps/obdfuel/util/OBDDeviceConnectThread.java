package com.mtdaps.obdfuel.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

/**
 * This is used to connect the OBD device through bluetooth
 */

public class OBDDeviceConnectThread extends Thread {
    private static final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static OBDDeviceConnectThreadState state;
    private static OBDDeviceConnectThread obdDeviceConnectThread;
    private final BluetoothDevice mmDevice;
    private final Context context;
    private BluetoothSocket mmSocket;

    private OBDDeviceConnectThread(BluetoothDevice obdDevice, Context context) {
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

    /**
     * Used to return the singleton ObdSocketHandler object
     *
     * @return obdSocketHandler
     */
    public static OBDDeviceConnectThread setDefaultOBDDeviceConnectThread(BluetoothDevice obdDevice, Context context) {
        if (obdDeviceConnectThread == null) {
            //synchronized block to remove overhead
            synchronized (OBDSocketHandler.class) {
                if (obdDeviceConnectThread == null) {
                    // if instance is null, initialize
                    obdDeviceConnectThread = new OBDDeviceConnectThread(obdDevice, context);
                }

            }
        }
        return obdDeviceConnectThread;
    }

    public static OBDDeviceConnectThread getDefaultOBDDeviceConnectThread() {
        if (obdDeviceConnectThread == null) {
            //synchronized block to remove overhead
            synchronized (OBDSocketHandler.class) {
                if (obdDeviceConnectThread == null) {
                    Log.println(Log.ERROR, "OBDDeviceConnectThread", "OBD Device is already connected. Disconnect it to Run Again");
                }

            }
        }
        return obdDeviceConnectThread;
    }

    public void run() {

        if ((getOBDDeviceConnectThreadState() != OBDDeviceConnectThreadState.WAITING) || (getOBDDeviceConnectThreadState() != OBDDeviceConnectThreadState.CONNECTED)) {
            //if (bluetoothAdapter.isDiscovering()) {
            // Cancel discovery because it otherwise slows down the connection.
            changeState(OBDDeviceConnectThreadState.WAITING);
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
                        changeState(OBDDeviceConnectThreadState.FAILED);
                        return;
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        cancel();
                        changeState(OBDDeviceConnectThreadState.FAILED);
                        return;
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        cancel();
                        changeState(OBDDeviceConnectThreadState.FAILED);
                        return;
                    }
                    mmSocket.connect();

                    Log.e("Try Again", "Connected");
                } catch (IOException e) {
                    Log.e("Try Again", "Failed Again...");
                    e.printStackTrace();
                    cancel();
                    changeState(OBDDeviceConnectThreadState.FAILED);
                    return;
                }
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            OBDSocketHandler.getDefaultObdSocketHandler().setObdSocket(mmSocket);

            if (checkConnectedToWrongDevice()) {
                Log.println(Log.ERROR, "OBDDeviceConnectThread", "Wrong Device Connected!");
                cancel();
                changeState(OBDDeviceConnectThreadState.WRONG_DEVICE);
            } else {
                this.changeState(OBDDeviceConnectThreadState.CONNECTED);
            }
        } else {
            Log.println(Log.ERROR, "OBDDeviceConnectThread", "OBD Device is already connected. Disconnect it to Run Again");
        }
    }

    // Closes the client socket and causes the thread to finish.
    private void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e("OBDConnectionError", "Could not close the client socket", e);
        }
        synchronized (OBDSocketHandler.class) {
            if (obdDeviceConnectThread != null) {
                obdDeviceConnectThread = null;
            }
        }
        return;
    }

    // Closes the client socket and causes the thread to finish.
    public void changeOBDDevice() throws IOException {
        mmSocket.close();

        if (getOBDDeviceConnectThreadState() == OBDDeviceConnectThreadState.CONNECTED) {
            obdDeviceConnectThread = null;
            changeState(OBDDeviceConnectThreadState.FREE);
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

    private void changeState(OBDDeviceConnectThreadState state) {
        OBDDeviceConnectThread.state = state;
    }

    public OBDDeviceConnectThreadState getOBDDeviceConnectThreadState() {
        return OBDDeviceConnectThread.state;
    }

    /**
     * @return true is wrong device connected
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean checkConnectedToWrongDevice() {
        try {
            Log.println(Log.ERROR, "OBDDeviceConnectThread", "Wrong Device Connected! - 1");
            Thread thread = OBDValues.check(OBDSocketHandler.getDefaultObdSocketHandler().getInputStream(), OBDSocketHandler.getDefaultObdSocketHandler().getOutputStream());

            thread.start();

            // Run a task specified by a Runnable Object asynchronously.
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {

                try {
                    this.sleep(5000);
                } catch (InterruptedException e) {
                   return true;
                }

                if (thread.isAlive()) {
                    thread.interrupt();
                    return true;
                }
                return false;

            });

            return future.get();

        } catch (IOException e) {
            Log.println(Log.ERROR, "HomeActivity", "Connected to Wrong Device");
            e.printStackTrace();
            return true;
        } catch (InterruptedException e) {
            Log.println(Log.ERROR, "HomeActivity", "Connected to Wrong Device");
            e.printStackTrace();
            return true;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return true;
        }
    }
}

