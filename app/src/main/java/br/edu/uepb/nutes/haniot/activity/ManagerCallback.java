package br.edu.uepb.nutes.haniot.activity;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import no.nordicsemi.android.ble.data.Data;

public interface ManagerCallback extends no.nordicsemi.android.ble.BleManagerCallbacks {
    // No more methods

    void measurementReceiver(@NonNull final BluetoothDevice device, @NonNull final Data data);

}
