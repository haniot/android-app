package br.edu.uepb.nutes.haniot.devices.register;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.disposables.CompositeDisposable;

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

    @BindView(R.id.box_not_devices)
    LinearLayout mNoRegisteredDevices;

    @BindView(R.id.title_registered)
    TextView mTitleRegistered;

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

    @BindView(R.id.content_devices)
    ScrollView contentDevices;

    @BindView(R.id.content_error)
    LinearLayout contentError;

    @BindView(R.id.manager_devices_swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayoutDevices;

    @BindView(R.id.box_content)
    RelativeLayout boxContent;

    private User user;
    private DeviceAdapter mAdapterDeviceAvailable;
    private DeviceAdapter mAdapterDeviceRegistered;
    private AppPreferencesHelper appPreferences;
    private DeviceDAO mDeviceDAO;
    private HaniotNetRepository haniotRepository;
    private CompositeDisposable compositeDisposable;
    private List<String> deviceIdToDelete;
    private Handler handler;
    private Runnable runnable;
    private Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_devices);
        ButterKnife.bind(this);
        haniotRepository = HaniotNetRepository.getInstance(this);
        compositeDisposable = new CompositeDisposable();
        appPreferences = AppPreferencesHelper.getInstance(this);
        mDeviceDAO = DeviceDAO.getInstance(this);

        user = appPreferences.getUserLogged();
        if (user == null || user.get_id().isEmpty()) finish();
        deviceIdToDelete = new ArrayList<>();
        initComponents();
        checkConnectivity();
        initDataSwipeRefresh();
        showErrorConnection(false);
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
        swipeRefreshLayoutDevices.setEnabled(false);
        downloadDevicesData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread() {
            @Override
            public void run() {
                Log.w("XXX", "onPause() - run()");
                if (snackbar != null && snackbar.isShown()) snackbar.dismiss();
                if (handler != null) {
                    removePendingDevices();
                    handler.removeCallbacks(runnable);
                }
            }
        }.start();
    }

    /**
     * @param enabled
     */
    private void showErrorConnection(boolean enabled) {
        if (enabled) {
            contentError.setVisibility(View.VISIBLE);
            contentDevices.setVisibility(View.GONE);
        } else {
            contentError.setVisibility(View.GONE);
            contentDevices.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        swipeRefreshLayoutDevices.setOnRefreshListener(this::downloadDevicesData);
    }

    /**
     * Download list of devices registered.
     */
    public void downloadDevicesData() {
        compositeDisposable.add(
                haniotRepository
                        .getAllDevices(user.get_id())
                        .doOnSubscribe(disposable -> {
                            swipeRefreshLayoutDevices.setRefreshing(true);
                            contentDevices.setVisibility(View.GONE);
                        })
                        .doAfterTerminate(() -> {
                            swipeRefreshLayoutDevices.setEnabled(true);
                            swipeRefreshLayoutDevices.setRefreshing(false);
                        })
                        .subscribe(devices -> {
                            Log.w("AAA", Arrays.toString(devices.toArray()));
                            contentDevices.setVisibility(View.VISIBLE);
                            populateDevicesRegistered(setImagesDevices(devices));


                            populateDevicesAvailable(mDeviceDAO.list(user.get_id()));
                            showErrorConnection(false);
                        }, err -> showErrorConnection(true))
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
            showErrorConnection(true);
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
                boxContent.setVisibility(View.VISIBLE);
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
            private void run() {
                removePendingDevices();
            }

            /**
             * @param item
             */
            @Override
            public void onItemClick(Device item) {

            }

            @Override
            public void onLongItemClick(View v, Device item) {
            }

            @Override
            public void onMenuContextClick(View v, Device item) {
            }

            @Override
            public void onItemSwiped(Device item, int position) {
                mAdapterDeviceRegistered.removeItem(item);
                deviceIdToDelete.add(item.get_id());
                handler = new Handler();
                runnable = this::run;
                handler.postDelayed(runnable, 4000);

                snackbar = Snackbar
                        .make(findViewById(R.id.root), getString(R.string.confirm_remove_devices), Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), view -> {
                    mAdapterDeviceRegistered.restoreItem(item, position);
                    mRegisteredRecyclerView.scrollToPosition(position);
                    deviceIdToDelete.remove(item.get_id());
                });
                snackbar.show();
                showNoDevices(mAdapterDeviceRegistered.itemsIsEmpty());
            }
        });
        mRegisteredRecyclerView.setAdapter(mAdapterDeviceRegistered);
        mAdapterDeviceRegistered.enableSwipe(this);
    }

    private void removePendingDevices() {
        Log.w("XXX", "removePendingDevices()");
        if (deviceIdToDelete == null || deviceIdToDelete.isEmpty()) return;
        for (String idDevice : deviceIdToDelete)
            compositeDisposable.add(haniotRepository
                    .deleteDevice(user.get_id(), idDevice).subscribe(() -> {
                        mDeviceDAO.remove(idDevice);
                        deviceIdToDelete.remove(idDevice);

                    }));
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
            showNoDevices(devicesRegistered.isEmpty());
        });
    }

    private void showNoDevices(boolean enabled) {
        if (enabled) {
            mNoRegisteredDevices.setVisibility(View.VISIBLE);
            mTitleRegistered.setVisibility(View.INVISIBLE);
        } else {
            mNoRegisteredDevices.setVisibility(View.GONE);
            mTitleRegistered.setVisibility(View.VISIBLE);
        }
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

        Log.w("AAA", "DAO: " + Arrays.toString(registeredList.toArray()));
        Log.w("AAA", "Available");
        for (Device device : availableList) {
            Log.w("AAA", device.toJson());
        }

        // Add only devices that have not been registered
        Log.w("AAA", "Registered");
        for (Device d : registeredList) {
            Log.w("AAA", d.toJson());
//            Log.w("AAA", "Registered: " + d.toJson() + " - Disponível: " + availableList.indexOf(d));
            availableList.remove(d);
        }
        return availableList;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}