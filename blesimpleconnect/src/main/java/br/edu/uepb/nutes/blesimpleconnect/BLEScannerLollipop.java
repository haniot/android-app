package br.edu.uepb.nutes.blesimpleconnect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.support.annotation.RequiresPermission;
import android.util.Log;

/**
 * Class of Bluetooth Scanner fo Lollipop.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.edu.uepb.br>
 * @author Arthur Stevam <arthurstevam.ac@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
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
    }

    /**
     *  Init Bluetooth Low Energy Scanner.
     */
    private void initBluetoothLeScanner(){
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public void startScan(ScanCallback callback) {
        if (callback == null) throw new IllegalArgumentException("Callback is null");
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
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public void stopScan() {
        mBluetoothLeScanner.stopScan(scanCallback);
        mScanning = false;
    }
}
