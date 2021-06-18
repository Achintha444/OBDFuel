package com.mtdaps.obdfuel;

import android.bluetooth.BluetoothAdapter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

public class BluetoothActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        // hide the action bar
        getSupportActionBar().hide();

        ConstraintLayout layout =findViewById(R.id.parent_layout);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Snackbar snackbar = Snackbar.make(layout, "Bluetooth Not Available", Snackbar.LENGTH_INDEFINITE);
            View snackbarView = snackbar.getView();
            TextView textview = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
            Typeface font = ResourcesCompat.getFont(getApplicationContext(),R.font.nunito_bold);
            textview.setTypeface(font);
            snackbarView.setBackgroundColor(ContextCompat.getColor(this, R.color.error));
            snackbar.show();
        }
    }
}