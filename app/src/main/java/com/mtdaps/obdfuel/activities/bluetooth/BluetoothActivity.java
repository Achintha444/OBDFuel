package com.mtdaps.obdfuel.activities.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.UiUtil;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements ActivityInterface {
    // UI Elements
    private FloatingActionButton bluetoothButton;
    private ConstraintLayout layout;
    private RecyclerView pairedBluetoothDeivces, discoveredBluetoothDevices;
    private Button discoverDevicesButton;
    private TextView selectYourOBDDevices, pairedDevices, availableDevices;


    // Others
    private BluetoothAdapter bluetoothAdapter;
    private final ArrayList<BluetoothDevice> discorverdDevices = new ArrayList<>();

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
    private BluetoothDevicesRecycleViewAdapter bluetoothDevicesRecycleViewAdapter;
    private BluetoothDevicesRecycleViewAdapter discoverBluetoothDevicesRecycleViewAdapter;
    /**
     * used to show message after bluetooth is connected
     */
    private final ActivityResultLauncher<Intent> connectBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth is Enabled", Toast.LENGTH_LONG).show();
                    getPairedDevices();
                    switchVisiblityOnBluetoothAvailablity(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth is not Enabled", Toast.LENGTH_LONG).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // hide the action bar
        getSupportActionBar().hide();

        setup();

        // Paired Devices Recylcer View
        bluetoothDevicesRecycleViewAdapter = new BluetoothDevicesRecycleViewAdapter(this);
        pairedBluetoothDeivces.setAdapter(bluetoothDevicesRecycleViewAdapter);
        pairedBluetoothDeivces.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<BluetoothDevice> emptyArrayList = new ArrayList<>();
        bluetoothDevicesRecycleViewAdapter.setBluetoothDevices(emptyArrayList);

        // Discover Devices Recylcer View
        discoverBluetoothDevicesRecycleViewAdapter = new BluetoothDevicesRecycleViewAdapter(this);
        discoveredBluetoothDevices.setAdapter(bluetoothDevicesRecycleViewAdapter);
        discoveredBluetoothDevices.setLayoutManager(new LinearLayoutManager(this));
        discoverBluetoothDevicesRecycleViewAdapter.setBluetoothDevices(discorverdDevices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectBluetooth();

        if (bluetoothAdapter.isEnabled()) {
            getPairedDevices();
        } else{
            switchVisiblityOnBluetoothAvailablity(View.GONE);
        }


    }

    @Override
    public void setup() {
        bluetoothButton = findViewById(R.id.bluetoothButton);
        layout = findViewById(R.id.parent_layout);
        pairedBluetoothDeivces = findViewById(R.id.pairedBluetoothDeivces);
        discoveredBluetoothDevices = findViewById(R.id.discoveredBluetoothDevices);
        discoverDevicesButton = findViewById(R.id.discoverDevicesButton);
        selectYourOBDDevices = findViewById(R.id.selectYourOBDDevice);
        pairedDevices = findViewById(R.id.pairedDevices);
        availableDevices = findViewById(R.id.availableDevices);

        discoverDevicesButtonOnClick();
    }

    /**
     *
     * @param  visiblity
     *
     * Used to switch visibility of elements on availablity of bluetooth
     */
    private void switchVisiblityOnBluetoothAvailablity(int visiblity) {
        pairedBluetoothDeivces.setVisibility(visiblity);
        discoverDevicesButton.setVisibility(visiblity);
        selectYourOBDDevices.setVisibility(visiblity);
        pairedDevices.setVisibility(visiblity);
        availableDevices.setVisibility(visiblity);
    }


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
    private ArrayList getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.println(Log.DEBUG, "Bluetooth Devices", deviceName + " " + deviceHardwareAddress);
            }

        }

        ArrayList<BluetoothDevice> pairedDevicesArrayList = new ArrayList<>();
        pairedDevicesArrayList.addAll(pairedDevices);

        bluetoothDevicesRecycleViewAdapter.setBluetoothDevices(pairedDevicesArrayList);

        return pairedDevicesArrayList;
    }

    /**
     * Discover Bluetooth Devices
     */
    private void discoverDevices() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);
        discoverBluetoothDevicesRecycleViewAdapter.setBluetoothDevices(discorverdDevices);
    }

    private void discoverDevicesButtonOnClick() {
        discoverDevicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}