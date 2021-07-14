package com.mtdaps.obdfuel.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mtdaps.obdfuel.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import static androidx.core.content.ContextCompat.startActivity;

// Utils used in UI
public class UiUtil {

    public static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String OBD_BLUETOOTH_SOCKET = "OBD_BLUETOOTH_SOCKET";

    public static Snackbar showSnackbar(ViewGroup layout, Context context, String text) {
        return showSnackbar(layout, context, text, R.color.purple_500, Snackbar.LENGTH_LONG);
    }

    public static Snackbar showErrorSnackbar(ViewGroup layout, Context context, String text) {
        return showSnackbar(layout, context, text, R.color.error, Snackbar.LENGTH_INDEFINITE);
    }

    // default snackbar
    public static Snackbar showSnackbar(ViewGroup layout, Context context, String text, int color, int duration) {



        Snackbar snackbar = Snackbar.make(layout, text, duration);
        View snackbarView = snackbar.getView();
        TextView textview = snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, color));

        try{
            Typeface font = ResourcesCompat.getFont(context, R.font.nunito_bold);
            textview.setTypeface(font);
            return snackbar;
        } catch(Exception ex){
            return snackbar;
        }

    }

    public static Snackbar showWaitingSnackBar(ViewGroup layout, Context context, String text) {
        Snackbar snackbar = showSnackbar(layout, context, text, R.color.purple_500, Snackbar.LENGTH_INDEFINITE);
        ViewGroup contentLay = (ViewGroup) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text).getParent();
        ProgressBar item = new ProgressBar(context);
        DrawableCompat.setTint(item.getIndeterminateDrawable(), Color.parseColor("#FEFCFF"));
        item.setPadding(8, 8, 8, 8);
        contentLay.addView(item);
        return snackbar;
    }

    public static AlertDialog.Builder createWarningAlertDialogBuilder(Bundle bundle, Context context, String title, String message, String postiveMessage, String settingsLocation) {
        return new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(postiveMessage, (paramDialogInterface, paramInt) -> {
                    Intent myIntent = new Intent(settingsLocation);
                    startActivity(context, myIntent, bundle);
                })
                .setCancelable(false);
    }

    /**
     * return the current time
     * @return the current time
     */
    public static Date getCurrentDate(){
        return  Calendar.getInstance().getTime();
    }

    /**
     * return the current time in epcoh format
     * @return the current time in epcoh format
     */
    public static Long getCurrentTimeMillis(){
        return System.currentTimeMillis();
    }

    /**
     * return the current date
     * yyyy-MM-dd
     * format
     */
    public static String formatCurrentDate(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return df.format(getCurrentDate());
    }
}
