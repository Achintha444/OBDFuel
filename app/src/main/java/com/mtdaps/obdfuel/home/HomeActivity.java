package com.mtdaps.obdfuel.home;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.UiUtil;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        UiUtil.showSnackbar(findViewById(R.id.parent_layout),getBaseContext(),"OBD Connected");
    }
}