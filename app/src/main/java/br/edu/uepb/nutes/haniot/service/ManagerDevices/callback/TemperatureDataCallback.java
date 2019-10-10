package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface TemperatureDataCallback extends DeviceStatusCallback {
    /**
     * Temperature measurement receiver.
     *
     * @param device    {@link BluetoothDevice} DeviceOB that collected the measurement.
     * @param temp      MeasurementOB value.
     * @param unit      MeasurementOB unit.
     * @param timestamp Datetime of collection.
     */
    void onMeasurementReceived(
            @NonNull final BluetoothDevice device,
            final double temp,
            final String unit,
            final String timestamp
    );
}
