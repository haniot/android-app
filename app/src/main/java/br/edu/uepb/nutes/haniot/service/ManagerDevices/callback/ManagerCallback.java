package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import no.nordicsemi.android.ble.BleManagerCallbacks;
import no.nordicsemi.android.ble.data.Data;

public interface ManagerCallback extends BleManagerCallbacks {
    void measurementReceiver(@NonNull final BluetoothDevice device, @NonNull final Data data);
}
