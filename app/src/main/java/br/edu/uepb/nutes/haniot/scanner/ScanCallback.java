package br.edu.uepb.nutes.haniot.scanner;

import android.bluetooth.BluetoothDevice;

import java.util.List;

/**
 * Callback for {@link BLEScanner}
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public interface ScanCallback {
    /**
     * Callback when a BLE advertisement has been found.
     *
     * @param callbackType Determines how this callback was triggered. Could be one of
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_ALL_MATCHES},
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_FIRST_MATCH} or
     *                     {@link android.bluetooth.le.ScanSettings#CALLBACK_TYPE_MATCH_LOST}
     * @param device       {@link BluetoothDevice}.
     */
    void onScanResult(int callbackType, BluetoothDevice device);

    /**
     * Callback when batch results are delivered.
     *
     * @param devices List of scan results that are previously scanned.
     */
    void onBatchScanResults(List<BluetoothDevice> devices);

    /**
     * Callback when scan could not be started.
     *
     * @param errorCode Error code for scan failure.
     */
    void onScanFailed(int errorCode);
}
