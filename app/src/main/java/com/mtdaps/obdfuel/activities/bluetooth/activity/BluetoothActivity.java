package com.mtdaps.obdfuel.activities.bluetooth.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.activities.bluetooth.util.BluetoothDevicesRecycleViewAdapter;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.UiUtil;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity implements ActivityInterface {
    private final ArrayList<BluetoothDevice> discorverdDevices = new ArrayList<>();
    // UI Elements
    private Toolbar bluetoothToolbar;
    private FloatingActionButton bluetoothButton;
    private ConstraintLayout layout;
    private ScrollView scrollView;
    private RecyclerView pairedBluetoothDeivces, discoveredBluetoothDevices;
    private Button discoverDevicesButton;
    private TextView selectYourOBDDevices, pairedDevices, availableDevices, noPairedDevices, loadingText, noAvailableText1, noAvailableText2;
    private ProgressBar loading;
    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.println(Log.DEBUG, "BluetoothDiscoveryDebug", action);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                discorverdDevices.add(device);


                //* Set visibility of UI elements
                discoverBluetoothDevicesRecycleViewAdapter.setBluetoothDevices(discorverdDevices);
                discoveredBluetoothDevices.setVisibility(View.VISIBLE);
                scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));

                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                Log.println(Log.DEBUG, "DiscoverBluetoothDevice", deviceName + " " + deviceHardwareAddress);

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                loading.setVisibility(View.VISIBLE);
                loadingText.setVisibility(View.VISIBLE);
                discoverDevicesButton.setVisibility(View.GONE);
                discoveredBluetoothDevices.setVisibility(View.GONE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                loading.setVisibility(View.GONE);
                loadingText.setVisibility(View.GONE);

                if (discorverdDevices.size() == 0) {
                    Log.println(Log.DEBUG, "discoveredDevices size", Integer.toString(discorverdDevices.size() + 4));
                    noAvailableText1.setVisibility(View.VISIBLE);
                    noAvailableText2.setVisibility(View.VISIBLE);
                    discoverDevicesButton.setVisibility(View.VISIBLE);
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                } else {
                    discoverDevicesButton.setVisibility(View.VISIBLE);
                }
            }
        }
    };
    private AlertDialog dialog;

    //Other
    private AlertDialog.Builder dialogBuilder;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevicesRecycleViewAdapter bluetoothDevicesRecycleViewAdapter, discoverBluetoothDevicesRecycleViewAdapter;
    /**
     * used to show message after bluetooth is connected
     */
    private final ActivityResultLauncher<Intent> connectBluetoothLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Toast.makeText(getApplicationContext(), R.string.bluetooth_enabled, Toast.LENGTH_LONG).show();
                    getPairedDevices();
                    scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
                } else {
                    Toast.makeText(getApplicationContext(), R.string.bluetooth_not_enabled, Toast.LENGTH_LONG).show();
                }
            });
    private Bundle bundle;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        bundle = savedInstanceState;

        setup();

        setSupportActionBar(bluetoothToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectBluetooth();

        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);

        if (bluetoothAdapter.isEnabled()) {
            getPairedDevices();
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        } else {
            switchVisiblityOnBluetoothAvailablity(View.GONE);
        }
    }

    @Override
    public void setup() {
        bluetoothButton = findViewById(R.id.bluetoothButton);
        noAvailableText1 = findViewById(R.id.noAvailableText1);
        noAvailableText2 = findViewById(R.id.noAvailableText2);
        layout = findViewById(R.id.parent_layout);
        scrollView = findViewById(R.id.scrollView);
        pairedBluetoothDeivces = findViewById(R.id.pairedBluetoothDeivces);
        discoveredBluetoothDevices = findViewById(R.id.discoveredBluetoothDevices);
        discoverDevicesButton = findViewById(R.id.discoverDevicesButton);
        selectYourOBDDevices = findViewById(R.id.selectYourOBDDevice);
        pairedDevices = findViewById(R.id.pairedDevices);
        availableDevices = findViewById(R.id.availableDevices);
        noPairedDevices = findViewById(R.id.noPairedDevices);
        loadingText = findViewById(R.id.loadingText);
        loading = findViewById(R.id.loading);
        bluetoothToolbar = findViewById(R.id.bluetoothToolbar);

        // Paired Devices Recylcer View
        bluetoothDevicesRecycleViewAdapter = new BluetoothDevicesRecycleViewAdapter(this);
        pairedBluetoothDeivces.setAdapter(bluetoothDevicesRecycleViewAdapter);
        pairedBluetoothDeivces.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<BluetoothDevice> emptyArrayList = new ArrayList<>();
        bluetoothDevicesRecycleViewAdapter.setBluetoothDevices(emptyArrayList);

        // Discover Devices Recylcer View
        discoverBluetoothDevicesRecycleViewAdapter = new BluetoothDevicesRecycleViewAdapter(this);
        discoveredBluetoothDevices.setAdapter(discoverBluetoothDevicesRecycleViewAdapter);
        discoveredBluetoothDevices.setLayoutManager(new LinearLayoutManager(this));
        discoverBluetoothDevicesRecycleViewAdapter.setBluetoothDevices(discorverdDevices);

        discoverDevicesButtonOnClick();
    }

    /**
     * @param visiblity Used to switch visibility of elements on availablity of bluetooth
     */
    private void switchVisiblityOnBluetoothAvailablity(int visiblity) {
        pairedBluetoothDeivces.setVisibility(visiblity);
        discoverDevicesButton.setVisibility(visiblity);
        selectYourOBDDevices.setVisibility(visiblity);
        pairedDevices.setVisibility(visiblity);
        availableDevices.setVisibility(visiblity);
        noPairedDevices.setVisibility(View.GONE);
    }


    /**
     * Initially Connect Bluetooth
     */
    private void connectBluetooth() {
        if (bluetoothAdapter == null) {
            UiUtil.showErrorSnackbar(layout, this, "Bluetooth Not Available").show();
        } else {
            bluetoothButton.setOnClickListener(view -> {
                if (!bluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    Intent chooserIntent = Intent.createChooser(enableBtIntent, "Enable Bluetooth");
                    //startActivity(chooserIntent);
                    connectBluetoothLauncher.launch(chooserIntent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.bluetooth_enabled, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    /**
     * @return Set of paired devices
     */
    private ArrayList getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<BluetoothDevice> pairedDevicesArrayList = new ArrayList<>();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address

                Log.println(Log.DEBUG, "Bluetooth Devices", deviceName + " " + deviceHardwareAddress);
            }


            pairedDevicesArrayList.addAll(pairedDevices);
            bluetoothDevicesRecycleViewAdapter.setBluetoothDevices(pairedDevicesArrayList);
            switchVisiblityOnBluetoothAvailablity(View.VISIBLE);

        } else {
            switchVisiblityOnBluetoothAvailablity(View.VISIBLE);
            pairedBluetoothDeivces.setVisibility(View.GONE);
            noPairedDevices.setVisibility(View.VISIBLE);
        }

        return pairedDevicesArrayList;
    }

    /**
     * Discover Bluetooth Devices
     */
    private void discoverDevices() {

        if (bluetoothAdapter.isEnabled()) {
            //* No available text will disappear once discovery started
            if (noAvailableText1.getVisibility() == View.VISIBLE && noAvailableText2.getVisibility() == View.VISIBLE) {
                noAvailableText1.setVisibility(View.GONE);
                noAvailableText2.setVisibility(View.GONE);
            }

            IntentFilter filter;
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            registerReceiver(receiver, filter);
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(receiver, filter);

            // To clear the items in the ArrayList before starting the process
            if (discorverdDevices.size() != 0) {
                discorverdDevices.clear();
            }

            boolean checkDiscoveryWorking = bluetoothAdapter.startDiscovery();

            Log.println(Log.DEBUG, "BlUETOOTHDiscoveryError", Boolean.toString(checkDiscoveryWorking));

            if (!checkDiscoveryWorking) {
                Toast.makeText(this, "bluetooth_discovery_failed", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, R.string.bluetooth_not_enabled, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check if GPS is Enabled
     */
    private void checkIfGPSEnabled() {
        try {

            if (!isLocationEnabled()) {
                // notify user
                dialogBuilder = UiUtil.createWarningAlertDialogBuilder(bundle, this, "Enable Location", "Location access is required for Bluetooth Connection.", "Enable Locaiton", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                dialog = dialogBuilder.create();
                // Used to check if Location is on
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        while (!isLocationEnabled()) {
                            Log.println(Log.INFO, "Location", "THREAD CHECK 1");
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (isLocationEnabled()) {
                            discoverDevices();
                        }
                        dialog.dismiss();
                    }
                };

                dialog.show();
                thread.start();
            } else {
                discoverDevices();
            }

        } catch (Exception ex) {
            Toast.makeText(this, "bluetooth_discovery_failed", Toast.LENGTH_LONG).show();
        }


    }

    /**
     * From Android 10 onwards it need Access Location to search bluetooth Devices
     */
    private void checkForLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                checkIfGPSEnabled();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,}, 1);
            }
        }
    }

    /**
     * Request Access Location while using the App, because bluetooth need location to start discovering devices
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            discoverDevices();
        } else {
            checkForLocationPermission();
        }
    }

    private void discoverDevicesButtonOnClick() {
        discoverDevicesButton.setOnClickListener(view -> checkForLocationPermission());
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}