package br.edu.uepb.nutes.haniot.devices.register;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

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

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_LOCATION = 2;


    private SimpleBleScanner mScanner;
    private Device mDevice;
    private DeviceDAO mDeviceDAO;
    private Server server;
    private Session session;
    private ActionBar mActionBar;
    private int contStartScanner = 0;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.btn_device_register)
    Button btnDeviceRegister;

    @BindView(R.id.txt_name_device_register)
    TextView nameDeviceRegister;

    @BindView(R.id.img_device_register)
    ImageView imgDeviceRegister;

    @BindView(R.id.img_bluetooth)
    ImageView imgBluetooth;

    @BindView(R.id.txt_mac_device)
    TextView txtMacDevice;

    @BindView(R.id.pulsator)
    PulsatorLayout mPulsatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        ButterKnife.bind(this);
        initComponents();

        server = Server.getInstance(this);
        session = new Session(this);
        mDeviceDAO = DeviceDAO.getInstance(this);

        btnDeviceRegister.setOnClickListener(this);

        mDevice = getIntent().getParcelableExtra(DeviceManagerActivity.EXTRA_DEVICE);

        //Initialize scanner settings
        mScanner = new SimpleBleScanner.Builder()
                .addFilterServiceUuid(getServiceUuidDevice(mDevice.getName()))
                .addScanPeriod(15000) // 15s
                .build();
    }


    //start scanner library ble
    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
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
            BluetoothDevice device = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                device = scanResult.getDevice();
            }
            if (device == null) {
                mScanner.stopScan();
                return;
            }
            Log.d(TAG, "onScanResult: " + device.getAddress());

            try {
                deviceAvailable(device);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> scanResults) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onFinish() {
            animationScanner(false);
            try {
                deviceAvailable(null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("MainActivity", "onFinish()");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("MainActivity", "onScanFailed() " + errorCode);
        }
    };

    //end scanner library ble

    /**
     * Initialize the components.
     */
    private void initComponents() {
        initToolBar();
        populateView();
    }

    /**
     * Initialize toolbar and insert title.
     */
    private void initToolBar() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.devices));
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initToolBarDetails() {
        mActionBar.setTitle(getString(R.string.details));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void populateView() {
        mDevice = getIntent().getParcelableExtra(DeviceManagerActivity.EXTRA_DEVICE);
        nameDeviceRegister.setText(mDevice.getName());
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


    public void deviceAvailable(BluetoothDevice device) throws JSONException {
        if (device != null) {
            mDevice.setAddress(device.getAddress());

            initToolBarDetails();
            mScanner.stopScan();
            mPulsatorLayout.stop();
            imgBluetooth.setVisibility(View.GONE);
            nameDeviceRegister.setText(getString(R.string.device_registered_success,
                    device.getName()));
            imgDeviceRegister.setImageResource(mDevice.getImg());
            txtMacDevice.setVisibility(View.VISIBLE);
            txtMacDevice.setText(device.getAddress());

            // Save in the server
            saveDeviceRegister(mDevice);
        } else {
            failedFindDevice(++contStartScanner);
        }
    }

    public void animationScanner(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                imgDeviceRegister.setVisibility(View.GONE);
                nameDeviceRegister.setVisibility(View.GONE);
                txtMacDevice.setVisibility(View.GONE);
                btnDeviceRegister.setText(R.string.stop_scanner);
                imgBluetooth.setVisibility(View.VISIBLE);
                mPulsatorLayout.start();
            } else {
                imgDeviceRegister.setVisibility(View.VISIBLE);
                nameDeviceRegister.setVisibility(View.VISIBLE);
                txtMacDevice.setVisibility(View.VISIBLE);
                btnDeviceRegister.setText(R.string.start_scanner);
                imgBluetooth.setVisibility(View.GONE);
                mPulsatorLayout.stop();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_device_register) {
            if (btnDeviceRegister.getText().equals(getString(R.string.start_scanner))) {
                Log.d(TAG, "onClick: start scanner");
                if (mScanner != null) {
                    mScanner.stopScan();
                    animationScanner(true);
                    mScanner.startScan(mScanCallback);
                }
            } else if (btnDeviceRegister.getText().equals(getString(R.string.stop_scanner))) {
                Log.d(TAG, "onClick: stop scanner");
                animationScanner(false);
                mScanner.stopScan();
            }
        }
    }

    public void failedFindDevice(int cont){
       if(cont >= 3){
           Log.d(TAG, "failedFindDevice: "+cont);
           nameDeviceRegister.setText(R.string.failed_to_find_device);
           imgDeviceRegister.setVisibility(View.GONE);
           btnDeviceRegister.setVisibility(View.GONE);
       }else{
           nameDeviceRegister.setText(R.string.device_not_found_try_again);
           txtMacDevice.setVisibility(View.GONE);
       }
    }


    /**
     * @param device
     * @return
     * @throws JSONException
     */
    public String deviceToJson(Device device) {
        mDevice = getIntent().getParcelableExtra(DeviceManagerActivity.EXTRA_DEVICE);
        JSONObject result = new JSONObject();
        try {

            result.put("typeId", mDevice.getTypeId());
            result.put("address", device.getAddress());
            result.put("name", mDevice.getName());
            result.put("manufacturer", mDevice.getManufacturer());
            result.put("modelNumber", mDevice.getModelNumber());
            result.put("img", device.getImg());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf(result);
    }

    public void saveDeviceRegister(Device device) {
        String path = "devices/".concat("/users/").concat(session.get_idLogged());
        server.post(path, deviceToJson(device), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                Log.d(TAG, "onError:");
            }

            @Override
            public void onSuccess(JSONObject result) {
                Gson gson = new Gson();
                Device deviceGson = gson.fromJson(deviceToJson(device), Device.class);
                mDeviceDAO.save(deviceGson);
            }
        });
    }
}
