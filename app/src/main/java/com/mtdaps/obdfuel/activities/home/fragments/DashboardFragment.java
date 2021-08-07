package com.mtdaps.obdfuel.activities.home.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;

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
    }


    @Override
    public void setup() {
        speedTextValue = getView().findViewById(R.id.speed_text_value);
        rpmTextValue = getView().findViewById(R.id.rpm_text_value);
        airFuelRatioTextValue = getView().findViewById(R.id.air_fuel_ratio_text_value);
        fuelConsumptionTextValue = getView().findViewById(R.id.fuel_consumption_text_value);
        batteryVoltageTextValue = getView().findViewById(R.id.battery_voltage_text_value);
        mafTextValue = getView().findViewById(R.id.maf_text_value);
    }
}