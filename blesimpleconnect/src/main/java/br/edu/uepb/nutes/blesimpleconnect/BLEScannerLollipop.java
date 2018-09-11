package br.edu.uepb.nutes.blesimpleconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.util.Log;

public class BLEScannerLollipop extends BLEScanner {

    private BluetoothLeScanner mBluetoothLeScanner;
    /**
     * Constructor.
     *
     * @param builder
     */
    protected BLEScannerLollipop(Builder builder) {
        super(builder);
        initBluetoothLeScanner();
        Log.i(TAG, ">= Lollipop");
    }

    private void initBluetoothLeScanner(){
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }
    /**
     * Start scan.
     *
     * @param callback
     */
    @Override
    public void startScan(ScanCallback callback) {
        if (callback == null) throw new IllegalArgumentException("Callback está nullo!");
        if (isScanStarted()) return;

        scanCallback = callback;
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                mScanning = false;
            }
        }, scanPeriod);

        mBluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
        mScanning = true;
    }

    /**
     * Stop scan.
     */
    @Override
    public void stopScan() {
        mBluetoothLeScanner.stopScan(scanCallback);
        mScanning = false;
    }
}
