package br.edu.uepb.nutes.blesimpleconnect;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresPermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of Bluetooth Scanner.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.edu.uepb.br>
 * @author Arthur Stevam <arthurstevam.ac@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public abstract class BLEScanner {
    protected final String TAG = "BLEScanner";

    protected BluetoothAdapter mBluetoothAdapter;
    protected Handler mHandler;
    protected boolean mScanning;
    protected ScanCallback scanCallback;
    protected int scanPeriod;
    protected List<ScanFilter> scanFilters;
    protected ScanSettings scanSettings;

    /**
     * Constructor.
     */
    protected BLEScanner(Builder builder) {
        this.scanPeriod = builder.scanPeriod;
        this.scanFilters = builder.scanFilters;
        this.scanSettings = builder.scanSettings;
        initResources();
    }

    /**
     * Init resources.
     */
    private void initResources() {
        this.mHandler = new Handler();
        this.mScanning = false;
        initBluetoothLeScanner();
    }

    /*
     * Get scanner of Low Energy Bluetooth.
     */
    private void initBluetoothLeScanner() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    public abstract void startScan(ScanCallback callback);

    /**
     * Stop scan.
     */
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    abstract void stopScan();

    /**
     * Reset settings to default.
     */
    public void resetSettings() {
        this.scanPeriod = 1000;
        this.scanFilters = null;
        this.scanSettings = null;
    }

    /**
     * Return status scan.
     *
     * @return mScanning
     */
    public boolean isScanStarted() {
        return mScanning;
    }

    public static class Builder {
        private int scanPeriod;
        private List<ScanFilter> scanFilters;
        private ScanSettings scanSettings;

        public Builder() {
            this.scanPeriod = 1000;
            this.scanFilters = new ArrayList<>();
            this.scanSettings = new ScanSettings.Builder().build();
        }

        /**
         * Add filter adress.
         *
         * @param adress
         * @return instance
         */
        public Builder addFilterAdress(String adress) {
            scanFilters.add(new ScanFilter.Builder()
                    .setDeviceAddress(adress)
                    .build());
            return this;
        }

        /**
         * Add period of scan.
         *
         * @param period
         * @return instance
         */
        public Builder addScanPeriod(int period) {
            scanPeriod = period;
            return this;
        }

        /**
         * Add filter service uuid.
         *
         * @param services
         * @return instance
         */
        public Builder addFilterServiceUuid(String... services) {
            for (String service : services) {
                scanFilters.add(new ScanFilter.Builder()
                        .setServiceUuid(ParcelUuid.fromString(service))
                        .build());
            }
            return this;
        }

        /**
         * Add filter device name.
         *
         * @param name
         * @return instance
         */
        public Builder addFilterName(String name) {
            scanFilters.add(new ScanFilter.Builder()
                    .setDeviceName(name)
                    .build());
            return this;
        }

        /**
         * Add settings scan.
         *
         * @param scanSettings
         * @return instance
         */
        public Builder addSettingsScan(ScanSettings scanSettings) {
            this.scanSettings = scanSettings;
            return this;
        }

        /**
         * Build instance of BLEScanner
         *
         * @return BLEScanner
         * @throws IllegalArgumentException If {@code settings} or {@code callback} is null.
         */
        public BLEScanner build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return new BLEScannerLollipop(this);
            throw new IllegalArgumentException("Not implemented");
        }
    }
}
