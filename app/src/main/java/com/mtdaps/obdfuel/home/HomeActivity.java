package com.mtdaps.obdfuel.home;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.UiUtil;

public class HomeActivity extends AppCompatActivity {

    private final BluetoothSocket obdBluetoothSocket = getIntent().getParcelableExtra(UiUtil.OBD_BLUETOOTH_SOCKET);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}