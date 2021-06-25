package com.mtdaps.obdfuel.activities.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.util.OBDDeviceConnectThread;

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
        holder.setDevice(bluetoothDevices.get(position));
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
        private BluetoothDevice device;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            setup();
            itemOnClickListner(itemView);
        }

        @Override
        public void setup() {
            parent = itemView.findViewById(R.id.parent);
            deviceName = itemView.findViewById(R.id.deivceName);
            connect = itemView.findViewById(R.id.connect);
        }

        private void itemOnClickListner(View item) {
            item.setOnClickListener(view -> {
                OBDDeviceConnectThread obdDeviceConnectThread = new OBDDeviceConnectThread(device);
                try {
                    obdDeviceConnectThread.run();
                } catch (Exception e) {
                    Toast.makeText(item.getContext(), "Connection Failed, Try Again.", Toast.LENGTH_LONG).show();
                }
            });
        }

        public void setDevice(BluetoothDevice device) {
            this.device = device;
        }
    }
}
