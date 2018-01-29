package br.edu.uepb.nutes.haniot.scanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * BLE Scanner implementation Lollipop.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class BLEScannerLollipop extends BLEScanner {
    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback;

    BLEScannerLollipop() {
        super();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    void startScanInternal(long scanPeriod, List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        Log.w("BLEScannerLollipop", "startScan()");
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) throw new NullPointerException("BT le scanner not available");

        this.mScanCallback = callback;
        if (filters == null && settings == null) scanner.startScan(bleScanCallback);
        else scanner.startScan(filters, settings, bleScanCallback);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BLEScannerLollipop.this.stopScan(mScanCallback);
            }
        }, scanPeriod);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void stopScan(ScanCallback callback) {
        final BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (scanner == null) return;

        scanner.stopScan(bleScanCallback);
    }

    final android.bluetooth.le.ScanCallback bleScanCallback = new android.bluetooth.le.ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mScanCallback.onScanResult(callbackType, result.getDevice());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            List<BluetoothDevice> list = new ArrayList<>();
            for (ScanResult re : results)
                list.add(re.getDevice());

            mScanCallback.onBatchScanResults(list);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            mScanCallback.onScanFailed(errorCode);
        }
    };
}
