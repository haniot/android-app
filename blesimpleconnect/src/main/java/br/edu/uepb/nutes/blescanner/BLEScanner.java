package br.edu.uepb.nutes.blescanner;
import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.support.v4.app.ActivityCompat.requestPermissions;

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
    private final int REQUEST_BLUETOOTH = 1;
    private final int REQUEST_LOCATION = 2;

    private static Context mContext;
    public static BLEScanner instance;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;

    private boolean mScanning;
    private long scanPeriod = 100000;
    private List<ScanFilter> scanFilters;
    private List<BluetoothDevice> listBluetoothDevice;
    private CallbackScan callbackScan;
    private ScanSettings scanSettings;

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
        this.scanFilters = new ArrayList<>();
        this.listBluetoothDevice = new ArrayList<>();
        this.mScanning = false;
        initBluetoothLeScanner();
    }

    /*
    * Get scanner of Low Energy Bluetooth.
    */
    private void initBluetoothLeScanner() {
        // Get BluetoothAdapter and BluetoothLeScanner.
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        //Verify bluetooth
        verifyBluetooth();
        verifyLocation();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
    }

    /**
     * Start scan.
     *
     * @param callback
     */
    public void startScan(CallbackScan callback) {
        if (isScanStarted()) {
            Log.i(TAG, "Scanning");

            Toast.makeText(mContext, "Wait for the scan to finish", Toast.LENGTH_SHORT);
            return;
        }

        this.callbackScan = callback;
        Log.i(TAG, "Start scanner");
        // Parar de escanear depois de um tempo pré-definido
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScan();
                callbackScan.onListResult(listBluetoothDevice);
                callbackScan = null;
                Toast.makeText(mContext,
                        "Scan end!",
                        Toast.LENGTH_LONG).show();
                Log.i(TAG, "End of scan");
                mScanning = false;
                resetSettings();

            }
        }, scanPeriod);

        if (scanSettings == null) scanSettings = new ScanSettings.Builder().build();
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, scanCallback);
        mScanning = true;
    }

    /**
     * Reset settings to default.
     */
    public void resetSettings(){
        scanFilters.clear();
        scanSettings = null;
        scanPeriod = 100000;
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

    /**
     * Scan Callback.
     */
    private ScanCallback scanCallback = new ScanCallback() {
        /*
        * Device from result scan.
        */
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            addBluetoothDevice(result.getDevice());

        }

        /*
        * Devices from result scan.
        */
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (ScanResult result : results) {
                Log.i(TAG, results.toString());
            }
        }

        /*
        * Fail scan.
        */

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            callbackScan.onError(errorCode);
        }

        /*
        * Save devices from scan result.
        */
        private void addBluetoothDevice(BluetoothDevice device) {
            if (!listBluetoothDevice.contains(device)) {
                Log.i(TAG, "Result of scan");
                Log.i(TAG, device.toString());
                callbackScan.onResult(device);
                listBluetoothDevice.add(device);
            }
        }
    };

    /**
     * Add filter adress.
     *
     * @param ADRESS
     * @return instance
     */
    public BLEScanner addFilterAdress(String ADRESS) {
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceAddress(ADRESS)
                .build();

        scanFilters.add(scanFilter);

        return instance;
    }

    /**
     * Add period of scan.
     *
     * @param period
     * @return instance
     */
    public BLEScanner addScanPeriod(int period) {
        this.scanPeriod = period;
        return instance;
    }

    /**
     * Add filter service uuid.
     *
     * @param services
     * @return instance
     */
    public BLEScanner addFilterServiceUuid(String... services) {
        for (String service : services) {
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setServiceUuid(ParcelUuid.fromString(service))
                    .build();
            scanFilters.add(scanFilter);
        }
        return instance;
    }

    /**
     * Add filter device name.
     *
     * @param Name
     * @return instance
     */
    public BLEScanner addFilterName(String Name) {
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceName(Name)
                .build();
        scanFilters.add(scanFilter);

        return instance;
    }

    /**
     * Add settings scan.
     *
     * @param Name
     * @return instance
     */
    public BLEScanner addSettingsScan(String Name) {
        ScanFilter scanFilter = new ScanFilter.Builder()
                .setDeviceName(Name)
                .build();
        scanFilters.add(scanFilter);

        return instance;
    }


    //TODO pensar onde e quando chamar os verifyBluetooth e verifyLocation
    //Verify bluetooth
    public void verifyBluetooth() {
        if (mBluetoothAdapter == null) {
            Log.i(TAG, "The device does not have Bluetooth!");
        } else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Log.i(TAG, "Permission bluetooth");
            ((Activity) mContext).startActivityForResult(enableBtIntent, REQUEST_BLUETOOTH);
        }
    }

    //Verify location
    public void verifyLocation() {

        //TODO Ajeitar melhor , verificar se para api menor que 23 precisa pedir a permissão ou se ela é dada na instalação
        //TODO Ver como fazer para o app reconhecer se a permissão foi dada ou não no momento que for aceita ou negada E A IDENTAÇÃO

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//API > 23
            requestPermissions(((Activity) mContext), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(((Activity) mContext), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }
        } else { // API < 23
            LocationManager manager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "Localização desativada");
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                ((Activity) mContext).startActivityForResult(intent, REQUEST_LOCATION);

            } else {
                Log.i(TAG, "Localização ativada");
            }
        }

    }

    private ActivityCompat.OnRequestPermissionsResultCallback permissionsResultCallback = new ActivityCompat.OnRequestPermissionsResultCallback() {
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == REQUEST_LOCATION) {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];

                    if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) || permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                        if (grantResult == PackageManager.PERMISSION_GRANTED) {

                        } else {
                            //requestPermissions(mActivity, new String[]{permission}, REQUEST_LOCATION);
                            verifyLocation();
                        }
                    }
                }
            }
        }

    };
}
