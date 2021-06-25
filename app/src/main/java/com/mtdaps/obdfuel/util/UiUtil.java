package com.mtdaps.obdfuel.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mtdaps.obdfuel.R;

import java.util.UUID;

// Utils used in UI
public class UiUtil {

    public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String OBD_BLUETOOTH_SOCKET = "OBD_BLUETOOTH_SOCKET";

    public static void showSnackbar(ViewGroup layout, Context context, String text) {
        showSnackbar(layout, context, text, R.color.purple_500, Snackbar.LENGTH_SHORT);
    }

    public static void showErrorSnackbar(ViewGroup layout, Context context, String text) {
        showSnackbar(layout, context, text, R.color.error, Snackbar.LENGTH_INDEFINITE);
    }

    // default snackbar
    public static void showSnackbar(ViewGroup layout, Context context, String text, int color, int duration) {
        Snackbar snackbar = Snackbar.make(layout, text, duration);
        View snackbarView = snackbar.getView();
        TextView textview = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        Typeface font = ResourcesCompat.getFont(context, R.font.nunito_bold);
        textview.setTypeface(font);
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, color));
        snackbar.show();
    }
}
