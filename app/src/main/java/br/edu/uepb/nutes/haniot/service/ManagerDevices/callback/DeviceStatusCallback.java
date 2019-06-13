package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface DeviceStatusCallback {
    void onConnected(@NonNull final BluetoothDevice device);

    void onDisconnected(@NonNull final BluetoothDevice device);

}
