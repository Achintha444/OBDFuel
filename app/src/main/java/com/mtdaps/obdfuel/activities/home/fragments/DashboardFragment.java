package com.mtdaps.obdfuel.activities.home.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.OBDValues;

import org.jetbrains.annotations.NotNull;

/**
 * Fragement of the Dashbaord
 * Here we will have the Dashboard that will show the data from the OBD.
 */
public class DashboardFragment extends Fragment implements ActivityInterface {

    private TextView speedTextValue;
    private TextView rpmTextValue;
    private TextView airFuelRatioTextValue;
    private TextView fuelConsumptionTextValue;
    private TextView batteryVoltageTextValue;
    private TextView mafTextValue;

    private String speed;
    private String rpm;
    private String airFuelRatio;
    private String fuelConsumption;
    private String batteryVoltage;
    private String maf;

    private boolean flag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();

        dashboardObdValuesThread().start();
    }

    @Override
    public void setup() {
        speedTextValue = getView().findViewById(R.id.speed_text_value);
        rpmTextValue = getView().findViewById(R.id.rpm_text_value);
        airFuelRatioTextValue = getView().findViewById(R.id.air_fuel_ratio_text_value);
        fuelConsumptionTextValue = getView().findViewById(R.id.fuel_consumption_text_value);
        batteryVoltageTextValue = getView().findViewById(R.id.battery_voltage_text_value);
        mafTextValue = getView().findViewById(R.id.maf_text_value);

        flag = true;
    }

    private Thread dashboardObdValuesThread() {


        Thread thread = new Thread() {
            @Override
            public void run() {

                while (flag) {
                    speed = OBDValues.speedData();
                    rpm = OBDValues.rpmData();
                    maf = OBDValues.massAirFlowData();
                    fuelConsumption = OBDValues.fuelConsumption(speed, maf);
                    batteryVoltage = OBDValues.moduleVoltageData();
                    airFuelRatio = OBDValues.airFuelRatioData();

                    updateTextViews(speed, rpm, fuelConsumption, airFuelRatio, batteryVoltage, maf);

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        };

        return thread;
    }

    private void updateTextViews(String speed, String rpm, String fuelConsumption, String airFuelRatio, String batteryVoltage, String maf) {
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                speedTextValue.setText(speed);
                rpmTextValue.setText(rpm);
                airFuelRatioTextValue.setText(airFuelRatio);
                fuelConsumptionTextValue.setText(fuelConsumption);
                batteryVoltageTextValue.setText(batteryVoltage);
                mafTextValue.setText(maf);
            }
        };
        handler.sendEmptyMessage(1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}