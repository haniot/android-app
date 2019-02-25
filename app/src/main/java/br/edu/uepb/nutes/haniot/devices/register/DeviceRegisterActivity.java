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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class DeviceRegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "DeviceRegisterActivity ";


    private final String NAME_DEVICE_THERM_DL8740 = "Ear Thermometer DL8740";
    private final String NAME_DEVICE_GLUCOMETER_PERFORMA = "Accu-Chek Performa Connect";
    private final String NAME_DEVICE_SCALE_1501 = "Scale YUNMAI Mini 1501";
    private final String NAME_DEVICE_HEART_RATE_H7 = "Heart Rate Sensor H7";
    private final String NAME_DEVICE_HEART_RATE_H10 = "Heart Rate Sensor H10";
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
    private IntentFilter intentFilter;
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

    @BindView(R.id.txt_device_not_found)
    TextView deviceNotFound;

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

    @BindView(R.id.devices_progressBar_bonded)
    ProgressBar progressBarBonded;

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

        //Broadcasts for balance pairing yunmai
        intentFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);

        initComponents();
    }

    //start scanner library ble
    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    @Override

    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
        unregisterReceiver(broadCastReceiver);
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     *
     * @return boolean
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

    public final SimpleScanCallback mScanCallback = new SimpleScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            //BluetoothDevice device = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btDevice = scanResult.getDevice();
            }

            if (btDevice == null) {
                mScanner.stopScan();
                return;
            }
            Log.d(TAG, "onScanResult: " + btDevice.getName());
            mScanner.stopScan();
            btDevice.createBond();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> scanResults) {
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

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //case1: bonded already
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    deviceAvailable(mBluetoothDevice);
                }
                //case2: creating a bone
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING. " + btDevice.getName());

                    if (mBluetoothDevice.getName().equals(btDevice.getName())) {
                        registerReceiver(broadCastReceiver, intentFilter);
                    }
                }
                //case3: breaking a bond
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
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
        imgDeviceRegister.setImageResource(mDevice.getImg());
    }


    public String getServiceUuidDevice(String nameDevice) {
        String service = null;

        if (!nameDevice.isEmpty()) {
            if (nameDevice.equals(NAME_DEVICE_THERM_DL8740)) {
                service = GattAttributes.SERVICE_HEALTH_THERMOMETER;
            } else if (nameDevice.equals(NAME_DEVICE_GLUCOMETER_PERFORMA)) {
                service = GattAttributes.SERVICE_GLUCOSE;
            } else if (nameDevice.equals(NAME_DEVICE_SCALE_1501)) {
                service = SERVICE_SCALE_1501;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H7)) {
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H10)) {
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_SMARTBAND_MI2)) {
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
            deviceNotFound.setText(R.string.device_not_found_try_again);
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
                //removes a device from the local database and server
                removeDeviceForType(mDevice);

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
     * @param device
     * @return
     * @throws JSONException
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

    public void saveDeviceRegister(final Device device) {
        String path = "devices/".concat("/users/").concat(session.get_idLogged());
        server.post(path, this.deviceToJson(device), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                Log.d(TAG, "onError:");
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
     * check during the scanner whether a device is already saved on the server and removes from the local DB server
     **/
    public void removeDeviceForType(Device device) {
        for (Device d : mDeviceDAO.list(session.getIdLogged())) {
            if (d.getTypeId() == device.getTypeId()) {
                String path = "devices/"
                        .concat(d.get_id())
                        .concat("/users/")
                        .concat(session.get_idLogged());

                server.delete(path, new Server.Callback() {
                    @Override
                    public void onError(JSONObject result) {
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
    }
}


