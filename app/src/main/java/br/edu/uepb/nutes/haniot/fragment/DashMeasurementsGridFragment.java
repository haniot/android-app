package br.edu.uepb.nutes.haniot.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.settings.ManagerMeasurementsActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.model.DateChangedEvent;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementMonitor;
import br.edu.uepb.nutes.haniot.model.SendMeasurementsEvent;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
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
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashMeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<MeasurementMonitor> {

    private final String TAG = "ManagerDevices";
    List<MeasurementMonitor> measurementMonitors;
    private MeasurementMonitor igActivity;
    private MeasurementMonitor igGlucose;
    private MeasurementMonitor igPressure;
    private MeasurementMonitor igTemperature;
    private MeasurementMonitor igWeight;
    private MeasurementMonitor igSleep;
    private MeasurementMonitor igHearRate;
    private MeasurementMonitor igAnthropometric;

    private ScaleManager scaleManager;
    private HeartRateManager heartRateManager;
    private GlucoseManager glucoseManager;
    private BloodPressureManager bloodPressureManager;
    SimpleBleScanner simpleBleScanner;
    DeviceDAO deviceDAO;

    final String glucoseAdress = "4B:74:11:1A:27:33";
    final String heartRateAddress = "E9:50:60:1F:31:D2";
    final String scaleAddress = "D4:36:39:91:75:71";
    final String bloodPressureAddress = "52:3F:42:BA:7D:AC";


    private List<MeasurementMonitor> buttonList = new ArrayList<>();
    private Context mContext;
    private GridDashAdapter mAdapter;

    //    This instance is used to get the preferences of preference screen
    private SharedPreferences preferences;

    // Variables strings of grid
    private String activity = "";
    private String glucose = "";
    private String pressure = "";
    private String temperature = "";
    private String weight = "";
    private String sleep = "";
    private String heartRate = "";

    private String measurementDate = "";

    private String deviceTypeTag;

    private Session session;

    List<Device> devices;

    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;

    @BindView(R.id.edit_monitor)
    TextView editMonitor;


    private DateChangedEvent measurementsValues;

    public DashMeasurementsGridFragment() {
    }

    public static DashMeasurementsGridFragment newInstance() {
        DashMeasurementsGridFragment fragment = new DashMeasurementsGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.preferences = mContext.getSharedPreferences("device_enabled", Context.MODE_PRIVATE);
//        Ajeitar para primeira vez que abrir o app;
        this.measurementsValues = new DateChangedEvent();
        measurementMonitors = new ArrayList<>();

//        builder.addFilterAddress(heartRateAddress);
//        builder.addFilterAddress(scaleAddress);
//        builder.addFilterAddress(bloodPressureAddress);
//        builder.addFilterAddress(glucoseAdress);


        bloodPressureManager = new BloodPressureManager(getContext());
    }


    private boolean isDeviceRegisted(int deviceType) {
        for (Device device : devices)
            if (device.getTypeId() == deviceType) return true;
        return false;
    }

    public void initMeasurementsMonitor() {

//        this.igActivity = new MeasurementMonitor();
//        igActivity.setContext(getContext());
//        igActivity.setIcon(R.drawable.xrunning);
//        igActivity.setDescription(getResources().getString(R.string.activity));
//        igActivity.setMeasurementValue(this.measurementsValues.getActivity());
//        igActivity.setType(ItemGridType.ACTIVITY);


//        this.igTemperature = new MeasurementMonitor();
//        igTemperature.setContext(getContext());
//        igTemperature.setIcon(R.drawable.xtemperature);
//        igTemperature.setDescription(getResources().getString(R.string.temperature));
//        igTemperature.setMeasurementValue(this.measurementsValues.getTemperature());
//        igTemperature.setType(ItemGridType.TEMPERATURE);


//        this.igSleep = new MeasurementMonitor();
//        igSleep.setContext(getContext());
//        igSleep.setIcon(R.drawable.xsleep);
//        igSleep.setDescription(getResources().getString(R.string.sleep));
//        igSleep.setMeasurementValue(this.measurementsValues.getSleep());
//        igSleep.setType(ItemGridType.SLEEP);


        this.activity = getResources().getString(R.string.activity);
        this.glucose = getResources().getString(R.string.blood_glucose);
        this.pressure = getResources().getString(R.string.blood_pressure);
        this.temperature = getResources().getString(R.string.temperature);
        this.weight = getResources().getString(R.string.weight);
        this.sleep = getResources().getString(R.string.sleep);
        this.heartRate = getResources().getString(R.string.heart_rate);
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        SimpleDateFormat spn = new SimpleDateFormat(getResources().getString(R.string.date_format));

        this.measurementDate = spn.format(date);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_dash2, container, false);
        ButterKnife.bind(this, view);
        initRecyclerView();
        initComponents();
        initManagerBLE();

        return view;
    }

    public void initManagerBLE() {
        measurementMonitors.clear();
        devices = DeviceDAO.getInstance(getContext()).list(session.getIdLogged());

        MeasurementMonitor measurementMonitor = null;
        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_weight))) {
            if (scaleManager == null) {
                scaleManager = new ScaleManager(getContext());
                scaleManager.setSimpleCallback(scaleDataCallback);
            }
            measurementMonitor = new MeasurementMonitor();
            measurementMonitor.setContext(getContext());
            measurementMonitor.setIcon(R.drawable.xweight);
            measurementMonitor.setDescription(getResources().getString(R.string.weight));
            measurementMonitor.setMeasurementValue("");
            measurementMonitor.setType(ItemGridType.WEIGHT);
            if (isDeviceRegisted(DeviceType.BODY_COMPOSITION))
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }
        //scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("D4:36:39:91:75:71"));

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_heart_rate))) {
            if (heartRateManager == null) {
                heartRateManager = new HeartRateManager(getContext());
                heartRateManager.setSimpleCallback(heartRateDataCallback);
            }
            measurementMonitor = new MeasurementMonitor();
            measurementMonitor.setContext(getContext());
            measurementMonitor.setIcon(R.drawable.xcardiogram);
            measurementMonitor.setDescription(getResources().getString(R.string.heart_rate));
            measurementMonitor.setMeasurementValue("");
            measurementMonitor.setType(ItemGridType.HEART_RATE);
            if (isDeviceRegisted(DeviceType.HEART_RATE))
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }
        //heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("A8:96:75:B0:28:D2"));

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_glucose))) {
            if (glucoseManager == null) {
                glucoseManager = new GlucoseManager(getContext());
                glucoseManager.setSimpleCallback(glucoseDataCallback);
            }
            measurementMonitor = new MeasurementMonitor();
            measurementMonitor.setContext(getContext());
            measurementMonitor.setIcon(R.drawable.xglucosemeter);
            measurementMonitor.setDescription(getResources().getString(R.string.blood_glucose));
            measurementMonitor.setMeasurementValue("");
            measurementMonitor.setType(ItemGridType.BLOOD_GLUCOSE);
            if (isDeviceRegisted(DeviceType.GLUCOMETER))
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_pressure))) {
            if (bloodPressureManager == null) {
                bloodPressureManager = new BloodPressureManager(getContext());
                bloodPressureManager.setSimpleCallback(bloodPressureDataCallback);
            }
            measurementMonitor = new MeasurementMonitor();
            measurementMonitor.setContext(getContext());
            measurementMonitor.setIcon(R.drawable.xblood_pressure);
            measurementMonitor.setDescription(getResources().getString(R.string.blood_pressure));
            measurementMonitor.setMeasurementValue("");
            measurementMonitor.setType(ItemGridType.BLOOD_PRESSURE);
            if (isDeviceRegisted(DeviceType.BLOOD_PRESSURE))
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else
                measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }

        if (getPreferenceBoolean(getResources()
                .getString(R.string.key_anthropometric))) {
            measurementMonitor = new MeasurementMonitor();
            measurementMonitor.setContext(getContext());
            measurementMonitor.setIcon(R.drawable.xshape);
            measurementMonitor.setDescription(getResources().getString(R.string.anthropometric));
            measurementMonitor.setMeasurementValue("");
            measurementMonitor.setType(ItemGridType.ANTHROPOMETRIC);
            measurementMonitors.add(measurementMonitor);
        }

        mAdapter.clearItems();
        mAdapter.addItems(measurementMonitors);
    }

    public void initScanner() {
        //TODO onStart()
        deviceDAO = DeviceDAO.getInstance(getContext());
        SimpleBleScanner.Builder builder = new SimpleBleScanner.Builder();
        builder.addScanPeriod(100000000);
        for (Device device : deviceDAO.list(session.getIdLogged())) {
            //TODO Escanear somente os que estão habilitados
            Log.i("Devices", "Salvo: " + device.getName() + " - " + device.getAddress() + " - " + device.getTypeId());
            builder.addFilterAddress(device.getAddress());
        }
        simpleBleScanner = builder.build();

        simpleBleScanner.startScan(new SimpleScannerCallback() {
            @Override
            public void onScanResult(int i, ScanResult scanResult) {
                String address = scanResult.getDevice().getAddress();

                Log.w("Scan", scanResult.getDevice().toString());
                Device device = deviceDAO.get(address, session.getIdLogged());
                if (device != null) {
                    int type = device.getTypeId();
                    Log.i("Devices", "Type search: " + type + " - Requerid: " + DeviceType.GLUCOMETER);
                    switch (type) {
                        case DeviceType
                                .BLOOD_PRESSURE:
                            //TODO Melhorar isso na reafatoração
                            if (bloodPressureManager != null)
                                if (bloodPressureManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                    bloodPressureManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
                            break;
                        case DeviceType
                                .GLUCOMETER:
                            if (glucoseManager != null)
                                if (glucoseManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                    glucoseManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
                            break;
                        case DeviceType
                                .BODY_COMPOSITION:
                            if (scaleManager != null)
                                if (scaleManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED) {
                                    scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
                                    Log.i("Devices", "Connecting Scale...");
                                }
                            break;
                        case DeviceType
                                .HEART_RATE:
                            if (heartRateManager != null)
                                if (heartRateManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                                    heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
                            break;
                    }


//                switch (address) {
//                    case glucoseAdress:
//                        glucoseManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(glucoseAdress));
//                        break;
//                    case heartRateAddress:
//                        heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(heartRateAddress));
//
//                        break;
//                    case scaleAddress:
//                        scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(scaleAddress));
//
//                        break;
//                    case bloodPressureAddress:
//                        bloodPressureManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bloodPressureAddress));
//
//                        break;
//                }
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
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initManagerBLE();
        initScanner();

    }

    private void initComponents() {

        session = new Session(getContext());
        editMonitor.setOnClickListener(v -> {
            Intent it = new Intent(getContext(), ManagerMeasurementsActivity.class);
            startActivity(it);
        });

        // updateGrid();
    }

    @Override
    public void onPause() {
        super.onPause();
        scaleManager = null;
        bloodPressureManager = null;
        glucoseManager = null;
        heartRateManager = null;
        simpleBleScanner.stopScan();
    }

    private void initRecyclerView() {
        mAdapter = new GridDashAdapter(mContext);
        mAdapter.setHasStableIds(true);

        // This method set the same size to all items of grid
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        initManagerBLE();
        initScanner();
        // updateGrid();
    }

    private void updateItemsOfGrid(boolean status, MeasurementMonitor item) {
        if (status) {
            if (!buttonList.contains(item)) {
                buttonList.add(item);
            }
            int index = buttonList.lastIndexOf(item);

            switch (item.getType()) {
                case ItemGridType.ACTIVITY:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getActivity());
                    break;

                case ItemGridType.BLOOD_GLUCOSE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getGlucose());
                    break;

                case ItemGridType.BLOOD_PRESSURE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getPressure());
                    break;

                case ItemGridType.TEMPERATURE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues
                            .getTemperature());
                    break;

                case ItemGridType.WEIGHT:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getWeight());
                    break;

                case ItemGridType.SLEEP:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues.getSleep());
                    break;

                case ItemGridType.HEART_RATE:
                    buttonList.get(index).setMeasurementValue(this.measurementsValues
                            .getHeartRate());
                    break;
            }
        } else {

            int index = buttonList.lastIndexOf(item);
            if (index >= 0) {
                buttonList.remove(index);
            }

        }
    }

    public BluetoothDevice getDevice(int measurementType) {
        for (Device device : devices) {
            if (device.getTypeId() == measurementType) return BluetoothAdapter
                    .getDefaultAdapter()
                    .getRemoteDevice(device.getAddress());
        }
        return null;
    }

    public void updateGrid() {

        //Pega os dados que foram selecionados nas preferencias
        Boolean activity = getPreferenceBoolean(getResources()
                .getString(R.string.key_activity));

        Boolean bloodGlucose = getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_glucose));

        Boolean bloodPressure = getPreferenceBoolean(getResources()
                .getString(R.string.key_blood_pressure));

        Boolean temperature = getPreferenceBoolean(getResources()
                .getString(R.string.key_temperature));

        Boolean weight = getPreferenceBoolean(getResources()
                .getString(R.string.key_weight));

        Boolean sleep = getPreferenceBoolean(getResources()
                .getString(R.string.key_sleep));

        Boolean heartRate = getPreferenceBoolean(getResources()
                .getString(R.string.key_heart_rate));

        Boolean anthropometric = getPreferenceBoolean(getResources()
                .getString(R.string.key_anthropometric));

//        If the list is empty, just add the items
        if (buttonList != null && buttonList.isEmpty()) {
            if (activity) buttonList.add(this.igActivity);
            if (bloodGlucose) {
                buttonList.add(this.igGlucose);
//                glucoseManager = new GlucoseManager(getContext());
//                BluetoothDevice bluetoothDevice = getDevice(DeviceType.GLUCOMETER);
//                if (bluetoothDevice != null)
//                    glucoseManager.connectDevice(bluetoothDevice);
            }
            if (bloodPressure) buttonList.add(this.igPressure);
            if (temperature) {
                buttonList.add(this.igTemperature);

            }
            if (weight) {
                buttonList.add(this.igWeight);

//                BluetoothDevice bluetoothDevice = getDevice(DeviceType.BODY_COMPOSITION);
//                if (bluetoothDevice != null)
                // scaleManager.connectDevice(bluetoothDevice);
            }
            if (sleep) buttonList.add(this.igSleep);
            if (heartRate) {
                buttonList.add(this.igHearRate);

//                        .getRemoteDevice("E9:50:60:1F:31:D2"));
//                BluetoothDevice bluetoothDevice = getDevice(DeviceType.HEART_RATE);
//                if (bluetoothDevice != null)
//                    heartRateManager.connectDevice(bluetoothDevice);
            }
            if (anthropometric) buttonList.add(this.igAnthropometric);

//            if the list is not empty, call updateItemsOfGrid to updateStatus the items
        } else if (buttonList != null) {

            updateItemsOfGrid(activity, igActivity);
            updateItemsOfGrid(bloodGlucose, igGlucose);
            updateItemsOfGrid(bloodPressure, igPressure);
            updateItemsOfGrid(temperature, igTemperature);
            updateItemsOfGrid(weight, igWeight);
            updateItemsOfGrid(sleep, igSleep);
            updateItemsOfGrid(heartRate, igHearRate);
            updateItemsOfGrid(anthropometric, igAnthropometric);
        }


    }

    BloodPressureDataCallback bloodPressureDataCallback = new BloodPressureDataCallback() {
        @Override
        public void onConnected() {
            Log.i("DEVICE", "Connected on BloodPressure");
        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onMeasurementReceiver(Measurement measurementBloodPressure) {
            igPressure.setMeasurementValue(String.valueOf(measurementBloodPressure.getValue()));
            mAdapter.notifyDataSetChanged();
        }
    };

    TemperatureDataCallback temperatureDataCallback = new TemperatureDataCallback() {
        @Override
        public void onConnected(BluetoothDevice device) {
            Log.i("DEVICE", "Connected on Thermometer");

        }

        @Override
        public void onDisconnected(BluetoothDevice device) {

        }

        @Override
        public void onMeasurementReceiver(Measurement measurementTemperature) {
            igTemperature.setMeasurementValue(String.format("%.2f", measurementTemperature.getValue()));
            mAdapter.notifyDataSetChanged();
        }
    };

    ScaleDataCallback scaleDataCallback = new ScaleDataCallback() {
        @Override
        public void onConnected() {
            Log.i("DEVICE", "Connected on Scale");
            for (MeasurementMonitor measurementMonitor : measurementMonitors) {
                if (measurementMonitor.getType() == ItemGridType.WEIGHT) {
                    measurementMonitor.setStatus(MeasurementMonitor.CONNECTED);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onDisconnected() {
            for (MeasurementMonitor measurementMonitor : measurementMonitors) {
                if (measurementMonitor.getType() == ItemGridType.WEIGHT) {
                    measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
        }

        @Override
        public void onMeasurementReceiver(Measurement measurementScale) {
            updateMeasurement(measurementScale, ItemGridType.WEIGHT);
        }

        @Override
        public void onMeasurementReceiving(String bodyMassMeasurement, String bodyMassUnit) {
            for (MeasurementMonitor measurementMonitor : measurementMonitors) {
                if (measurementMonitor.getType() == ItemGridType.WEIGHT) {
                    measurementMonitor.setStatus(MeasurementMonitor.RECEIVING);
                    mAdapter.notifyDataSetChanged();
                    break;
                }
            }
            updateMeasurement(bodyMassMeasurement, ItemGridType.WEIGHT);
        }
    };

    private void updateMeasurement(String value, int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                measurementMonitor.setMeasurementValue(value);
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    private void updateMeasurement(Measurement measurement, int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                measurementMonitor.setMeasurementValue(String.format("%.2f", measurement.getValue()));
                mAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    GlucoseDataCallback glucoseDataCallback = new GlucoseDataCallback() {
        @Override
        public void onConnected() {
            Log.i("DEVICE", "Connected on Glucose");

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onMeasurementReceiver(Measurement measurementGlucose) {

            igGlucose.setMeasurementValue(GlucoseManager.parse(measurementGlucose));
            mAdapter.notifyDataSetChanged();
        }
    };

    HeartRateDataCallback heartRateDataCallback = new HeartRateDataCallback() {
        @Override
        public void onConnected() {
            Log.i("DEVICE", "Connected on Heart Rate");

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onMeasurementReceiver(Measurement measurementHeartRate) {
            igHearRate.setMeasurementValue(String.format("%.2f", measurementHeartRate.getValue()));
            mAdapter.notifyDataSetChanged();
        }
    };


    //    Method used to get the preferences of sharedpreference screen
    public Boolean getPreferenceBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(MeasurementMonitor item) {
//        item.setStatus(MeasurementMonitor.RECEIVING);
//        mAdapter.notifyDataSetChanged();
//        new Handler().postDelayed(() -> {
//            item.setStatus(MeasurementMonitor.CONNECTED);
//            item.setMeasurementValue("20.2");
//            mAdapter.notifyDataSetChanged();
//        }, 1000);

        if (item.getType() == ItemGridType.BLOOD_GLUCOSE)
            startActivity(new Intent(getContext(), GlucoseActivity.class));
        else if (item.getType() == ItemGridType.HEART_RATE)
            startActivity(new Intent(getContext(), HeartRateActivity.class));
        else if (item.getType() == ItemGridType.WEIGHT)
            startActivity(new Intent(getContext(), ScaleActivity.class));
        else if (item.getType() == ItemGridType.BLOOD_PRESSURE)
            startActivity(new Intent(getContext(), BloodPressureHDPActivity.class));

    }

    @Override
    public void onLongItemClick(View v, MeasurementMonitor item) {
        throw new UnsupportedOperationException();
    }

    //    Methods to manually add measurement button
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

            default:
                return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateDate(DateChangedEvent e) {
        this.measurementsValues = e;

        updateGrid();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void sendMeasurement(SendMeasurementsEvent e) {
        openConfirmMeasurementsDialog();
        Log.d("TESTE", "Evento recebido no grid, Heart: " + this.measurementsValues.getHeartRate());
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(getContext()).run();
    }

    private void openConfirmMeasurementsDialog() {

        LayoutInflater li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.confirm_measurements_layout, null);
        String name = getContext().getResources().getString(R.string.name_last_patient);

        String patientName = session.getString(name);
        Character t = patientName.charAt(0);
        patientName = patientName.substring(1);
        String first = t.toString().toUpperCase();
        patientName = first + patientName;

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getResources().getString(R.string.save_measurements_of) + " "
                + patientName);

        builder.setView(view);

        TextView textActivity = view.findViewById(R.id.textActivity);
        textActivity.setText(this.measurementsValues.getActivity());

        TextView textSleep = view.findViewById(R.id.textSleep);
        textSleep.setText(this.measurementsValues.getSleep());

        TextView textGlucose = view.findViewById(R.id.textGlucose);
        textGlucose.setText(this.measurementsValues.getGlucose());

        TextView textPressure = view.findViewById(R.id.textPressure);
        textPressure.setText(this.measurementsValues.getPressure());

        TextView textTemperature = view.findViewById(R.id.textTemperature);
        textTemperature.setText(this.measurementsValues.getTemperature());

        TextView textWeight = view.findViewById(R.id.textWeight);
        textWeight.setText(this.measurementsValues.getWeight());

        TextView textHeart = view.findViewById(R.id.textHeartRate);
        textHeart.setText(this.measurementsValues.getHeartRate());

        TextView textHeight = view.findViewById(R.id.textHeight);
        textHeight.setText(this.measurementsValues.getHeight());

        TextView textCircumference = view.findViewById(R.id.textCircumference);
        textCircumference.setText(this.measurementsValues.getCircumference());

        builder.setPositiveButton(getResources().getString(R.string.confirm), (dialog, id) ->
//                Colocar filtro por paciente no sincronizar
                synchronizeWithServer());
        builder.setNegativeButton(getResources().getString(R.string.cancel), null);
        builder.show();
    }

}
