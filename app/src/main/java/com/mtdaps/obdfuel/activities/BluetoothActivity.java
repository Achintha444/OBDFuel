package com.mtdaps.obdfuel.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.UiUtil;

import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements ActivityInterface {
    // UI Elements
    FloatingActionButton bluetoothButton;
    ConstraintLayout layout;

    // Others
    BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> discorverdDevices;

    /**
     * used to show message after bluetooth is connected
     */
    ActivityResultLauncher<Intent> connectBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
                    getPairedDevices();
                    discoverDevices();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is not Enabled", Toast.LENGTH_LONG).show();
                }
            });

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discorverdDevices.add(device);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.println(Log.DEBUG, "DiscoverBluetoothDevice", deviceName + " " + deviceHardwareAddress);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // hide the action bar
        getSupportActionBar().hide();

        setup();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectBluetooth();

        if(bluetoothAdapter.isEnabled()){
            getPairedDevices();
            discoverDevices();
        }


    }

    @Override
    public void setup() {
        bluetoothButton = findViewById(R.id.bluetoothButton);
        layout = findViewById(R.id.parent_layout);
    }

/*
    private void setOnClickBluetoothButton(){

    }*/

    /**
     * Initially Connect Bluetooth
     */
    private void connectBluetooth() {
        if (bluetoothAdapter == null) {
            UiUtil.showErrorSnackbar(layout, this, "Bluetooth Not Available");
        } else {
            bluetoothButton.setOnClickListener(view -> {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    Intent chooserIntent = Intent.createChooser(enableBtIntent, "Enable Bluetooth");
                    //startActivity(chooserIntent);
                    connectBluetoothLauncher.launch(chooserIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * @return Set of paired devices
     */
    private Set getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.println(Log.DEBUG, "Bluetooth Devices", deviceName + " " + deviceHardwareAddress);
            }

        }
        return pairedDevices;
    }

    /**
     * Discover Bluetooth Devices
     */
    private void discoverDevices(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}