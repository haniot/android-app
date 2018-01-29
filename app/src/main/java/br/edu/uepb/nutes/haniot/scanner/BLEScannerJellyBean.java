package br.edu.uepb.nutes.haniot.scanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.util.List;

/**
 * BLE Scanner implementation JellyBean.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BLEScannerJellyBean extends BLEScanner implements BluetoothAdapter.LeScanCallback {

    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback;

    public BLEScannerJellyBean() {
        super();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    void startScanInternal(long scanPeriod, List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        this.mScanCallback = callback;
        mBluetoothAdapter.startLeScan(null,BLEScannerJellyBean.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BLEScannerJellyBean.this.stopScan(mScanCallback);
            }
        }, scanPeriod);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void stopScan(ScanCallback callback) {
        if (mBluetoothAdapter == null) return;

        mBluetoothAdapter.stopLeScan(this);
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        Log.i("BLEScannerJellyBean", "onLeScan()" + device.getName());
        mScanCallback.onScanResult(1, device);
    }
}
