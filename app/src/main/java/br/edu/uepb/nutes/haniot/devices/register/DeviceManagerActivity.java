package br.edu.uepb.nutes.haniot.devices.register;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.DeviceAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.utils.GattAttributes.SERVICE_GLUCOSE;
import static br.edu.uepb.nutes.haniot.utils.GattAttributes.SERVICE_HEALTH_THERMOMETER;
import static br.edu.uepb.nutes.haniot.utils.GattAttributes.SERVICE_HEART_RATE;
import static br.edu.uepb.nutes.haniot.utils.GattAttributes.SERVICE_SCALE_YUNMAI;
import static br.edu.uepb.nutes.haniot.utils.GattAttributes.SERVICE_STEPS_DISTANCE_CALORIES;

public class DeviceManagerActivity extends AppCompatActivity {
    private final String LOG_TAG = getClass().getSimpleName();

    public static final String EXTRA_DEVICE = "extra_device";

    public final String NUMBER_MODEL_THERM_DL8740 = "DL8740";
    public final String NUMBER_MODEL_GLUCOMETER_PERFORMA = "Performa Connect";
    public final String NUMBER_MODEL_SCALE_1501 = "1501";
    public final String NUMBER_MODEL_HEART_RATE = "Polar H7, Polar H10...";
    public final String NUMBER_MODEL_SMARTBAND_MI2 = "MI Band 2";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.registered_devices_recyclerview)
    RecyclerView mRegisteredRecyclerView;

    @BindView(R.id.available_devices_recyclerview)
    RecyclerView mAvailableRecyclerView;

    @BindView(R.id.no_registered_devices_textView)
    TextView mNoRegisteredDevices;

    @BindView(R.id.no_available_devices_textView)
    TextView mNoAvailableDevices;

    @BindView(R.id.message_alert_textView)
    TextView mMessageAlert;

    @BindView(R.id.box_devices_registered)
    LinearLayout mBoxDevicesRegistered;

    @BindView(R.id.box_devices_available)
    LinearLayout mBoxDevicesAvailable;

    @BindView(R.id.devices_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.message_error_server)
    TextView messageErrorServer;

    @BindView(R.id.devices_registered_available)
    LinearLayout boxRegisteredAvailable;

    private User user;
    private DeviceAdapter mAdapterDeviceAvailable;
    private DeviceAdapter mAdapterDeviceRegistered;
    private AppPreferencesHelper appPreferences;
    private DeviceDAO mDeviceDAO;
    private HaniotNetRepository haniotRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_devices);
        ButterKnife.bind(this);
        haniotRepository = HaniotNetRepository.getInstance(this);
        appPreferences = AppPreferencesHelper.getInstance(this);
        mDeviceDAO = DeviceDAO.getInstance(this);

        user = appPreferences.getUserLogged();
        if (user == null || user.get_id().isEmpty()) finish();

        initComponents();
        checkConnectivity();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        downloadDevicesData();
    }

    /**
     * Download list of devices registered.
     */
    public void downloadDevicesData() {
        DisposableManager.add(
                haniotRepository
                        .getAllDevices(user.get_id())
                        .doOnSubscribe(disposable -> displayLoading(true))
                        .doAfterTerminate(() -> displayLoading(false))
                        .subscribe(devices -> {
                            Log.w("AAA", Arrays.toString(devices.toArray()));
                            populateDevicesRegistered(setImagesDevices(devices));
                            populateDevicesAvailable(mDeviceDAO.list(user.get_id()));
                        }, err -> {
                            messageErrorServer.setVisibility(View.VISIBLE);
                            boxRegisteredAvailable.setVisibility(View.INVISIBLE);
                        })
        );
    }

    /**
     * Initialize the components.
     */
    private void initComponents() {
        initToolBar();
        initRegisteredDevicesRecyclerView();
        initAvailableDevicesRecyclerView();
    }

    /**
     * Initialize toolbar and insert title.
     */
    private void initToolBar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setTitle(getString(R.string.devices));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void checkConnectivity() {
        if (!ConnectionUtils.internetIsEnabled(getApplicationContext())) {
            mMessageAlert.setVisibility(View.VISIBLE);
            mBoxDevicesRegistered.setVisibility(View.GONE);
            mBoxDevicesAvailable.setVisibility(View.GONE);
            return;
        }
    }

    /**
     * Returns a device with your image.
     *
     * @param devices {@link List<Device>}
     * @return {@link List<Device>}
     */
    public List<Device> setImagesDevices(List<Device> devices) {
        for (Device d : devices) {
            switch (d.getType()) {
                case DeviceType.THERMOMETER:
                    d.setImg(R.drawable.device_thermometer_philips_dl8740);
                    break;
                case DeviceType.GLUCOMETER:
                    d.setImg(R.drawable.device_glucose_accuchek);
                    break;
                case DeviceType.BODY_COMPOSITION:
                    d.setImg(R.drawable.device_scale_yunmai_mini_color);
                    break;
                case DeviceType.HEART_RATE:
                    d.setImg(R.drawable.device_heart_rate_h10);
                    break;
                case DeviceType.SMARTBAND:
                    d.setImg(R.drawable.device_smartband_miband2);
                    break;
                default:
                    break;
            }
        }
        return devices;
    }

    /**
     * Display ProgressBar or disable according to the parameter value.
     * True to display or False to disable.
     *
     * @param show boolean
     */
    private void displayLoading(boolean show) {
        runOnUiThread(() -> {
            if (show) {
                mBoxDevicesRegistered.setVisibility(View.GONE);
                mBoxDevicesAvailable.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mBoxDevicesRegistered.setVisibility(View.VISIBLE);
                mBoxDevicesAvailable.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Init RecyclerView Registered Devices
     */
    private void initRegisteredDevicesRecyclerView() {
        mAdapterDeviceRegistered = new DeviceAdapter(this);
        mRegisteredRecyclerView.setHasFixedSize(true);
        mRegisteredRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRegisteredRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapterDeviceRegistered.setListener(new OnRecyclerViewListener<Device>() {
            @Override
            public void onItemClick(Device item) {
                confirmRemoveDevice(item);
            }

            @Override
            public void onLongItemClick(View v, Device item) {
            }

            @Override
            public void onMenuContextClick(View v, Device item) {
            }

            @Override
            public void onItemSwiped(Device item, int position) {

            }
        });
        mRegisteredRecyclerView.setAdapter(mAdapterDeviceRegistered);
        mAdapterDeviceRegistered.enableSwipe(this);
    }

    /**
     * Init RecyclerView Available Devices
     */
    private void initAvailableDevicesRecyclerView() {
        mAdapterDeviceAvailable = new DeviceAdapter(this);
        mAvailableRecyclerView.setHasFixedSize(true);
        mAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAvailableRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapterDeviceAvailable.setListener(new OnRecyclerViewListener<Device>() {
            @Override
            public void onItemClick(Device device) {
                Intent intent = new Intent(DeviceManagerActivity.this, DeviceRegisterActivity.class);
                intent.putExtra(EXTRA_DEVICE, device);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View v, Device item) {

            }

            @Override
            public void onMenuContextClick(View v, Device item) {

            }

            @Override
            public void onItemSwiped(Device item, int position) {

            }
        });

        mAvailableRecyclerView.setAdapter(mAdapterDeviceAvailable);
    }

    /**
     * Populate devices registered.
     *
     * @param devicesRegistered {@link List<Device>}
     */
    public void populateDevicesRegistered(@NonNull List<Device> devicesRegistered) {
        mAdapterDeviceRegistered.clearItems();
        mAdapterDeviceRegistered.addItems(devicesRegistered);

        runOnUiThread(() -> {
            if (devicesRegistered.isEmpty()) {
                mNoRegisteredDevices.setVisibility(View.VISIBLE);
            } else {
                mNoRegisteredDevices.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Only those who have not been registered.
     *
     * @param devicesRegistered {@link List<Device>}
     */
    public void populateDevicesAvailable(@NonNull List<Device> devicesRegistered) {
        List<Device> devicesAvailable = new ArrayList<>();

        devicesAvailable.add(new Device("Ear Thermometer ".concat(NUMBER_MODEL_THERM_DL8740),
                "Philips", NUMBER_MODEL_THERM_DL8740,
                R.drawable.device_thermometer_philips_dl8740_mini, DeviceType.THERMOMETER, SERVICE_HEALTH_THERMOMETER));

        devicesAvailable.add(new Device("Accu-Chek ".concat(NUMBER_MODEL_GLUCOMETER_PERFORMA),
                "Accu-Chek", NUMBER_MODEL_GLUCOMETER_PERFORMA,
                R.drawable.device_glucose_accuchek, DeviceType.GLUCOMETER, SERVICE_GLUCOSE));

        devicesAvailable.add(new Device("Scale YUNMAI Mini ".concat(NUMBER_MODEL_SCALE_1501),
                "Yunmai", NUMBER_MODEL_SCALE_1501,
                R.drawable.device_scale_yunmai_mini_color, DeviceType.BODY_COMPOSITION, SERVICE_SCALE_YUNMAI));

        devicesAvailable.add(new Device("Heart Rate Sensor", NUMBER_MODEL_HEART_RATE,
                NUMBER_MODEL_HEART_RATE, R.drawable.device_heart_rate_h10, DeviceType.HEART_RATE, SERVICE_HEART_RATE));

        devicesAvailable.add(new Device("Smartband ".concat(NUMBER_MODEL_SMARTBAND_MI2),
                "Xiaomi", NUMBER_MODEL_SMARTBAND_MI2,
                R.drawable.device_smartband_miband2, DeviceType.SMARTBAND, SERVICE_STEPS_DISTANCE_CALORIES));

        mAdapterDeviceAvailable.clearItems();
        mAdapterDeviceAvailable.addItems(
                mergeDevicesAvailableRegistered(devicesRegistered, devicesAvailable)
        );

        if (devicesAvailable.isEmpty()) {
            mNoAvailableDevices.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Merge between list of available and registered devices.
     *
     * @param registeredList {@link List<Device>}
     * @param availableList  {@link List<Device>}
     * @return {@link List<Device>}
     */
    private List<Device> mergeDevicesAvailableRegistered(List<Device> registeredList,
                                                         List<Device> availableList) {
        // Add only devices that have not been registered
        for (Device d : registeredList) {
            if (availableList.contains(d)) {
                availableList.remove(d);
            }
        }
        return availableList;
    }

    /**
     * Dialog to confirm removal of associated device.
     *
     * @param device {@link Device}
     */
    private void confirmRemoveDevice(Device device) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setIcon(R.drawable.ic_action_warning);
        builder.setTitle(R.string.attention);
        builder.setMessage(getString(R.string.remove_device, device.getName()));
        //define a button how to remove
        builder.setPositiveButton(R.string.remove, (arg0, arg1) -> {
            //removes the device from the server database
            removeDevice(device);
        });
        //define a button how to cancel.
        builder.setNegativeButton(R.string.cancel, (arg0, arg1) -> {
        });
        builder.create().show();
    }

    /**
     * Remove device from server and cellphone.
     *
     * @param device
     */
    private void removeDevice(Device device) {
        DisposableManager.add(
                haniotRepository.deleteDevice(user.get_id(), device.get_id())
                        .doOnSubscribe(disposable -> displayLoading(true))
                        .doAfterTerminate(() -> displayLoading(false))
                        .subscribe(() -> {
                            mDeviceDAO.remove(device.getAddress());
                            unpairDevice(device);
                            downloadDevicesData();
                        }, err -> {
                            Log.w(LOG_TAG, "ERROR DELETE DEVICE: " + err.getMessage());
                        })
        );
    }

    /**
     * Unpair device from cellphone.
     *
     * @param device
     */
    private void unpairDevice(Device device) {
        if (device.getAddress().isEmpty()) return;
        BluetoothDevice mBluetoothDevice = BluetoothAdapter.getDefaultAdapter().
                getRemoteDevice(device.getAddress());
        try {
            Method m = mBluetoothDevice.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(mBluetoothDevice, (Object[]) null);
        } catch (Exception e) {
            Log.d(LOG_TAG, "error removing pairing " + e.getMessage());
        }
    }

}