package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;

import br.edu.uepb.nutes.haniot.data.model.Measurement;

public interface TemperatureDataCallback {
    /**
     * On Connected to Temperature Device.
     */
    void onConnected();

    /**
     * On Disconnected to Temperature Device.
     */
    void onDisconnected();

    /**
     * On receiver data of measurement from Temperature Device.
     */
    void onMeasurementReceived(Measurement measurementTemperature);
}
