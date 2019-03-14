package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import org.json.JSONException;

import no.nordicsemi.android.ble.data.Data;

public interface ManagerCallback extends no.nordicsemi.android.ble.BleManagerCallbacks {

    void measurementReceiver(@NonNull final BluetoothDevice device, @NonNull final Data data);

}
