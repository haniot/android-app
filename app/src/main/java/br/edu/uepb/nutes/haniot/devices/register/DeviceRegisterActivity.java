package br.edu.uepb.nutes.haniot.devices.register;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class DeviceRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String LOG_TAG = "DeviceRegisterActivity ";

    private final String NAME_DEVICE_THERM_DL8740 = "Ear Thermometer DL8740";
    private final String NAME_DEVICE_GLUCOMETER_PERFORMA = "Accu-Chek Performa Connect";
    private final String NAME_DEVICE_SCALE_1501 = "Scale YUNMAI Mini 1501";
    private final String NAME_DEVICE_YUNMAI = "YUNMAI-SIGNAL-M1US";
    private final String NAME_DEVICE_HEART_RATE = "Heart Rate Sensor";
    private final String NAME_DEVICE_SMARTBAND_MI2 = "Smartband MI Band 2";
    private final String SERVICE_SCALE_1501 = "00001310-0000-1000-8000-00805f9b34fb";
    private final String PIN_YUNMAI = "000000";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_LOCATION = 2;

    //    private SimpleBleScanner mScanner;
    private Device mDevice;
    private DeviceDAO mDeviceDAO;
    private AppPreferencesHelper appPreferences;
    private HaniotNetRepository haniotRepository;
    private BluetoothDevice btDevice;
    private User user;
    private BluetoothLeScannerCompat mScanner;

    @BindView(R.id.box_scanner)
    FrameLayout boxScanner;

    @BindView(R.id.box_register)
    FrameLayout boxRegister;

    @BindView(R.id.box_response)
    FrameLayout boxResponse;

    @BindView(R.id.btn_device_register_scanner)
    Button btnDeviceRegisterScanner;

    @BindView(R.id.name_device_scanner)
    TextView nameDeviceScanner;

    @BindView(R.id.btn_device_register_stop)
    Button btnDeviceRegisterStop;

    @BindView(R.id.name_device_scanner_register)
    TextView nameDeviceScannerRegister;

    @BindView(R.id.device_connection_status)
    TextView deviceConnectionStatus;

    @BindView(R.id.txt_device_successfully_registered)
    TextView deviceSuccessfullyRegistered;

    @BindView(R.id.img_device_register)
    ImageView imgDeviceRegister;

    @BindView(R.id.btn_close_register)
    ImageButton btnCloseRegister;

    @BindView(R.id.btn_close_response)
    Button btnCloseResponse;

    @BindView(R.id.pulsator)
    PulsatorLayout mPulsatorLayout;

    @BindView(R.id.progressBar_pairing)
    ProgressBar progressBarPairing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_device_register);
        ButterKnife.bind(this);

        appPreferences = AppPreferencesHelper.getInstance(this);
        haniotRepository = HaniotNetRepository.getInstance(this);
        mDeviceDAO = DeviceDAO.getInstance(this);


        btnDeviceRegisterScanner.setOnClickListener(this);
        btnDeviceRegisterStop.setOnClickListener(this);
        btnCloseRegister.setOnClickListener(this);
        btnCloseResponse.setOnClickListener(this);

        mDevice = getIntent().getParcelableExtra(DeviceManagerActivity.EXTRA_DEVICE);
        for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
            Log.w("AAA", device.getName() + " - " + device.getAddress());
        }

//        //Initialize scanner settings
//        mScanner = new SimpleBleScanner.Builder()
//                .addScanPeriod(15000) // 15s
//                .addFilterServiceUuid(getServiceUuidDevice(mDevice.getName()))
//                .build();
        mScanner = BluetoothLeScannerCompat.getScanner();
        ScanSettings settings = new ScanSettings.Builder()
                .setLegacy(false)
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(1000)
                .setUseHardwareBatchingIfSupported(true)
                .build();
        List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(getServiceUuidDevice(mDevice.getName()))).build());
        mScanner.startScan(filters, settings, callback);

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);
////        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        IntentFilter intent = new IntentFilter(
//                "android.bluetooth.device.action.PAIRING_REQUEST");
//        registerReceiver(mBroadcastReceiver, intent);

//        IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
//        registerReceiver(mPairReceiver, intent);
        user = appPreferences.getUserLogged();
        if (user == null) {
            finish();
            return;
        }

        initComponents();
    }

    public boolean createBond(BluetoothDevice btDevice) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * @param device
     */
    private void pairDevice(BluetoothDevice device) {
        try {
            Log.w("AA", "pairDevice - pairing device " + device.getName());

            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param device
     */
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//
//    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.w("AA", "mPairReceiver: " + action);
//
//            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
//                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
//                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Log.w("AA", "Bond status: " + mBluetoothDevice.getBondState());
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
//                    IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
//                    intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//                    registerReceiver(broadCastReceiver, intentFilter);
//                }
////                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
////                    Log.w("AA", "Paired");
////                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
////                    Log.w("AA", "Unpaired");
////                }
//
//            }
//        }
//    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
//
//    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action != null && action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                //case1: bonded already
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDED.");
//                    deviceAvailable(mBluetoothDevice);
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
//                        unregisterReceiver(broadCastReceiver);
//                    }
//                }
//                //case2: creating a bone
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDING. " + mBluetoothDevice.getName());
//                    deviceConnectionStatus.setText(R.string.pairing_device);
//                    progressBarPairing.setVisibility(View.VISIBLE);
//                    btnDeviceRegisterScanner.setEnabled(false);
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
//                        //Broadcasts for balance pairing yunmai
//                        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
//                        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//                        registerReceiver(broadCastReceiver, intentFilter);
//                    }
//                }
//                // case3: breaking a bond
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_NONE.");
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI) && broadCastReceiver != null) {
//                        Log.i("AAA", mBluetoothDevice.getName() + " - " +broadCastReceiver.getResultCode());
//                        unregisterReceiver(broadCastReceiver);
//                    }
//                    deviceConnectionStatus.setText(R.string.failed_pairing_device);
//                    btnDeviceRegisterScanner.setEnabled(true);
//                    btnDeviceRegisterScanner.setText(R.string.start_scanner_try);
//                    progressBarPairing.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//    };
//
//    /**
//     * method for balance pairing yunmai
//     */
//    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            Log.w("AAA", "broadCastReceiver " + action);
//            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                bluetoothDevice.setPin(PIN_YUNMAI.getBytes());
//                //does not display the pairing request for the user
//                abortBroadcast();
//                Log.e(LOG_TAG, "Auto-entering pin: " + PIN_YUNMAI);
//            }
//        }
//    };

    ScanCallback callback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @androidx.annotation.NonNull no.nordicsemi.android.support.v18.scanner.ScanResult result) {
            super.onScanResult(callbackType, result);
            mScanner.stopScan(callback);
            btDevice = result.getDevice();
            Log.w("AAAA", result.toString());
            Log.w("AAAA", "" + result.getDevice().getName());
            if (btDevice == null) {
                mScanner.stopScan(callback);
//                pairDevice(btDevice);
                //pairDevice(btDevice);
            }
//
            try {
                Log.w("AA", "onScanResult - device non null");
                pairDevice(btDevice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(LOG_TAG, "onScanResult: " + btDevice.getName());
            mDevice.setAddress(btDevice.getAddress());
            mDevice.setUserId(user.get_id());

            // removes a device from the local database and server
            removeDeviceForType(mDevice);
            Log.d(LOG_TAG, "Scanner onFinish()");
            deviceAvailable(null);
        }

        @Override
        public void onBatchScanResults(@androidx.annotation.NonNull List<no.nordicsemi.android.support.v18.scanner.ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("DeviceRegisterActivity", "onScanFailed() " + errorCode);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override

    protected void onDestroy() {
        Log.d(LOG_TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
//        unregisterReceiver(broadCastReceiver);
        DisposableManager.dispose();
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    private void checkPermissions() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            requestBluetoothEnable();
        }
        if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    private boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Location permission.
     */
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH && resultCode != Activity.RESULT_OK) {
            requestBluetoothEnable();
        }
    }

//
//    /**
//     * Broadcast Receiver that detects bond state changes (Pairing status changes)
//     */
//
//    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            Log.w("AAA", "Action:" + action);
//            if (intent.getAction().equals("android.bluetooth.device.action.PAIRING_REQUEST")) {
//                try {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    int pin = intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0);
//                    //the pin in case you need to accept for an specific pin
//                    Log.d("PIN", " " + intent.getIntExtra("android.bluetooth.device.extra.PAIRING_KEY", 0));
//                    //maybe you look for a name or address
//                    Log.d("Bonded", device.getName());
//                    byte[] pinBytes;
//                    pinBytes = ("" + pin).getBytes("UTF-8");
//                    device.setPin(pinBytes);
//                    //setPairing confirmation if neeeded
//                    device.setPairingConfirmation(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//            if (action != null && action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
//                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                //case1: bonded already
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDED.");
//                    deviceAvailable(mBluetoothDevice);
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
//                        unregisterReceiver(broadCastReceiver);
//                    }
//                }
//                //case2: creating a bone
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDING. " + mBluetoothDevice.getName());
//                    deviceConnectionStatus.setText(R.string.pairing_device);
//                    progressBarPairing.setVisibility(View.VISIBLE);
//                    btnDeviceRegisterScanner.setEnabled(false);
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
//                        //Broadcasts for balance pairing yunmai
//                        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
//                        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
//                        registerReceiver(broadCastReceiver, intentFilter);
//                    }
//                }
//                // case3: breaking a bond
//                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
//                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_NONE.");
//
//                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI) && broadCastReceiver != null) {
//                        Log.i("AAA", mBluetoothDevice.getName() + " - " + broadCastReceiver.getResultCode());
//                        unregisterReceiver(broadCastReceiver);
//                    }
//                    deviceConnectionStatus.setText(R.string.failed_pairing_device);
//                    btnDeviceRegisterScanner.setEnabled(true);
//                    btnDeviceRegisterScanner.setText(R.string.start_scanner_try);
//                    progressBarPairing.setVisibility(View.INVISIBLE);
//                }
//            }
//        }
//    };
    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null && action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //case1: bonded already
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDED.");
                    deviceAvailable(mBluetoothDevice);

                    if (mBluetoothDevice.getName() != null && mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
                        unregisterReceiver(broadCastReceiver);
                    }
                }
                //case2: creating a bone
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_BONDING. " + mBluetoothDevice.getName());
                    deviceConnectionStatus.setText(R.string.pairing_device);
                    progressBarPairing.setVisibility(View.VISIBLE);
                    btnDeviceRegisterScanner.setEnabled(false);

                    if (mBluetoothDevice.getName() != null && mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
                        //Broadcasts for balance pairing yunmai
                        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
                        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                        registerReceiver(broadCastReceiver, intentFilter);
                    }
                }
                // case3: breaking a bond
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(LOG_TAG, "BroadcastReceiver: BOND_NONE.");

                    if (mBluetoothDevice.getName() != null && mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI) && broadCastReceiver != null) {
                        Log.i("AAA", mBluetoothDevice.getName() + " - " + broadCastReceiver.getResultCode());
                        unregisterReceiver(broadCastReceiver);
                    }
                    deviceConnectionStatus.setText(R.string.failed_pairing_device);
                    btnDeviceRegisterScanner.setEnabled(true);
                    btnDeviceRegisterScanner.setText(R.string.start_scanner_try);
                    progressBarPairing.setVisibility(View.INVISIBLE);
                }
            }
        }
    };

    /**
     * method for balance pairing yunmai
     */
    private BroadcastReceiver broadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                bluetoothDevice.setPin(PIN_YUNMAI.getBytes());
                //does not display the pairing request for the user
                abortBroadcast();
                Log.e(LOG_TAG, "Auto-entering pin: " + PIN_YUNMAI);
            }
        }
    };

    //    //end scanner library ble
//
//    /**
//     * Initialize the components.
//     */
    private void initComponents() {
        populateView();
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //
    public void populateView() {
        if (mDevice == null) return;
        nameDeviceScannerRegister.setText(mDevice.getName());
        if (mDevice.getName().equalsIgnoreCase(NAME_DEVICE_THERM_DL8740)) {
            imgDeviceRegister.setImageResource(R.drawable.device_thermometer_philips_dl8740);
            return;
        }
        imgDeviceRegister.setImageResource(mDevice.getImg());
    }

    //
    public String getServiceUuidDevice(String nameDevice) {
        String service = null;

        if (!nameDevice.isEmpty()) {
            if (nameDevice.equalsIgnoreCase(NAME_DEVICE_THERM_DL8740)) {
                service = GattAttributes.SERVICE_HEALTH_THERMOMETER;
            } else if (nameDevice.equalsIgnoreCase(NAME_DEVICE_GLUCOMETER_PERFORMA)) {
                service = GattAttributes.SERVICE_GLUCOSE;
            } else if (nameDevice.equalsIgnoreCase(NAME_DEVICE_SCALE_1501)) {
                service = SERVICE_SCALE_1501;
            } else if (nameDevice.equalsIgnoreCase(NAME_DEVICE_HEART_RATE)) {
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equalsIgnoreCase(NAME_DEVICE_SMARTBAND_MI2)) {
                service = GattAttributes.SERVICE_STEPS_DISTANCE_CALORIES;
            }
        }
        return service;
    }

    //
//
    public void deviceAvailable(BluetoothDevice device) {
        if (device != null) {
            mDevice.setAddress(device.getAddress());
            // Save in the server
            saveDeviceRegister(mDevice);

            mPulsatorLayout.stop();
            deviceSuccessfullyRegistered.setText(
                    getString(R.string.device_registered_success,
                            device.getName())
            );
            boxRegister.setVisibility(View.GONE);
            boxScanner.setVisibility(View.GONE);
            boxResponse.setVisibility(View.VISIBLE);
        } else {
            animationScanner(false);
            nameDeviceScannerRegister.setText(mDevice.getName());
            deviceConnectionStatus.setText(R.string.device_not_found_try_again);
        }
    }

    //
    public void animationScanner(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                boxRegister.setVisibility(View.GONE);
                boxResponse.setVisibility(View.GONE);
                nameDeviceScanner.setText(mDevice.getName());
                boxScanner.setVisibility(View.VISIBLE);
                mPulsatorLayout.start();
            } else {
                boxResponse.setVisibility(View.GONE);
                boxScanner.setVisibility(View.GONE);
                boxRegister.setVisibility(View.VISIBLE);
                mPulsatorLayout.stop();
            }
        });
    }

    //
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_device_register_scanner) {
            Log.d(LOG_TAG, "onClick: start scanner");
            if (mScanner != null) {
                mScanner.stopScan(callback);
                animationScanner(true);
                mScanner.startScan(callback);
            }

        } else if (id == R.id.btn_device_register_stop) {
            Log.d(LOG_TAG, "onClick: stop scanner");
            animationScanner(false);
            mScanner.stopScan(callback);
        } else if (id == R.id.btn_close_register) {
            finish();
        } else if (id == R.id.btn_close_response) {
            finish();
        }
    }
//

    /**
     * Save device in remote server.
     *
     * @param device {@link Device}
     */
    public void saveDeviceRegister(final Device device) {
        Log.i("AAA", device.toJson());
        device.setModelNumber(null); // TODO Remover quando a API der suporte
        DisposableManager.add(haniotRepository
                .saveDevice(device)
                .subscribe(deviceRest -> {
                    deviceRest.setImg(device.getImg());
                    deviceRest.setUserId(user.get_id());
                    mDeviceDAO.save(deviceRest);
                }, err -> {
                    deviceAvailable(null);
                    Log.w(LOG_TAG, "ERROR SAVE:" + err.getMessage() + device);
                })
        );
    }

    /**
     * Remove device according to its type.
     * This ensures that only one device per type will be registered.
     *
     * @param device {@link Device}
     */
    public void removeDeviceForType(Device device) {
        Device registered = mDeviceDAO.getByType(user.get_id(), device.getType());
        Log.w(LOG_TAG, "registered:" + registered);
        if (registered != null) {
            DisposableManager.add(
                    haniotRepository
                            .deleteDevice(user.get_id(), registered.get_id())
                            .subscribe(() -> {
                                mDeviceDAO.remove(registered.getAddress());
                                unpairDevice(mDevice);
                                btDevice.createBond();
                            }, err -> Log.w(LOG_TAG, "ERROR DELETE:" + err.getMessage())));
        } else {
            unpairDevice(mDevice);
            btDevice.createBond();
        }
    }

    private boolean unpairDevice(Device device) {
        boolean confirmed = false;
        if (!device.getAddress().isEmpty()) {
            BluetoothDevice mBluetoothDevice = BluetoothAdapter.getDefaultAdapter().
                    getRemoteDevice(device.getAddress());
            try {
                Method m = mBluetoothDevice.getClass()
                        .getMethod("removeBond", (Class[]) null);
                m.invoke(mBluetoothDevice, (Object[]) null);
                confirmed = true;
            } catch (Exception e) {
                Log.d(LOG_TAG, "error removing pairing " + e.getMessage());
            }
        }
        return confirmed;
    }
}
