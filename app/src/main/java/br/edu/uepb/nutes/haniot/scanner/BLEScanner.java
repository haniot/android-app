package br.edu.uepb.nutes.haniot.scanner;

import android.Manifest;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;

import java.util.List;

/**
 * Class provides methods to perform scan related operations for Bluetooth LE devices.
 * An application can scan for a particular type of Bluetooth LE devices using {@link ScanFilter}.
 * <p>
 * Use {@link BLEScanner#getScanner()} to get an instance of the scanner.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public abstract class BLEScanner {
    private final String TAG = "BLEScanner";

    private static BLEScanner mInstance;

    /**
     * Returns the BLEScanner object.
     *
     * @return BLEScanner implementation
     */
    public static synchronized BLEScanner getScanner() {
        if (mInstance != null)
            return mInstance;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return mInstance = new BLEScannerMarshmallow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return mInstance = new BLEScannerLollipop();
        return mInstance = new BLEScannerJellyBean();
    }

    /**
     * Start Bluetooth LE scan with default parameters and no filters.
     * The scan results will be delivered through {@code callback}.
     *
     * @param scanPeriod To the scanner after the set period.
     * @param callback   {@link ScanCallback} Callback used to deliver scan results.
     * @throws NullPointerException If {@code callback} is null.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public void startScan(final long scanPeriod, final ScanCallback callback) {
        if (callback == null)
            throw new NullPointerException("Callback is null");


        startScanInternal(scanPeriod, null, null, callback);
    }

    /**
     * Start Bluetooth LE scan. The scan results will be delivered through {@code callback}.
     *
     * @param scanPeriod To the scanner after the set period
     * @param filters    {@link ScanFilter} for finding exact BLE devices.
     * @param settings   {@link ScanSettings} Settings for the scan.
     * @param callback   {@link ScanCallback} Callback used to deliver scan results.
     * @throws NullPointerException If {@code settings} or {@code callback} is null.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public void startScan(final long scanPeriod, final List<ScanFilter> filters, final ScanSettings settings, final ScanCallback callback) {
        if (settings == null || callback == null)
            throw new NullPointerException("settings or callback is null");

        startScanInternal(scanPeriod, filters, settings, callback);
    }

    /**
     * Start BLE Scanner according to the Android version.
     *
     * @param scanPeriod To the scanner after the set period
     * @param filters    {@link ScanFilter} for finding exact BLE devices.
     * @param settings   {@link ScanSettings} Settings for the scan.
     * @param callback   {@link ScanCallback} Callback used to deliver scan results.
     * @throws NullPointerException If {@code settings} or {@code callback} is null.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    abstract void startScanInternal(final long scanPeriod, final List<ScanFilter> filters, final ScanSettings settings, final ScanCallback callback);

    /**
     * Stop the BLE scanner.
     *
     * @param callback {@link ScanCallback} Callback used to deliver scan results.
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_ADMIN)
    protected abstract void stopScan(final ScanCallback callback);
}
