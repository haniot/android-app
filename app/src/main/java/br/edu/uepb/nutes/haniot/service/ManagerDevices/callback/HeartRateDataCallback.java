package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import br.edu.uepb.nutes.haniot.model.Measurement;

public interface HeartRateDataCallback {

    /**
     * On Connected to Heart Rate Device.
     */
    void onConnected();

    /**
     * On Disconnected to Heart Rate Device.
     */
    void onDisconnected();

    /**
     * On receiver data of measurement from Heart Rate Device.
     */
    void onMeasurementReceiver(Measurement measurementHeartRate);

}
