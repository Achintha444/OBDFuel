package com.mtdaps.obdfuel.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.UiUtil;

public class BluetoothActivity extends AppCompatActivity implements ActivityInterface {
    FloatingActionButton bluetoothButton;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        setup();

        // hide the action bar
        getSupportActionBar().hide();

        ConstraintLayout layout = findViewById(R.id.parent_layout);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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

    @Override
    public void setup() {
        bluetoothButton = findViewById(R.id.bluetoothButton);
    }
}