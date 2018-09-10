package br.edu.uepb.nutes.blescanner;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

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

    private static Context mContext;
    public static BLEScanner instance;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;

    private boolean mScanning;
    //private long scanPeriod = 100000;
    //private List<ScanFilter> scanFilters;
    private List<BluetoothDevice> listBluetoothDevice;
    //private CallbackScan callbackScan;
    //private ScanSettings scanSettings;
    static private Filter mFilter;

    private ScanCallback scanCallback;

    /**
     * Constructor.
     */
    private BLEScanner() {
    }

    /**
     * Get instance.
     *
     * @param context
     * @return instance
     */
    public static synchronized BLEScanner getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new BLEScanner();
            instance.initResources();
        }
        return instance;
    }

    /**
     * Init resources.
     */
    private void initResources() {
        this.mHandler = new Handler();
        //this.scanFilters = new ArrayList<>();
        this.listBluetoothDevice = new ArrayList<>();
        this.mScanning = false;
        initBluetoothLeScanner();
    }

    /*
     * Get scanner of Low Energy Bluetooth.
     */
    private void initBluetoothLeScanner() {
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    public void startScan(ScanCallback callback) {
        if (callback != null) {
            if (isScanStarted()) {
                Log.i(TAG, "Scanning");

                Toast.makeText(mContext, "Wait for the scan to finish", Toast.LENGTH_SHORT);
                return;
            }

            this.scanCallback = callback;
            Log.i(TAG, "Start scanner");
            // Parar de escanear depois de um tempo pré-definido
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopScan();
                    //callbackScan.onListResult(listBluetoothDevice);
                    Toast.makeText(mContext,
                            "Scan end!",
                            Toast.LENGTH_LONG).show();
                    Log.i(TAG, "End of scan");
                    mScanning = false;
                }
            }, mFilter.scanPeriod);

            mBluetoothLeScanner.startScan(mFilter.scanFilters, mFilter.scanSettings, scanCallback);
            mScanning = true;
        }
    }

    /**
     * Reset settings to default.
     */
    public void resetSettings() {
        //scanFilters.clear();
        //scanSettings = null;
        //scanPeriod = 100000;]
        this.mFilter = null;
    }

    /**
     * Stop scan.
     */
    public void stopScan() {
        mBluetoothLeScanner.stopScan(scanCallback);
        mScanning = false;
        Log.i(TAG, "End of scan");
    }

    /**
     * Return status scan.
     *
     * @return mScanning
     */
    public boolean isScanStarted() {
        return mScanning;
    }

//    /**
//     * Scan Callback.
//     */
//    private ScanCallback scanCallback = new ScanCallback() {
//        /*
//         * Device from result scan.
//         */
//        @Override
//        public void onScanResult(int callbackType, ScanResult result) {
//            super.onScanResult(callbackType, result);
//            addBluetoothDevice(result.getDevice());
//        }
//
//        /*
//         * Devices from result scan.
//         */
//        @Override
//        public void onBatchScanResults(List<ScanResult> results) {
//
//            super.onBatchScanResults(results);
//            for (ScanResult result : results) {
//                Log.i(TAG, results.toString());
//            }
//        }
//
//        /*
//         * Fail scan.
//         */
//
//        @Override
//        public void onScanFailed(int errorCode) {
//            super.onScanFailed(errorCode);
//            callbackScan.onError(errorCode);
//        }
//
//        /*
//         * Save devices from scan result.
//         */
//        private void addBluetoothDevice(BluetoothDevice device) {
//            if (!listBluetoothDevice.contains(device)) {
//                Log.i(TAG, "Result of scan");
//                Log.i(TAG, device.toString());
//                callbackScan.onResult(device);
//                listBluetoothDevice.add(device);
//            }
//        }
//    };

    public void setFilters(Filter filters) {
        this.mFilter = filters;
    }

    public static class Filter {
        private Context context;
        private int scanPeriod = 1000;
        private List<ScanFilter> scanFilters = new ArrayList<>();
        private ScanSettings scanSettings = new ScanSettings.Builder().build();

        public Filter(Context context){
            this.context = context;
        }

        /**
         * Add filter adress.
         *
         * @param adress
         * @return instance
         */
        public Filter addFilterAdress(String adress) {
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
        public Filter addScanPeriod(int period) {
            scanPeriod = period;
            return this;
        }

        /**
         * Add filter service uuid.
         *
         * @param services
         * @return instance
         */
        public Filter addFilterServiceUuid(String... services) {
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
        public Filter addFilterName(String name) {
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
        public Filter addSettingsScan(ScanSettings scanSettings) {
            this.scanSettings = scanSettings;
            return this;
        }

        public BLEScanner build() {
            BLEScanner bleScanner = getInstance(context);
            bleScanner.setFilters(this);
            return bleScanner;
        }
    }
}
