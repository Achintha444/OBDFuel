package com.mtdaps.obdfuel.activities.home.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.activities.home.util.HomeTabAdapter;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.UiUtil;

public class HomeActivity extends AppCompatActivity implements ActivityInterface {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private LocationManager locationManager;
    private BluetoothAdapter bluetoothAdapter;

    private AlertDialog locaitonDialog, bluetoothDialog;
    private AlertDialog.Builder locaitonDialogBuilder, bluetoothDialogBuilder;

    private Bundle bundle;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hide the action bar
        getSupportActionBar().hide();

        UiUtil.showSnackbar(findViewById(R.id.home_parent_layout), getBaseContext(), "OBD Connected").show();

        setup();

        prepareAdapter(viewPager);

        locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // Used to call the static alert dialog maker
        bundle = savedInstanceState;

        servicesCheckThread();
    }

    @Override
    public void setup() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        flag = true;
    }

    private void prepareAdapter(ViewPager2 viewPager) {
        HomeTabAdapter homeTabAdapter = new HomeTabAdapter(this);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(homeTabAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(5000));
        viewPager.animate();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) {
                        tab.setText("Dashboard");
                        tab.setIcon(R.drawable.ic_sharp_dashboard_24);

                    } else {
                        tab.setText("Send Data");
                        tab.setIcon(R.drawable.ic_baseline_cloud_upload_24);
                    }

                }
        ).attach();

    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    /**
     * Continually check if location and bluetooth is enabled.
     */
    private void servicesCheckThread() {
        bluetoothDialogBuilder = UiUtil.createWarningAlertDialogBuilder(bundle, HomeActivity.this, "Enable Bluetooth", "Bluetooth is not enabled.", "Enable Bluetooth", Settings.ACTION_BLUETOOTH_SETTINGS);
        bluetoothDialog = bluetoothDialogBuilder.create();

        locaitonDialogBuilder = UiUtil.createWarningAlertDialogBuilder(bundle, HomeActivity.this, "Enable Location", "Location access is required for Bluetooth Connection.", "Enable Locaiton", Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        locaitonDialog = locaitonDialogBuilder.create();

        Thread thread = new Thread() {
            @Override
            public void run() {
                while (flag) {
                    Log.println(Log.INFO, "Home Activiy", "THREAD CHECK ");
                    if (!isLocationEnabled()) {
                        Log.println(Log.INFO, "Home Activiy", "THREAD CHECK LOCATION");

                        ContextCompat.getMainExecutor(HomeActivity.this).execute(() -> {
                            locaitonDialog.show();
                        });

                        while (!isLocationEnabled()) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        locaitonDialog.dismiss();
                    } else if (!isBluetoothEnabled()) {
                        Log.println(Log.INFO, "Home Activiy", "THREAD CHECK BLUETOOTH");
                        //runOnUiThread(() -> bluetoothDialog.show());
                        ContextCompat.getMainExecutor(HomeActivity.this).execute(() -> {
                            bluetoothDialog.show();
                        });
                        while (!isBluetoothEnabled()) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        bluetoothDialog.dismiss();
                    }

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.flag = false;
    }
}