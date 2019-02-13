package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;

import br.edu.uepb.nutes.haniot.model.Measurement;

public interface TemperatureDataCallback {
    /**
     * On Connected to Temperature Device.
     */
    void onConnected(BluetoothDevice device);

    /**
     * On Disconnected to Temperature Device.
     */
    void onDisconnected(BluetoothDevice device);

    /**
     * On receiver data of measurement from Temperature Device.
     */
    void onMeasurementReceiver(Measurement measurementTemperature);
}
