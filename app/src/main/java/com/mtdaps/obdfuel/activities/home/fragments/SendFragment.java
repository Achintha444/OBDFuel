package com.mtdaps.obdfuel.activities.home.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;

import org.jetbrains.annotations.NotNull;

/**
 * Interface, which enables to send OBD data to database
 */
public class SendFragment extends Fragment implements ActivityInterface {

    private ViewGroup container;
    private TextInputLayout vehicleName;
    private TextInputEditText vehicleNameEditText;
    private Button sendObdDataButton, cancelButton;
    private TextView sendText;
    private ProgressBar progressBar;
    private ConstraintLayout sendFragmentLayout;

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
            if (isInternetAvailable()) {
                if (vehicleNameEditText.getText().toString().trim().equals("")) {
                    vehicleName.setError(getResources().getString(R.string.vehicle_name_error));
                } else {
                    switchVisibilityOnSendingObdData();
                }
            } else {
                Toast.makeText(getContext(), "Internet is not Connected", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void cancelButtonOnClickListner() {
        cancelButton.setOnClickListener(view -> {
            // TODO: cancel the process
            switchVisibilityOnSendingObdData();
            Toast.makeText(getContext(), "Sending OBD Data Canceled", Toast.LENGTH_LONG).show();

        });
    }

    public boolean isInternetAvailable() {
        return connectivityManager.getActiveNetworkInfo() != null;
    }
}