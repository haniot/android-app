package br.edu.uepb.nutes.haniot.devices.register;

import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.DeviceAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
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
    public final String NUMBER_MODEL_HEART_RATE_H7 = "H7";
    public final String NUMBER_MODEL_HEART_RATE_H10 = "H10";
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

    private Server server;
    private Session session;

    private DeviceAdapter mAdapterDeviceAvailable;
    private DeviceAdapter mAdapterDeviceRegistered;

    private DeviceDAO mDeviceDAO;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_devices);
        ButterKnife.bind(this);
        server = Server.getInstance(this);
        session = new Session(this);
        mDeviceDAO = DeviceDAO.getInstance(this);

        initComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateView();
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
        String path = "devices/users/".concat(session.get_idLogged());
        server.get(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                displayLoading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                List<Device> devicesRegistered = jsonToListDevice(result);

                //saves the devices coming from the server in the local database
                for (Device mDevices : devicesRegistered) {
                    mDeviceDAO.save(mDevices);
                }
                populateDevicesRegistered(devicesRegistered);
                populateDevicesAvailable(mDeviceDAO.list(session.getIdLogged()));
                displayLoading(false);
            }
        });
    }

    /**
     * Display ProgressBar or disable according to the parameter value.
     * True to display or False to disable.
     *
     * @param show boolean
     */
    private void displayLoading(boolean show) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    mBoxDevicesRegistered.setVisibility(View.GONE);
                    mBoxDevicesAvailable.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    mBoxDevicesRegistered.setVisibility(View.VISIBLE);
                    mBoxDevicesAvailable.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
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
                Log.w(LOG_TAG, "onItemClick()" + item);
                confirmRemoveDeviceRegister(item);
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
        });
        mAvailableRecyclerView.setAdapter(mAdapterDeviceAvailable);
    }

    /**
     * Populate devices registered.
     *
     * @param devicesRegistered
     */
    private void populateDevicesRegistered(@NonNull List<Device> devicesRegistered) {
        if (devicesRegistered.isEmpty()) {
            mNoRegisteredDevices.setVisibility(View.VISIBLE);
            return;
        }
        mAdapterDeviceRegistered.clearItems();
        mAdapterDeviceRegistered.addItems(devicesRegistered);
    }

    /**
     * Only those who have not been registered.
     *
     * @param devicesRegistered
     */
    private void populateDevicesAvailable(@NonNull List<Device> devicesRegistered) {
        List<Device> devicesAvailable = new ArrayList<>();

        devicesAvailable.add(new Device("Ear Thermometer ".concat(NUMBER_MODEL_THERM_DL8740),
                "Philips", NUMBER_MODEL_THERM_DL8740,
                R.drawable.device_thermometer_philips_dl8740));

        devicesAvailable.add(new Device("Accu-Chek ".concat(NUMBER_MODEL_GLUCOMETER_PERFORMA),
                "Accu-Chek", NUMBER_MODEL_GLUCOMETER_PERFORMA,
                R.drawable.device_glucose_accuchek));

        devicesAvailable.add(new Device("Scale YUNMAI Mini ".concat(NUMBER_MODEL_SCALE_1501),
                "Yunmai", NUMBER_MODEL_SCALE_1501,
                R.drawable.device_scale_yunmai_mini_color));

        devicesAvailable.add(new Device("Heart Rate Sensor ".concat(NUMBER_MODEL_HEART_RATE_H7),
                "Polar", NUMBER_MODEL_HEART_RATE_H7,
                R.drawable.device_heart_rate_h7));

        devicesAvailable.add(new Device("Heart Rate Sensor ".concat(NUMBER_MODEL_HEART_RATE_H10),
                "Polar", NUMBER_MODEL_HEART_RATE_H10,
                R.drawable.device_heart_rate_h10));

        devicesAvailable.add(new Device("Smartband ".concat(NUMBER_MODEL_SMARTBAND_MI2),
                "Xiaomi", NUMBER_MODEL_SMARTBAND_MI2,
                R.drawable.device_smartband_miband2));


        devicesAvailable = mergeDevicesAvailableRegistered(devicesRegistered, devicesAvailable);

        if (devicesAvailable.isEmpty()) {
            mNoAvailableDevices.setVisibility(View.VISIBLE);
            return;
        }
        mAdapterDeviceAvailable.clearItems();
        mAdapterDeviceAvailable.addItems(devicesAvailable);
    }

    /**
     * Merge between list of available and registered devices.
     *
     * @param registeredList {@link List<Device>}
     * @param availableList  {@link List<Device>}
     * @return {@link List<Device>}
     */
    private List<Device> mergeDevicesAvailableRegistered(List<Device> registeredList, List<Device> availableList) {
        /**
         * Add only devices that have not been registered
         */
        for (Device d : registeredList) {
            if (availableList.contains(d))
                availableList.remove(d);
        }
        return availableList;
    }

    private void openRegister(Device device) {
        Intent intent = new Intent(this, DeviceRegisterActivity.class);
        intent.putExtra(EXTRA_DEVICE, device);
        startActivity(intent);
    }

    private void confirmRemoveDeviceRegister(Device device) {
        //Create the AlertDialog generator
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //sets the title
        builder.setTitle(device.getName());
        //sets the message
        builder.setMessage(R.string.remove_device);
        //define a button how to remove
        builder.setPositiveButton(R.string.remove, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                //removes the device from the server database
                removeDeviceRegister(device);
            }
        });
        //define a button how to cancel.
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        //create the AlertDialog
        alert = builder.create();
        alert.show();
    }

    private void removeDeviceRegister(Device device) {
        displayLoading(true);
        String path = "devices/".concat(device.get_id()).concat("/users/").concat(session.get_idLogged());
        server.delete(path, new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                displayLoading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                mDeviceDAO.remove(device);

                List<Device> devicesRegistered = jsonToListDevice(result);
                populateDevicesRegistered(devicesRegistered);
                populateDevicesAvailable(mDeviceDAO.list(session.getIdLogged()));
                displayLoading(false);
            }
        });
    }

    public String deviceToJson(Device device, BluetoothDevice bluetoothDevice) throws JSONException {

        JSONObject result = new JSONObject();
        result.put("typeId", device.get_id());
        result.put("name",device.getName());
        result.put("manufacture",device.getManufacturer());
        result.put("model",device.getModelNumber());
        result.put("address", bluetoothDevice.getAddress());

        return String.valueOf(result);
    }

    //TODO: 1 - finish the save method on the server
    public void saveDeviceRegister(Device device,BluetoothDevice bluetoothDevice,Session session,Server server) throws JSONException {
        displayLoading(true);

        String path = "devices/".concat("/users/").concat(session.get_idLogged());
        server.post(path, deviceToJson(device, bluetoothDevice), new Server.Callback() {
            @Override
            public void onError(JSONObject result) {
                displayLoading(false);
            }

            @Override
            public void onSuccess(JSONObject result) {
                mDeviceDAO.save(device);

                List<Device> devicesRegistered = jsonToListDevice(result);
                populateDevicesRegistered(devicesRegistered);
                populateDevicesAvailable(mDeviceDAO.list(session.getIdLogged()));
                displayLoading(false);
            }
        });
    }

}
