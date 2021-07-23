package com.mtdaps.obdfuel.activities.home.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.activities.home.model.OBDData;
import com.mtdaps.obdfuel.activities.home.util.DatabaseHandler;
import com.mtdaps.obdfuel.util.ActivityInterface;

import org.jetbrains.annotations.NotNull;

/**
 * Interface, which enables to send OBD data to database
 */
public class SendFragment extends Fragment implements ActivityInterface {

    /**
     * Location updates will be saved to the database
     */
    private final LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            Log.println(Log.INFO,"SendFragemnet","lattitude : "+Double.toString(mLastLocation.getLatitude())+"longtitude : "+Double.toString(mLastLocation.getLongitude()));
            sentObdData(mLastLocation);
        }
    };
    private ViewGroup container;
    private TextInputLayout vehicleName;
    private TextInputEditText vehicleNameEditText;
    private Button sendObdDataButton, cancelButton;
    private TextView sendText;
    private ProgressBar progressBar;
    private ConstraintLayout sendFragmentLayout;
    private DatabaseHandler databaseHandler;
    private FusedLocationProviderClient mFusedLocationClient;
    private ConnectivityManager connectivityManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.container = container;
        this.databaseHandler = DatabaseHandler.getDefaultDatabaseHandler(getContext());
        this.mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void setup() {
        vehicleName = getView().findViewById(R.id.vehicleName);
        vehicleNameEditText = getView().findViewById(R.id.vehicleNameEditText);
        sendObdDataButton = getView().findViewById(R.id.sendObdDataButton);
        cancelButton = getView().findViewById(R.id.cancelButton);
        sendText = getView().findViewById(R.id.sendText);
        progressBar = getView().findViewById(R.id.progressBar);
        sendFragmentLayout = getView().findViewById(R.id.sendFragmentLayout);

        addTextChangedListener(vehicleNameEditText, vehicleName);

        sendObdDataButtonOnClickListner();
        cancelButtonOnClickListner();
    }

    /**
     * validator of vehicle name text field
     *
     * @param e
     * @param t
     */
    private void addTextChangedListener(TextInputEditText e, final TextInputLayout t) {
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 6) {
                    t.setError(getResources().getString(R.string.vehicle_name_error));
                } else {
                    OBDData.setVehicleName(s.toString());
                    t.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void switchVisibilityOnSendingObdData() {
        int visibility = sendText.getVisibility();
        if (visibility == View.GONE) {
            sendText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.VISIBLE);
            sendObdDataButton.setVisibility(View.GONE);
            vehicleName.setEnabled(false);
        } else {
            sendText.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
            sendObdDataButton.setVisibility(View.VISIBLE);
            vehicleName.setEnabled(true);
        }


    }

    private void sendObdDataButtonOnClickListner() {
        sendObdDataButton.setOnClickListener(view -> {
            if (isInternetAvailable() && isLocationEnabled()) {
                if (vehicleNameEditText.getText().toString().trim().equals("")) {
                    vehicleName.setError(getResources().getString(R.string.vehicle_name_error));
                } else {
                    switchVisibilityOnSendingObdData();
                    saveLocationAndObdToDatabase();
                }
            }
            else {
                Toast.makeText(getContext(), "Internet is not Connected", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cancelButtonOnClickListner() {
        cancelButton.setOnClickListener(view -> {
            // cancel the process
            stopLocationUpdates();
            switchVisibilityOnSendingObdData();
            Toast.makeText(getContext(), "Sending OBD Data Canceled", Toast.LENGTH_LONG).show();

        });
    }

    private void sentObdData(Location location) {
        DatabaseHandler.saveObdData(new OBDData("check").getDocument(location));
        databaseHandler.startReplication();
    }

    /**
     * call the method to get location to save on the DB
     */
    private void saveLocationAndObdToDatabase() {
        // check if permissions are given
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            Toast.makeText(getContext(), "Location Recived Failed. Try Again.", Toast.LENGTH_LONG).show();
                            switchVisibilityOnSendingObdData();
                            Toast.makeText(getContext(), "Sending OBD Data Canceled", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
            }
        } else {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 2);
        }
    }

    /**
     * Request the new location and put it to the locaiton callback
     */
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(2000)
                .setFastestInterval(1500);


        // setting LocationRequest
        // on FusedLocationClient
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 2);
            return;
        }
        if (isLocationEnabled()) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    private void stopLocationUpdates() {
        Log.println(Log.INFO,"SendFragemnet","Location update stopped");
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isInternetAvailable() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}