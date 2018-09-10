package br.edu.uepb.nutes.blesimpleconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;

import java.util.ArrayList;
import java.util.List;

/**
 * Class of Bluetooth Scanner.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.edu.uepb.br>
 * @author Arthur Stevam <arthurstevam.ac@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2018
 */
public class BLEScanner {
    private final String TAG = "BLEScanner";

    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private boolean mScanning;
    private ScanCallback scanCallback;
    private int scanPeriod;
    private List<ScanFilter> scanFilters;
    private ScanSettings scanSettings;

    /**
     * Constructor.
     */
    private BLEScanner(Builder builder) {
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
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    public void startScan(ScanCallback callback) {
        if (callback == null) throw new IllegalArgumentException("Callback está nullo!");
        if (isScanStarted()) return;

        this.scanCallback = callback;
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
     * Reset settings to default.
     */
    public void resetSettings() {
        this.scanPeriod = 1000;
        this.scanFilters = null;
        this.scanSettings = null;
    }

    /**
     * Stop scan.
     */
    public void stopScan() {
        mBluetoothLeScanner.stopScan(scanCallback);
        mScanning = false;
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

            public BLEScanner build() {
                return new BLEScanner(this);
            }
        }
}
