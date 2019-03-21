package br.edu.uepb.nutes.haniot.devices.register;

import android.Manifest;
import android.annotation.SuppressLint;
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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class DeviceRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "DeviceRegisterActivity ";

    private final String NAME_DEVICE_THERM_DL8740 = "Ear Thermometer DL8740";
    private final String NAME_DEVICE_GLUCOMETER_PERFORMA = "Accu-Chek Performa Connect";
    private final String NAME_DEVICE_SCALE_1501 = "Scale YUNMAI Mini 1501";
    private final String NAME_DEVICE_YUNMAI = "YUNMAI-SIGNAL-M1US";
    private final String NAME_DEVICE_HEART_RATE = "Heart Rate Sensor H7, H10 ...";
    private final String NAME_DEVICE_SMARTBAND_MI2 = "Smartband MI Band 2";
    private final String SERVICE_SCALE_1501 = "00001310-0000-1000-8000-00805f9b34fb";
    private final String PIN_YUNMAI = "000000";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_LOCATION = 2;

    private SimpleBleScanner mScanner;
    private Device mDevice;
    private DeviceDAO mDeviceDAO;
    private Server server;
    private Session session;
    private BluetoothDevice btDevice;

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

        server = Server.getInstance(this);
        session = new Session(this);
        mDeviceDAO = DeviceDAO.getInstance(this);


        btnDeviceRegisterScanner.setOnClickListener(this);
        btnDeviceRegisterStop.setOnClickListener(this);
        btnCloseRegister.setOnClickListener(this);
        btnCloseResponse.setOnClickListener(this);

        mDevice = getIntent().getParcelableExtra(DeviceManagerActivity.EXTRA_DEVICE);

        //Initialize scanner settings
        mScanner = new SimpleBleScanner.Builder()
                .addScanPeriod(15000) // 15s
                .addFilterServiceUuid(getServiceUuidDevice(mDevice.getName()))
                .build();

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver, filter);

        initComponents();
    }

    //start scanner library ble
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        checkPermissions();
    }

    @Override

    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
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

    public final SimpleScannerCallback mScanCallback = new SimpleScannerCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull ScanResult scanResult) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btDevice = scanResult.getDevice();
            }
            if (btDevice == null) {
                mScanner.stopScan();
                return;
            }
            Log.d(TAG, "onScanResult: " + btDevice.getName());
            mScanner.stopScan();

            //removes a device from the local database and server
            mDevice.setAddress(btDevice.getAddress());
            if (removeDeviceForType(mDevice)) {
                if (unpairDevice(mDevice)) {
                    btDevice.createBond();
                }
            } else {
                if (btDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    if (unpairDevice(mDevice)) {
                        btDevice.createBond();
                    }
                } else {
                    btDevice.createBond();
                }
            }
        }

        @Override
        public void onBatchScanResults(@NonNull List<ScanResult> scanResults) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onFinish() {
            animationScanner(false);
            deviceAvailable(null);
            Log.d("MainActivity", "onFinish()");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("MainActivity", "onScanFailed() " + errorCode);
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action != null && action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //case1: bonded already
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    deviceAvailable(mBluetoothDevice);

                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
                        unregisterReceiver(broadCastReceiver);
                    }
                }
                //case2: creating a bone
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING. " + mBluetoothDevice.getName());
                    deviceConnectionStatus.setText(R.string.pairing_device);
                    progressBarPairing.setVisibility(View.VISIBLE);
                    btnDeviceRegisterScanner.setEnabled(false);

                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
                        //Broadcasts for balance pairing yunmai
                        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
                        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
                        registerReceiver(broadCastReceiver, intentFilter);
                    }
                }
                //case3: breaking a bond
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");

                    if (mBluetoothDevice.getName().equals(NAME_DEVICE_YUNMAI)) {
                        unregisterReceiver(broadCastReceiver);
                    }
                    deviceConnectionStatus.setText(R.string.failed_pairing_device);
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
                Log.e(TAG, "Auto-entering pin: " + PIN_YUNMAI);
            }
        }
    };
    //end scanner library ble

    /**
     * Initialize the components.
     */
    private void initComponents() {
        populateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateView() {
        if (mDevice == null) return;
        nameDeviceScannerRegister.setText(mDevice.getName());
        if (mDevice.getName().equalsIgnoreCase(NAME_DEVICE_THERM_DL8740)) {
            imgDeviceRegister.setImageResource(R.drawable.device_thermometer_philips_dl8740);
            return;
        }
        imgDeviceRegister.setImageResource(mDevice.getImg());
    }

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
            nameDeviceScannerRegister.setText(mDevice.getName());
            deviceConnectionStatus.setText(R.string.device_not_found_try_again);
        }
    }

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

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_device_register_scanner) {
            Log.d(TAG, "onClick: start scanner");
            if (mScanner != null) {
                mScanner.stopScan();
                animationScanner(true);
                mScanner.startScan(mScanCallback);
            }

        } else if (id == R.id.btn_device_register_stop) {
            Log.d(TAG, "onClick: stop scanner");
            animationScanner(false);
            mScanner.stopScan();
        } else if (id == R.id.btn_close_register) {
            finish();
        } else if (id == R.id.btn_close_response) {
            finish();
        }
    }

    /**
     * Convert Device in JSON
     *
     * @param device {@link Device}
     */
    public String deviceToJson(Device device) {
        JSONObject result = new JSONObject();
        try {
            result.put("typeId", device.getTypeId());
            result.put("address", device.getAddress());
            result.put("name", device.getName());
            result.put("manufacturer", device.getManufacturer());
            result.put("modelNumber", device.getModelNumber());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(result);
    }

    /**
     * Save device in remote server.
     *
     * @param device {@link Device}
     */
    public void saveDeviceRegister(final Device device) {
        String path = "devices/".concat("/users/").concat(session.get_idLogged());
        server.post(path, this.deviceToJson(device), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
            }

            @Override
            public void onSuccess(JSONObject result) {
                Device mDevice = new Gson().fromJson(String.valueOf(result), Device.class);
                mDevice.setImg(device.getImg());
                mDevice.setUser(session.getUserLogged());
                mDeviceDAO.save(mDevice);
            }
        });
    }

    /**
     * Remove device according to its type.
     * This ensures that only one device per type will be registered.
     *
     * @param device {@link Device}
     */
    public boolean removeDeviceForType(Device device) {
        boolean confirmed = false;
        for (Device d : mDeviceDAO.list(session.getIdLogged())) {
            if (d.getTypeId() == device.getTypeId()) {
                confirmed = true;
                String path = "devices/"
                        .concat(d.get_id())
                        .concat("/users/")
                        .concat(session.get_idLogged());

                server.delete(path, new Server.Callback() {
                    @Override
                    public void onError(JSONObject result) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onSuccess(JSONObject result) {
                        try {
                            if (result.has("code") && result.getInt("code") == 204) {
                                mDeviceDAO.remove(d.getAddress());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
        return confirmed;
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
                Log.d(TAG, "error removing pairing " + e.getMessage());
            }
        }
        return confirmed;
    }
}
