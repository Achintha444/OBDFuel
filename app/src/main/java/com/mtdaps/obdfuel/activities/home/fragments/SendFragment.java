package com.mtdaps.obdfuel.activities.home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;

import org.jetbrains.annotations.NotNull;

import static androidx.core.content.ContextCompat.getSystemService;

/**
 * Interface, which enables to send OBD data to database
 */
public class SendFragment extends Fragment implements ActivityInterface {

    private TextInputLayout vehicleName;
    private TextInputEditText vehicleNameEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setup();
    }

    @Override
    public void setup() {
        vehicleName = getView().findViewById(R.id.vehicleName);
        vehicleNameEditText = getView().findViewById(R.id.vehicleNameEditText);

        addTextChangedListener(vehicleNameEditText, vehicleName);
    }

    /**
     * validator of vehicle name text field
     * @param e
     * @param t
     */
    private static void addTextChangedListener(TextInputEditText e, final TextInputLayout t) {
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 6) {
                    t.setError("Vehicle Name should be at least 6 characters");
                } else {

                    t.setError("");

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}