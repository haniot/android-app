package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface ScaleDataCallback extends DeviceStatusCallback {
    /**
     * Data is received by device before the end of the measurement.
     *
     * @param bodyMass     MeasurementOB value
     * @param bodyMassUnit MeasurementOB unit
     */
    void onMeasurementReceiving(double bodyMass, String bodyMassUnit);

    /**
     * Body Mass measurement receiver.
     *
     * @param device       {@link BluetoothDevice} DeviceOB that collected the measurement.
     * @param bodyMass     Body Mass MeasurementOB value.
     * @param bodyMassUnit Body Mass unit.
     * @param bodyFat      Body Fat MeasurementOB value in %.
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
