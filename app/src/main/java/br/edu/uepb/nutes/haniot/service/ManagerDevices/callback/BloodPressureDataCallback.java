package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import br.edu.uepb.nutes.haniot.model.Measurement;

public interface BloodPressureDataCallback {

    /**
     * On Connected to Blood Pressure Device.
     */
    void onConnected();

    /**
     * On Disconnected to Blood Pressure Device.
     */
    void onDisconnected();

    /**
     * On receiver data of measurement from Blood Pressure Device.
     */
    void onMeasurementReceived(Measurement measurementBloodPressure);
}
