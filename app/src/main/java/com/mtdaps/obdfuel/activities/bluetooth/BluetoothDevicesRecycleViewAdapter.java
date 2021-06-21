package com.mtdaps.obdfuel.activities.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;

import java.util.ArrayList;

public class BluetoothDevicesRecycleViewAdapter extends RecyclerView.Adapter<BluetoothDevicesRecycleViewAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<BluetoothDevice> bluetoothDevices;

    public BluetoothDevicesRecycleViewAdapter(Context context) {
        this.context = context;
    }

    // ViewGroup is parent of every Layout
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_devices_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BluetoothDevicesRecycleViewAdapter.ViewHolder holder, final int position) {

        holder.deviceName.setText(bluetoothDevices.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.bluetoothDevices.size();
    }

    public void setBluetoothDevices(ArrayList<BluetoothDevice> bluetoothDevices) {
        this.bluetoothDevices = bluetoothDevices;

        /* This will notify that data set has been changed */
        notifyDataSetChanged();
    }

    /*
     * Hold Every Item in the Recycle View contact_list_item.xml
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements ActivityInterface {
        private ConstraintLayout parent;
        private TextView deviceName, connect;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            setup();
        }

        @Override
        public void setup() {
            parent = itemView.findViewById(R.id.parent);
            deviceName = itemView.findViewById(R.id.deivceName);
            connect = itemView.findViewById(R.id.connect);
        }
    }
}
