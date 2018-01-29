package br.edu.uepb.nutes.haniot.scanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.util.Log;

import java.util.List;

/**
 * BLE Scanner implementation Marshmallow.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@TargetApi(Build.VERSION_CODES.M)
public class BLEScannerMarshmallow extends BLEScannerLollipop {
    BLEScannerMarshmallow() {
        super();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    void startScanInternal(long scanPeriod, List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {
        super.startScanInternal(scanPeriod, filters, settings, callback);
        Log.w("BLEScannerMarshmallow", "startScanInternal()");
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void stopScan(ScanCallback callback) {
        super.stopScan(callback);
    }
}
