package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

public interface BloodGlucoseDataCallback extends DeviceStatusCallback {
    /**
     * Blood Glucose measurement receiver.
     *
     * @param device    {@link BluetoothDevice} Device that collected the measurement.
     * @param glucose   Measurement value.
     * @param meal      Meal value.
     * @param timestamp Datetime of collection.
     */
    void onMeasurementReceived(
            @NonNull final BluetoothDevice device,
            final int glucose,
            final String meal,
            final String timestamp
    );
}
