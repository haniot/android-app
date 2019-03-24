package br.edu.uepb.nutes.haniot.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.RequiresApi;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.settings.ManagerMeasurementsActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementMonitor;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BloodPressureManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.GlucoseManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.HeartRateManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ScaleManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodPressureDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.GlucoseDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.HeartRateDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<MeasurementMonitor> {
    private DeviceDAO deviceDAO;
    private final String TAG = "ManagerDevices";
    private ScaleManager scaleManager;
    private HeartRateManager heartRateManager;
    private GlucoseManager glucoseManager;
    private BloodPressureManager bloodPressureManager;
    private GridDashAdapter mAdapter;
    private String deviceTypeTag;
    private Session session;
    private List<MeasurementMonitor> measurementMonitors;
    private Context mContext;
    private SimpleBleScanner simpleBleScanner;
    private DashboardChartsFragment.Communicator communicator;
    private SharedPreferences preferences;
    private List<Device> devices;
    private SimpleBleScanner.Builder builder;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;
    @BindView(R.id.edit_monitor)
    TextView editMonitor;

    public MeasurementsGridFragment() {
        // Required empty public constructor
    }

    public static MeasurementsGridFragment newInstance() {
        return new MeasurementsGridFragment();
    }

    /**
     * On create.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferences = mContext.getSharedPreferences("device_enabled", Context.MODE_PRIVATE);
        measurementMonitors = new ArrayList<>();
        bloodPressureManager = new BloodPressureManager(getContext());
    }

    /**
     * On Create View.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurements_dashboard, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        initComponents();
        initManagerBLE();

        return view;
    }

    /**
     * On activity created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initManagerBLE();
    }

    /**
     * On attach.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        try {
            communicator = (DashboardChartsFragment.Communicator) context;
        } catch (ClassCastException castException) {
        }
    }

    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
        initManagerBLE();
    }

    /**
     * On pause.
     */
    @Override
    public void onPause() {
        super.onPause();
        scaleManager = null;
        bloodPressureManager = null;
        glucoseManager = null;
        heartRateManager = null;
        simpleBleScanner.stopScan();
    }

    /**
     * Get device registeres from type.
     *
     * @param deviceType
     * @return
     */
    private Device getDeviceRegistered(int deviceType) {
        for (Device device : devices)
            if (device.getTypeId() == deviceType) return device;
        return null;
    }

    /**
     * Callback of scan result of bluetooth devices.
     */
    SimpleScannerCallback simpleScannerCallback = new SimpleScannerCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int i, ScanResult scanResult) {
            String address = scanResult.getDevice().getAddress();
            if (scanResult.getDevice() == null) Log.i(TAG, "getDevice null");
            if (scanResult.getDevice().getAddress() == null) Log.i(TAG, "getAddress null");
            Device device = deviceDAO.get(scanResult.getDevice().getAddress(),
                    session.getIdLogged());

            if (device != null) {
                switch (device.getTypeId()) {
                    case DeviceType.BLOOD_PRESSURE:
                        if (bloodPressureManager != null)
                            if (bloodPressureManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                bloodPressureManager.connectDevice(BluetoothAdapter.getDefaultAdapter()
                                        .getRemoteDevice(address));
                        Log.i(TAG, "Connecting Blood Pressure...");
                        break;
                    case DeviceType.GLUCOMETER:
                        if (glucoseManager != null)
                            if (glucoseManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                glucoseManager.connectDevice(BluetoothAdapter.getDefaultAdapter()
                                        .getRemoteDevice(address));
                        Log.i(TAG, "Connecting Glucometer...");
                        break;
                    case DeviceType.BODY_COMPOSITION:
                        if (scaleManager != null)
                            if (scaleManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED) {
                                scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter()
                                        .getRemoteDevice(address));
                                Log.i(TAG, "Connecting Scale...");
                            }
                        break;
                    case DeviceType.HEART_RATE:
                        if (heartRateManager != null)
                            if (heartRateManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter()
                                        .getRemoteDevice(address));
                        Log.i(TAG, "Connecting Heart Rate...");
                        break;
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> list) {

        }

        @Override
        public void onScanFailed(int i) {

        }

        @Override
        public void onFinish() {

        }
    };

    /**
     * Init BLE Manager of devices for monitoring.
     */
    public void initManagerBLE() {
        measurementMonitors.clear();
        devices = DeviceDAO.getInstance(getContext()).list(session.getUserLogged().get_id());
        builder = new SimpleBleScanner.Builder();
        builder.addScanPeriod(999999999);

        MeasurementMonitor measurementMonitor;

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_weight))) {
            if (scaleManager == null) {
                scaleManager = new ScaleManager(getContext());
                scaleManager.setSimpleCallback(scaleDataCallback);
            }
            measurementMonitor = new MeasurementMonitor(getContext(), R.drawable.xweight,
                    getResources().getString(R.string.weight),
                    "", ItemGridType.WEIGHT, getString(R.string.unit_kg));
            if (getDeviceRegistered(DeviceType.BODY_COMPOSITION) != null) {
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
                Log.i(TAG, getDeviceRegistered(DeviceType.BODY_COMPOSITION).getTypeId() + "");
                builder.addFilterAddress(getDeviceRegistered(DeviceType.BODY_COMPOSITION)
                        .getAddress());
            } else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_heart_rate))) {
            if (heartRateManager == null) {
                heartRateManager = new HeartRateManager(getContext());
                heartRateManager.setSimpleCallback(heartRateDataCallback);
            }
            measurementMonitor = new MeasurementMonitor(getContext(), R.drawable.xcardiogram,
                    getResources().getString(R.string.heart_rate),
                    "", ItemGridType.HEART_RATE, getString(R.string.unit_pulse));
            if (getDeviceRegistered(DeviceType.HEART_RATE) != null) {
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
                builder.addFilterAddress(getDeviceRegistered(DeviceType.HEART_RATE)
                        .getAddress());
            } else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_glucose))) {
            if (glucoseManager == null) {
                glucoseManager = new GlucoseManager(getContext());
                glucoseManager.setSimpleCallback(glucoseDataCallback);
            }
            measurementMonitor = new MeasurementMonitor(getContext(), R.drawable.xglucosemeter,
                    getResources().getString(R.string.blood_glucose),
                    "", ItemGridType.BLOOD_GLUCOSE, getString(R.string.unit_glucose_mg_dL));
            if (getDeviceRegistered(DeviceType.GLUCOMETER) != null) {
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
                builder.addFilterAddress(getDeviceRegistered(DeviceType.GLUCOMETER)
                        .getAddress());
            } else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_pressure))) {
            if (bloodPressureManager == null) {
                bloodPressureManager = new BloodPressureManager(getContext());
                bloodPressureManager.setSimpleCallback(bloodPressureDataCallback);
            }
            measurementMonitor = new MeasurementMonitor(getContext(), R.drawable.xblood_pressure,
                    getResources().getString(R.string.blood_pressure),
                    "", ItemGridType.BLOOD_PRESSURE, getString(R.string.unit_pressure));
            if (getDeviceRegistered(DeviceType.BLOOD_PRESSURE) != null) {
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
                builder.addFilterAddress(getDeviceRegistered(DeviceType.BLOOD_PRESSURE)
                        .getAddress());
            } else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_anthropometric))) {
            measurementMonitor = new MeasurementMonitor(getContext(), R.drawable.xshape,
                    getResources().getString(R.string.anthropometric),
                    "", ItemGridType.ANTHROPOMETRIC, "");
            measurementMonitors.add(measurementMonitor);
        }

        mAdapter.clearItems();
        mAdapter.addItems(measurementMonitors);
        simpleBleScanner = builder.build();
        simpleBleScanner.startScan(simpleScannerCallback);
    }

    /**
     * Init basics components.
     */
    private void initComponents() {
        deviceDAO = DeviceDAO.getInstance(getContext());
        session = new Session(getContext());
        editMonitor.setOnClickListener(v -> {
            Intent it = new Intent(getContext(), ManagerMeasurementsActivity.class);
            startActivity(it);
        });
    }

    /**
     * Init recycler view of list devices.
     */
    private void initRecyclerView() {
        mAdapter = new GridDashAdapter(mContext);
        mAdapter.setHasStableIds(true);
        gridMeasurement.setHasFixedSize(true);

        gridMeasurement.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        deviceTypeTag = gridMeasurement.getTag().toString();
        /**
         * Set a grid layout to recyclerview,
         * the calculateNoOfColumns was used to set the grid autospacing
         */
        if (deviceTypeTag.equals("tablet")) {
            gridMeasurement.setLayoutManager(new GridLayoutManager(mContext,
                    calculateNoOfColumns(mContext)));
        } else {
            gridMeasurement.setLayoutManager(new LinearLayoutManager(getContext()));
        }
        gridMeasurement.setItemAnimator(new DefaultItemAnimator());
        gridMeasurement.setNestedScrollingEnabled(false);

        mAdapter.setListener(this);
        gridMeasurement.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {

                Collections.swap(measurementMonitors, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                // and notify the adapter that its dataset has changed
                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (deviceTypeTag.equals("tablet")) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
                } else {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.UP | ItemTouchHelper.DOWN);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(gridMeasurement);
    }

    /**
     * calculate number of columns of list measurement.
     *
     * @param context
     * @return
     */
    public int calculateNoOfColumns(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = 1;

        if (this.deviceTypeTag.equals("tablet")) {
//            260 is the size for tablets
            noOfColumns = (int) (dpWidth / 260);
        } else {
//            160 is the size for smartphones
            noOfColumns = (int) (dpWidth / 160);
        }
        return noOfColumns;
    }

    private MeasurementMonitor getMeasurementMonitor(int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                return measurementMonitor;
            }
        }
        return null;
    }

    /**
     * Data callback of Blood Pressure.
     */
    BloodPressureDataCallback bloodPressureDataCallback = new BloodPressureDataCallback() {
        @Override
        public void onConnected() {
            Log.i(TAG, "Connected on BloodPressure");
            getMeasurementMonitor(ItemGridType.BLOOD_PRESSURE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "Disconnected on Blood Pressure");
            getMeasurementMonitor(ItemGridType.BLOOD_PRESSURE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(Measurement measurementBloodPressure) {
            Log.i(TAG, "Receiver measurement of Blood Pressure");
            updateMeasurement(measurementBloodPressure, ItemGridType.BLOOD_PRESSURE);
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Data callback of Scale.
     */
    ScaleDataCallback scaleDataCallback = new ScaleDataCallback() {
        @Override
        public void onConnected() {
            Log.i(TAG, "Connected on Scale");
            getMeasurementMonitor(ItemGridType.WEIGHT).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "Disconnected on Scale");
            getMeasurementMonitor(ItemGridType.WEIGHT).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(Measurement measurementScale) {
            Log.i(TAG, "Receiver measurement of Scale");
            updateMeasurement(measurementScale, ItemGridType.WEIGHT);
            communicator.notifyNewMeasurement(String.format("%.2f", measurementScale.getValue()));
        }

        @Override
        public void onMeasurementReceiving(String bodyMassMeasurement, long timeStamp, String bodyMassUnit) {
            Log.i(TAG, "Receiving measurement of Scale");
            getMeasurementMonitor(ItemGridType.WEIGHT).setStatus(MeasurementMonitor.RECEIVING);
            mAdapter.notifyDataSetChanged();
            communicator.notifyNewMeasurement(bodyMassMeasurement);
            updateMeasurement(bodyMassMeasurement, timeStamp, ItemGridType.WEIGHT);
        }
    };

    /**
     * Data callback of Glucose.
     */
    GlucoseDataCallback glucoseDataCallback = new GlucoseDataCallback() {
        @Override
        public void onConnected() {
            Log.i(TAG, "Connected on Glucose");
            getMeasurementMonitor(ItemGridType.BLOOD_GLUCOSE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "Disconnected on Glucose");
            getMeasurementMonitor(ItemGridType.BLOOD_GLUCOSE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(Measurement measurementGlucose) {
            Log.i(TAG, "Receiver measurement of Glucose");
            updateMeasurement(measurementGlucose, ItemGridType.BLOOD_GLUCOSE);
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Data callback Heart Rate.
     */
    HeartRateDataCallback heartRateDataCallback = new HeartRateDataCallback() {
        @Override
        public void onConnected() {
            Log.i(TAG, "Connected on Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected() {
            Log.i(TAG, "Disconnected on Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(Measurement measurementHeartRate) {
            Log.i(TAG, "Receiver measurement of Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.CONNECTED);
            updateMeasurement(measurementHeartRate, ItemGridType.HEART_RATE);
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Data callback of Temperature.
     */
    TemperatureDataCallback temperatureDataCallback = new TemperatureDataCallback() {
        @Override
        public void onConnected(BluetoothDevice device) {
            Log.i(TAG, "Connected on Thermometer");
            getMeasurementMonitor(ItemGridType.TEMPERATURE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(BluetoothDevice device) {
            Log.i(TAG, "Connected on Thermometer");
            getMeasurementMonitor(ItemGridType.TEMPERATURE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(Measurement measurementTemperature) {
            Log.i(TAG, "Received measurement of Temperature");
            updateMeasurement(measurementTemperature, ItemGridType.TEMPERATURE);
            mAdapter.notifyDataSetChanged();
        }
    };

    /**
     * Update measurement not finalized.
     *
     * @param value
     * @param timeStamp
     * @param type
     */
    private void updateMeasurement(String value, long timeStamp, int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                measurementMonitor.setMeasurementValue(value);
                measurementMonitor.setTime(DateUtils.formatDate(timeStamp, getString(R.string.time_format_simple)));
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * Update measurement finalized.
     *
     * @param measurement
     * @param type
     */
    private void updateMeasurement(Measurement measurement, int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                measurementMonitor.setMeasurementValue(String.format("%.2f", measurement.getValue()));
                Log.i(TAG, "Value measurement: " + String.format("%.2f", measurement.getValue()));
                measurementMonitor.setTime(DateUtils.formatDate(measurement.getRegistrationDate(), getString(R.string.time_format_simple)));
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * Get key of devices for monitoring.
     *
     * @param key
     * @return
     */
    public Boolean getPreferenceBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    /**
     * On Clink from list devices monitor.
     *
     * @param item
     */
    @Override
    public void onItemClick(MeasurementMonitor item) {
        if (item.getType() == ItemGridType.BLOOD_GLUCOSE)
            startActivity(new Intent(getContext(), GlucoseActivity.class));
        else if (item.getType() == ItemGridType.HEART_RATE)
            startActivity(new Intent(getContext(), HeartRateActivity.class));
        else if (item.getType() == ItemGridType.WEIGHT)
            startActivity(new Intent(getContext(), ScaleActivity.class));
        else if (item.getType() == ItemGridType.BLOOD_PRESSURE)
            startActivity(new Intent(getContext(), BloodPressureHDPActivity.class));
    }

    /**
     * On Clink from list devices monitor.
     *
     * @param v
     * @param item
     */
    @Override
    public void onLongItemClick(View v, MeasurementMonitor item) {
        throw new UnsupportedOperationException();
    }

    /**
     * Clink add measurement manually.
     *
     * @param v
     * @param item
     */
    @Override
    public void onMenuContextClick(View v, MeasurementMonitor item) {
        int type = item.getType();
        Intent it = new Intent(getContext(), ManuallyAddMeasurement.class);
        switch (type) {

            case ItemGridType.ACTIVITY:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.ACTIVITY);
                startActivity(it);
                break;

            case ItemGridType.BLOOD_GLUCOSE:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_GLUCOSE);
                startActivity(it);
                break;

            case ItemGridType.BLOOD_PRESSURE:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;

            case ItemGridType.TEMPERATURE:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.TEMPERATURE);
                startActivity(it);
                break;

            case ItemGridType.WEIGHT:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.WEIGHT);
                startActivity(it);
                break;

            case ItemGridType.SLEEP:
                it.putExtra(getResources().getString(R.string.measurementType), ItemGridType.SLEEP);
                startActivity(it);
                break;

            case ItemGridType.HEART_RATE:
                it.putExtra(getResources().getString(R.string.measurementType),
                        ItemGridType.HEART_RATE);
                startActivity(it);
                break;

            case ItemGridType.ANTHROPOMETRIC:
                it.putExtra(getResources().getString(R.string.measurementType),
                        ItemGridType.ANTHROPOMETRIC);
                startActivity(it);
                break;
        }
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(getContext()).run();
    }
}
