package com.mtdaps.obdfuel.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // hide the action bar
        getSupportActionBar().hide();

        setup();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        connectBluetooth();
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
            bluetoothButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!bluetoothAdapter.isEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                        Intent chooserIntent = Intent.createChooser(enableBtIntent, "Open URL...");
                        startActivity(chooserIntent);
                    }
                }
            });
        }
    }

    /**
     *
     * @return Set of paired devices
     */
    private Set getPairedDevices(){
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        return pairedDevices;
    }
}