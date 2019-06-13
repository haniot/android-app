package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface BloodPressureDataCallback extends DeviceStatusCallback {
    /**
     * Blood Pressure measurement receiver.
     *
     * @param device    {@link BluetoothDevice} Device that collected the measurement.
     * @param systolic  Systolic value.
     * @param diastolic Diastolic value.
     * @param pulse     Pulse value in bpm.
     * @param unit      Unit of systolic to diastolic pressure.
     * @param timestamp Datetime of collection.
     */
    void onMeasurementReceived(
            @NonNull final BluetoothDevice device,
            final int systolic,
            final int diastolic,
            final int pulse,
            final String unit,
            final String timestamp
    );
}
