package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface ScaleDataCallback extends DeviceStatusCallback {
    /**
     * Data is received by device before the end of the measurement.
     *
     * @param bodyMass     Measurement value
     * @param bodyMassUnit Measurement unit
     */
    void onMeasurementReceiving(double bodyMass, String bodyMassUnit);

    /**
     * Body Mass measurement receiver.
     *
     * @param device       {@link BluetoothDevice} Device that collected the measurement.
     * @param bodyMass     Body Mass Measurement value.
     * @param bodyMassUnit Body Mass unit.
     * @param bodyFat      Body Fat Measurement value in %.
     * @param timestamp    Datetime of collection.
     */
    void onMeasurementReceived(
            @NonNull final BluetoothDevice device,
            final double bodyMass,
            final String bodyMassUnit,
            final double bodyFat,
            final String timestamp
    );
}
