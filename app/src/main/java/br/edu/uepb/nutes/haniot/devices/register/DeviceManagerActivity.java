package br.edu.uepb.nutes.haniot.devices.register;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.DeviceAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.server.Server;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

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

    private Server server;
    private AppPreferencesHelper appPreferences;
    private DeviceAdapter mAdapterDeviceAvailable;
    private DeviceAdapter mAdapterDeviceRegistered;
    private User user;

    private DeviceDAO mDeviceDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_devices);
        ButterKnife.bind(this);
        server = Server.getInstance(this);
        appPreferences = AppPreferencesHelper.getInstance(this);
        mDeviceDAO = DeviceDAO.getInstance(this);

        user = appPreferences.getUserLogged();
        if (user == null) {
            finish();
        }

        initComponents();
        populateView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
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
        actionBar.setTitle(getString(R.string.devices));
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void populateView() {
        if (!ConnectionUtils.internetIsEnabled(getApplicationContext())) {
            mMessageAlert.setVisibility(View.VISIBLE);
            mBoxDevicesRegistered.setVisibility(View.GONE);
            mBoxDevicesAvailable.setVisibility(View.GONE);
            return;
        }

        displayLoading(true);
        String path = "devices/users/".concat(user.get_id());
        server.get(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                Log.d(LOG_TAG, "onError: ");

                runOnUiThread(() -> {
                    messageErrorServer.setVisibility(View.VISIBLE);
                    boxRegisteredAvailable.setVisibility(View.INVISIBLE);
                });
                displayLoading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                List<Device> devicesRegistered = jsonToListDevice(result);
                populateDevicesRegistered(newList(devicesRegistered));
                populateDevicesAvailable(mDeviceDAO.list(user.get_id()));
                displayLoading(false);
            }
        });
    }

    /**
     * Returns a device with your image.
     *
     * @param list {@link List<Device>}
     * @return {@link List<Device>}
     */
    public List<Device> newList(List<Device> list) {
        List<Device> listDevices = new ArrayList<>();

        for (Device devices : list) {
            if (devices.getName().equalsIgnoreCase("Ear Thermometer ".concat(NUMBER_MODEL_THERM_DL8740))) {
                devices.setImg(R.drawable.device_thermometer_philips_dl8740);
                listDevices.add(devices);
            } else if (devices.getName().equalsIgnoreCase("Accu-Chek ".concat(NUMBER_MODEL_GLUCOMETER_PERFORMA))) {
                devices.setImg(R.drawable.device_glucose_accuchek);
                listDevices.add(devices);
            } else if (devices.getName().equalsIgnoreCase("Scale YUNMAI Mini ".concat(NUMBER_MODEL_SCALE_1501))) {
                devices.setImg(R.drawable.device_scale_yunmai_mini_color);
                listDevices.add(devices);
            } else if (devices.getName().equalsIgnoreCase("Heart Rate Sensor")) {
                devices.setImg(R.drawable.device_heart_rate_h10);
                listDevices.add(devices);
            } else if (devices.getName().equalsIgnoreCase("Smartband ".concat(NUMBER_MODEL_SMARTBAND_MI2))) {
                devices.setImg(R.drawable.device_smartband_miband2);
                listDevices.add(devices);
            }
        }
        return listDevices;
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
     * Deserialize json in a list of devices.
     * If any error occurs it will be returned List empty.
     *
     * @param json {@link JSONObject}
     * @return {@link List<Device>}
     */
    private List<Device> jsonToListDevice(JSONObject json) {
        if (json == null || !json.has("devices")) return new ArrayList<>();

        Type typeUserAccess = new TypeToken<List<Device>>() {
        }.getType();

        try {
            JSONArray jsonArray = json.getJSONArray("devices");
            return new Gson().fromJson(jsonArray.toString(), typeUserAccess);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Init RecyclerView Registered Devices
     */
    private void initRegisteredDevicesRecyclerView() {
        mAdapterDeviceRegistered = new DeviceAdapter(this);
        mRegisteredRecyclerView.setHasFixedSize(true);
        mRegisteredRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRegisteredRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRegisteredRecyclerView.addItemDecoration(new DividerItemDecoration(
                mRegisteredRecyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation())
        );

        mAdapterDeviceRegistered.setListener(new OnRecyclerViewListener<Device>() {
            @Override
            public void onItemClick(Device item) {
                confirmRemoveDeviceRegister(item);
            }

            @Override
            public void onLongItemClick(View v, Device item) {
            }

            @Override
            public void onMenuContextClick(View v, Device item) {
            }
        });
        mRegisteredRecyclerView.setAdapter(mAdapterDeviceRegistered);
    }

    /**
     * Init RecyclerView Available Devices
     */
    private void initAvailableDevicesRecyclerView() {
        mAdapterDeviceAvailable = new DeviceAdapter(this);
        mAvailableRecyclerView.setHasFixedSize(true);
        mAvailableRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAvailableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAvailableRecyclerView.addItemDecoration(new DividerItemDecoration(
                mAvailableRecyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation())
        );

        mAdapterDeviceAvailable.setListener(new OnRecyclerViewListener<Device>() {
            @Override
            public void onItemClick(Device item) {
                openRegister(item);
            }

            @Override
            public void onLongItemClick(View v, Device item) {

            }

            @Override
            public void onMenuContextClick(View v, Device item) {

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
                R.drawable.device_thermometer_philips_dl8740_mini, DeviceType.THERMOMETER));

        devicesAvailable.add(new Device("Accu-Chek ".concat(NUMBER_MODEL_GLUCOMETER_PERFORMA),
                "Accu-Chek", NUMBER_MODEL_GLUCOMETER_PERFORMA,
                R.drawable.device_glucose_accuchek, DeviceType.GLUCOMETER));

        devicesAvailable.add(new Device("Scale YUNMAI Mini ".concat(NUMBER_MODEL_SCALE_1501),
                "Yunmai", NUMBER_MODEL_SCALE_1501,
                R.drawable.device_scale_yunmai_mini_color, DeviceType.BODY_COMPOSITION));

        devicesAvailable.add(new Device("Heart Rate Sensor", NUMBER_MODEL_HEART_RATE,
                NUMBER_MODEL_HEART_RATE, R.drawable.device_heart_rate_h10, DeviceType.HEART_RATE));

        devicesAvailable.add(new Device("Smartband ".concat(NUMBER_MODEL_SMARTBAND_MI2),
                "Xiaomi", NUMBER_MODEL_SMARTBAND_MI2,
                R.drawable.device_smartband_miband2, DeviceType.SMARTBAND));

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
    private List<Device> mergeDevicesAvailableRegistered(List<Device> registeredList, List<Device> availableList) {
        // Add only devices that have not been registered
        for (Device d : registeredList) {
            if (availableList.contains(d)) {
                availableList.remove(d);
            }
        }
        return availableList;
    }

    private void openRegister(Device device) {
        Intent intent = new Intent(this, DeviceRegisterActivity.class);
        intent.putExtra(EXTRA_DEVICE, device);
        startActivity(intent);
    }

    @SuppressLint("ResourceType")
    private void confirmRemoveDeviceRegister(Device device) {
        AlertDialog alert;
        //Create the AlertDialog generator
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // sets image title
        builder.setIcon(R.drawable.ic_action_warning);
        //sets the title
        builder.setTitle(R.string.attention);
        //sets the message
        builder.setMessage(getString(R.string.remove_device, device.getName()));
        //define a button how to remove
        builder.setPositiveButton(R.string.remove, (arg0, arg1) -> {
            //removes the device from the server database
            removeDeviceRegister(device);
        });
        //define a button how to cancel.
        builder.setNegativeButton(R.string.cancel, (arg0, arg1) -> {
        });
        //create the AlertDialog
        alert = builder.create();
        alert.show();
    }


    private void removeDeviceRegister(Device device) {
        displayLoading(true);
        String path = "devices/".concat(device.get_id()).concat("/users/").concat(user.get_id());
        server.delete(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                displayLoading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                mDeviceDAO.remove(device.getAddress());
                unpairDevice(device);
                populateView();
            }
        });
    }

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