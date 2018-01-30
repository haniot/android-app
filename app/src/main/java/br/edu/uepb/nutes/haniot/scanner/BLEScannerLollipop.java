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
    private BluetoothLeScanner mScanner;

    BLEScannerLollipop() {
        super();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    void startScanInternal(long scanPeriod, List<ScannerFilter> filters, ScanSettings settings, ScanCallback callback) {
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mScanner == null) throw new IllegalArgumentException("BT le scanner not available");

        this.mScanCallback = callback;
        if (filters == null && settings == null) mScanner.startScan(bleScanCallback);
        else mScanner.startScan(uuidToScanFilter(filters), settings, bleScanCallback);

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
        mScanner = mBluetoothAdapter.getBluetoothLeScanner();
        if (mScanner == null) return;

        mScanner.stopScan(bleScanCallback);
        mScanCallback = null;
        mScanner = null;
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

    /**
     * Convert List<UUID> in List<ScannerFilter>.
     *
     * @param filtersServices List<ScannerFilter>
     * @return List<ScanFilter>
     */
    private List<ScanFilter> uuidToScanFilter(List<ScannerFilter> filtersServices) {
        List<ScanFilter> filters = new ArrayList<>();

        for (ScannerFilter f : filtersServices) {
            ScanFilter filter = f.getScanFilter();
            if (filter != null) filters.add(filter);
        }

        return filters;
    }
}
