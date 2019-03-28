package br.edu.uepb.nutes.haniot.service.ManagerDevices.callback;

import br.edu.uepb.nutes.haniot.data.model.Measurement;

public interface ScaleDataCallback {
    
    /**
     * On Connected to Scale Device.
     */
    void onConnected();

    /**
     * On Disconnected to Scale Device.
     */
    void onDisconnected();

    /**
     * On receiver data of measurement from Scale Device.
     */
    void onMeasurementReceived(Measurement measurementScale);

    void onMeasurementReceiving(String bodyMassMeasurement, long timeStamp, String bodyMassUnit);


}
