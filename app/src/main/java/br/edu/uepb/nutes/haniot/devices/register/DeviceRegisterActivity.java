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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceRegisterActivity extends AppCompatActivity
        implements DeviceProcessFragment.OnDeviceRegisterListener {

    private final String TAG = "DeviceRegisterActivity ";
    public final static String EXTRA_DEVICE = "extra_device";

    private final String NAME_DEVICE_THERM_DL8740 = "Ear Thermometer DL8740";
    private final String NAME_DEVICE_GLUCOMETER_PERFORMA = "Accu-Chek Performa Connect";
    private final String NAME_DEVICE_SCALE_1501 = "Scale YUNMAI Mini 1501";
    private final String NAME_DEVICE_SCALE_HBF206IT = "Scale OMRON HB-F206IT";
    private final String NAME_DEVICE_HEART_RATE_H7 = "Heart Rate Sensor H7";
    private final String NAME_DEVICE_HEART_RATE_H10 = "Heart Rate Sensor H10";
    private final String NAME_DEVICE_SMARTBAND_MI2 = "Smartband MI Band 2";
    private final String NAME_DEVICE_PRESSURE_BP792IT = "Blood Pressure Monitor BP792IT";

    //scanner
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ENABLE_LOCATION = 2;

    //scanner
    private TextView mResultTextView;
    private ProgressBar mProgresBar;
    private SimpleBleScanner mScanner;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_register);
        ButterKnife.bind(this);

        initComponents();

        // set arguments in fragments DeviceProcessFragment
        DeviceProcessFragment deviceProcessFragment = DeviceProcessFragment.newInstance();

        Bundle bundle = new Bundle();
        Device scannerDevice = getIntent().getParcelableExtra(EXTRA_DEVICE);
        bundle.putParcelable(EXTRA_DEVICE, scannerDevice);
        deviceProcessFragment.setArguments(bundle);

        // open fragments default DeviceProcessFragment
        replaceFragment(deviceProcessFragment);

        // Initialize scanner settings
//        mScanner = new SimpleBleScanner.Builder()
////                .addFilterServiceUuid(GattAttributes.SERVICE_HEALTH_THERMOMETER,
////                        "00001310-0000-1000-8000-00805f9b34fb")
//                .addFilterServiceUuid(getServiceUuidDevice(scannerDevice.getName()))
//                //.addFilterName("MI Band 2")
//                .addScanPeriod(15000) // 15s
//                .build();
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

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bnt_find_device_fragment:
                Log.d(TAG, "onClick:");
                if (mScanner != null) {
                    mScanner.stopScan();
                    mScanner.startScan(mScanCallback);
                }
                break;
            default:
                break;
        }
    }

    //displays the scanner result
    public final SimpleScanCallback mScanCallback = new SimpleScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            BluetoothDevice device = scanResult.getDevice();
            if (device == null) return;
            Log.d(TAG, "onScanResult: " + device);



        }

        @Override
        public void onBatchScanResults(List<ScanResult> scanResults) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onFinish() {
            Log.d("MainActivity", "onFinish()");
            mResultTextView.setText(String.valueOf(mResultTextView.getText())
                    .concat("\n\n")
                    .concat("onFinish()")
            );
            mProgresBar.setVisibility(View.GONE);
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
    }

    /**
     * Initialize toolbar and insert title.
     */
    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.devices));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Replace fragment.
     *
     * @param fragment {@link Fragment}
     */
    public void replaceFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.content, fragment).commit();
        }
    }

    public String getServiceUuidDevice(String nameDevice) {
        String service = null;

        if (!nameDevice.isEmpty()) {
            if (nameDevice.equals(NAME_DEVICE_THERM_DL8740)) {//
                service = GattAttributes.SERVICE_HEALTH_THERMOMETER;
            } else if (nameDevice.equals(NAME_DEVICE_GLUCOMETER_PERFORMA)) {//
                service = GattAttributes.SERVICE_GLUCOSE;
            } else if (nameDevice.equals(NAME_DEVICE_SCALE_1501)) {
                service = GattAttributes.SERVICE_SCALE;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H7)) {//
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H10)) {//
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_SMARTBAND_MI2)) {//
                service = GattAttributes.SERVICE_STEPS_DISTANCE_CALORIES;
            }
        }
        return service;
    }
//    public String chooseDevice2(String nameDevice) {
//        String service = null;
//        switch (nameDevice) {
//            case NAME_DEVICE_THERM_DL8740:
//                service = GattAttributes.SERVICE_HEALTH_THERMOMETER;
//                break;
//            case NAME_DEVICE_GLUCOMETER_PERFORMA:
//                service = GattAttributes.SERVICE_GLUCOSE;
//                break;
//            case NAME_DEVICE_SCALE_1501:
//                service = GattAttributes.SERVICE_SCALE;
//                break;
//            case NAME_DEVICE_SCALE_HBF206IT:
//                service = GattAttributes.SERVICE_GLUCOSE;
//                break;
//            case NAME_DEVICE_HEART_RATE_H7:
//                service = GattAttributes.SERVICE_HEART_RATE;
//                break;
//            case NAME_DEVICE_HEART_RATE_H10:
//                service = GattAttributes.SERVICE_HEART_RATE;
//                break;
//            case NAME_DEVICE_SMARTBAND_MI2:
//                service = GattAttributes.SERVICE_STEPS_DISTANCE_CALORIES;
//                break;
//            case NAME_DEVICE_PRESSURE_BP792IT:
//                service = GattAttributes.SERVICE_BLOOD_PRESSURE;
//                break;
//            default:
//                break;
//        }
//        return service;
//    }

    @Override
    public void onClickStartScan(Device device) {
        // set arguments in fragments DeviceProcessFragment
//        DeviceProcessFragment mDeviceProcessFragment = DeviceScannerFragment.newInstance();
        Bundle bundle = new Bundle();
//        bundle.putParcelable(EXTRA_SERVICE_UUID, getServiceUuid(device));
//        mDeviceProcessFragment.setArguments(bundle);
//        replaceFragment(DeviceScannerFragment);
    }
}
