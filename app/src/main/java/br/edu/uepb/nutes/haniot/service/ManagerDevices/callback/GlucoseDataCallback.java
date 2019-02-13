package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import br.edu.uepb.nutes.haniot.model.Measurement;

public interface GlucoseDataCallback {

    /**
     * On Connected to Glucose Device.
     */
    void onConnected();

    /**
     * On Disconnected to Glucose Device.
     */
    void onDisconnected();

    /**
     * On receiver data of measurement from Glucose Device.
     */
    void onMeasurementReceiver(Measurement measurementGlucose);
}
