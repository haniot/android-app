package br.edu.uepb.nutes.haniot.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GridDashAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.model.DateChangedEvent;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.SendMeasurementsEvent;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BloodPressureManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.GlucoseManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.HeartRateManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ScaleManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ThermometerManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodPressureDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.GlucoseDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.HeartRateDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashMeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<ItemGrid> {

    private final String TAG = "ManagerDevices";
    private ItemGrid igActivity;
    private ItemGrid igGlucose;
    private ItemGrid igPressure;
    private ItemGrid igTemperature;
    private ItemGrid igWeight;
    private ItemGrid igSleep;
    private ItemGrid igHearRate;
    private ItemGrid igAnthropometric;

    private ScaleManager scaleManager;
    private ThermometerManager temperatureManager;
    private HeartRateManager heartRateManager;
    private GlucoseManager glucoseManager;
    private BloodPressureManager bloodPressureManager;
    SimpleBleScanner simpleBleScanner;
    DeviceDAO deviceDAO;

    final String glucoseAdress = "4B:74:11:1A:27:33";
    final String heartRateAddress = "E9:50:60:1F:31:D2";
    final String scaleAddress = "D4:36:39:91:75:71";
    final String bloodPressureAddress = "52:3F:42:BA:7D:AC";


    private List<ItemGrid> buttonList = new ArrayList<>();
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

        PreferenceManager.setDefaultValues(
                getActivity(), R.xml.pref_manage_measurements, false);
        this.preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        Ajeitar para primeira vez que abrir o app;
        this.measurementsValues = new DateChangedEvent();


        scaleManager = new ScaleManager(getContext());
        scaleManager.setSimpleCallback(scaleDataCallback);
        //scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("D4:36:39:91:75:71"));

        heartRateManager = new HeartRateManager(getContext());
        heartRateManager.setSimpleCallback(heartRateDataCallback);
        //heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("A8:96:75:B0:28:D2"));

        temperatureManager = new ThermometerManager(getContext());
        temperatureManager.setSimpleCallback(temperatureDataCallback);
        //temperatureManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice("1C:87:74:01:73:10"));

        glucoseManager = new GlucoseManager(getContext());
        glucoseManager.setSimpleCallback(glucoseDataCallback);

        bloodPressureManager = new BloodPressureManager(getContext());
        bloodPressureManager.setSimpleCallback(bloodPressureDataCallback);


        deviceDAO = DeviceDAO.getInstance(getContext());
        SimpleBleScanner.Builder builder = new SimpleBleScanner.Builder();
//        for (Device device : deviceDAO.list(session.getIdLogged())) {
//            builder.addFilterAddress(device.getAddress());
//        }

        builder.addFilterAddress(heartRateAddress);
        builder.addFilterAddress(scaleAddress);
        builder.addFilterAddress(bloodPressureAddress);
        builder.addFilterAddress(glucoseAdress);

        simpleBleScanner = builder.build();

        simpleBleScanner.startScan(new SimpleScanCallback() {
            @Override
            public void onScanResult(int i, ScanResult scanResult) {
                String address = scanResult.getDevice().getAddress();

                Log.w("Scan", scanResult.getDevice().toString());
//                int type = deviceDAO.get(address, session.getIdLogged()).getTypeId();
//                switch (type) {
//                    case DeviceType
//                            .BLOOD_PRESSURE:
//                        if (bloodPressureManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
//                            bloodPressureManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
//                        break;
//                    case DeviceType
//                            .GLUCOMETER:
//                        if (glucoseManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
//                            glucoseManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
//                        break;
//                    case DeviceType
//                            .BODY_COMPOSITION:
//                        if (scaleManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
//                            scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
//                        break;
//                    case DeviceType
//                            .HEART_RATE:
//                        if (heartRateManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
//                            heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address));
//                        break;
//                }


                switch (address) {
                    case glucoseAdress:
                        glucoseManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(glucoseAdress));
                        break;
                    case heartRateAddress:
                        heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(heartRateAddress));

                        break;
                    case scaleAddress:
                        scaleManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(scaleAddress));

                        break;
                    case bloodPressureAddress:
                        bloodPressureManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bloodPressureAddress));

                        break;
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

        bloodPressureManager = new BloodPressureManager(getContext());
        this.igActivity = new ItemGrid();
        igActivity.setContext(getContext());
        igActivity.setIcon(R.drawable.ic_activity);
        igActivity.setDescription(getResources().getString(R.string.activity));
        igActivity.setMeasurementValue(this.measurementsValues.getActivity());
        igActivity.setType(ItemGridType.ACTIVITY);

        this.igGlucose = new ItemGrid();
        igGlucose.setContext(getContext());
        igGlucose.setIcon(R.drawable.ic_blood_glucose);
        igGlucose.setDescription(getResources().getString(R.string.blood_glucose));
        igGlucose.setMeasurementValue(this.measurementsValues.getGlucose());
        igGlucose.setType(ItemGridType.BLOOD_GLUCOSE);

        this.igPressure = new ItemGrid();
        igPressure.setContext(getContext());
        igPressure.setIcon(R.drawable.ic_blood_pressure_64);
        igPressure.setDescription(getResources().getString(R.string.blood_pressure));
        igPressure.setMeasurementValue(this.measurementsValues.getPressure());
        igPressure.setType(ItemGridType.BLOOD_PRESSURE);

        this.igTemperature = new ItemGrid();
        igTemperature.setContext(getContext());
        igTemperature.setIcon(R.drawable.ic_temperature);
        igTemperature.setDescription(getResources().getString(R.string.temperature));
        igTemperature.setMeasurementValue(this.measurementsValues.getTemperature());
        igTemperature.setType(ItemGridType.TEMPERATURE);

        this.igWeight = new ItemGrid();
        igWeight.setContext(getContext());
        igWeight.setIcon(R.drawable.ic_weight);
        igWeight.setDescription(getResources().getString(R.string.weight));
        igWeight.setMeasurementValue(this.measurementsValues.getWeight());
        igWeight.setType(ItemGridType.WEIGHT);

        this.igSleep = new ItemGrid();
        igSleep.setContext(getContext());
        igSleep.setIcon(R.drawable.ic_sleeping);
        igSleep.setDescription(getResources().getString(R.string.sleep));
        igSleep.setMeasurementValue(this.measurementsValues.getSleep());
        igSleep.setType(ItemGridType.SLEEP);

        this.igHearRate = new ItemGrid();
        igHearRate.setContext(getContext());
        igHearRate.setIcon(R.drawable.ic_heart_rate_64);
        igHearRate.setDescription(getResources().getString(R.string.heart_rate));
        igHearRate.setMeasurementValue(this.measurementsValues.getHeartRate());
        igHearRate.setType(ItemGridType.HEART_RATE);

        this.igAnthropometric = new ItemGrid();
        igAnthropometric.setContext(getContext());
        igAnthropometric.setIcon(R.drawable.ic_ruler_64);
        igAnthropometric.setDescription(getResources().getString(R.string.anthropometric));
        igAnthropometric.setMeasurementValue(this.measurementsValues.getHeight());
        igAnthropometric.setType(ItemGridType.ANTHROPOMETRIC);


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
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        devices = DeviceDAO.getInstance(getContext()).list(session.getIdLogged());

    }

    private void initComponents() {

        session = new Session(getContext());

        updateGrid();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiverMeasurement(Measurement measurement) {

        //measurement.getTypeId();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateStatus(Intent intent) {
        if (intent.getIntExtra("Device", -1) != -1) {
            ItemGrid itemGrid = null;
            switch (intent.getIntExtra("Device", 0)) {
                case MeasurementType
                        .HEART_RATE:
                    itemGrid = igHearRate;
                    break;
                case MeasurementType
                        .BODY_FAT:
                    itemGrid = igWeight;
                    break;
                case MeasurementType
                        .BLOOD_PRESSURE_SYSTOLIC:
                    itemGrid = igPressure;
                    break;
                case MeasurementType
                        .BLOOD_GLUCOSE:
                    itemGrid = igGlucose;
                    break;
                case MeasurementType
                        .TEMPERATURE:
                    itemGrid = igTemperature;
                    break;
            }
            String action = intent.getAction();
            if (itemGrid != null) {
                if (action.equals("Connecting")) {
                    //Em breve
                    //itemGrid.setLoading(true);
                } else if (action.equals("Connected")) {
                    //Em breve
                    //itemGrid.setLoading(false);
                    //itemGrid.setStatus(true);
                } else if (action.equals("Disconnected")) {
                    //Em breve
                    //itemGrid.setLoading(false);
                    //itemGrid.setStatus(false);
                } else if (action.equals("Measurement")) {
                    float value = intent.getFloatExtra("Value", 0);
                    Log.i(TAG, "Setting Measurement " + value);
                    measurementsValues.setTemperature(String.valueOf(value));
                    //updateItemsOfGrid(true, igWeight);
                    updateGrid();
                }
            }
        }
    }


    private void initRecyclerView() {
        mAdapter = new GridDashAdapter(mContext);
        mAdapter.setHasStableIds(true);

        // This method set the same size to all items of grid
        gridMeasurement.setHasFixedSize(true);
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

                Collections.swap(buttonList, viewHolder.getAdapterPosition(), target.getAdapterPosition());
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
        updateGrid();
    }

    private void updateItemsOfGrid(boolean status, ItemGrid item) {
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

        mAdapter.clearItems();
        mAdapter.addItems(buttonList);
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

        }

        @Override
        public void onDisconnected() {

        }

        @Override
        public void onMeasurementReceiver(Measurement measurementScale) {
            igWeight.setMeasurementValue(String.format("%.2f", measurementScale.getValue()));
            mAdapter.notifyDataSetChanged();
        }
    };


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

            igGlucose.setMeasurementValue(String.format("%.2f", measurementGlucose.getValue()));
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
    public void onItemClick(ItemGrid item) {

        if (this.activity.equals(item.getDescription())) {//place activity when implemented

        } else if (this.glucose.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), GlucoseActivity.class));

        } else if (this.pressure.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), BloodPressureHDPActivity.class));

        } else if (this.temperature.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), ThermometerActivity.class));

        } else if (this.weight.equals(item.getDescription())) {

            startActivity(new Intent(getContext(), ScaleActivity.class));

        } else if (this.sleep.equals(item.getDescription())) {
        } else if (this.heartRate.equals(item.getDescription())) {

            Intent intent = new Intent(getContext(), HeartRateActivity.class);
            intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
            intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
            startActivity(intent);

        } else {
        }
    }

    @Override
    public void onLongItemClick(View v, ItemGrid item) {
        throw new UnsupportedOperationException();
    }

    //    Methods to manually add measurement button
    @Override
    public void onMenuContextClick(View v, ItemGrid item) {

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
