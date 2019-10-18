package br.edu.uepb.nutes.haniot.fragment;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.github.clans.fab.FloatingActionMenu;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.NutritionalEvaluationActivity;
import br.edu.uepb.nutes.haniot.activity.settings.SettingsActivity;
import br.edu.uepb.nutes.haniot.adapter.MeasurementMonitorAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.model.BodyFat;
import br.edu.uepb.nutes.haniot.data.model.model.Device;
import br.edu.uepb.nutes.haniot.data.model.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.model.MeasurementMonitor;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.type.DeviceType;
import br.edu.uepb.nutes.haniot.data.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.type.MeasurementType;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.devices.BloodPressureActivity;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BloodPressureManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.GlucoseManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.HeartRateManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ScaleManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ThermometerManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodGlucoseDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodPressureDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.HeartRateDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.simpleblescanner.SimpleBleScanner;
import br.edu.uepb.nutes.simpleblescanner.SimpleScannerCallback;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * MeasurementsGridFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class MeasurementsGridFragment extends Fragment implements OnRecyclerViewListener<MeasurementMonitor> {
    private final String LOG_TAG = "ManagerDevices";

    private ThermometerManager thermometerManager;
    private ScaleManager scaleManager;
    private HeartRateManager heartRateManager;
    private GlucoseManager glucoseManager;
    private BloodPressureManager bloodPressureManager;
    private MeasurementMonitorAdapter mAdapter;
    private AppPreferencesHelper appPreferencesHelper;
    private User user;
    private String deviceTypeTag;
    private List<MeasurementMonitor> measurementMonitors;
    private Context mContext;
    private SimpleBleScanner simpleBleScanner;
    private DashboardChartsFragment.Communicator communicator;
    private List<Device> devices;
    private SimpleBleScanner.Builder builder;
    private SharedPreferences prefSettings;
    private DecimalFormat decimalFormat;
    private Repository mRepository;
    private PilotStudy pilotStudy;
    private static List<HeartRateItem> heartRateItems;
    private Patient patient;


    @BindView(R.id.gridMeasurement)
    RecyclerView gridMeasurement;
    @BindView(R.id.edit_monitor)
    TextView editMonitor;
    @BindView(R.id.add_monitor)
    TextView addMonitor;
    @BindView(R.id.root)
    RelativeLayout message;

    public MeasurementsGridFragment() {
        // Required empty public constructor
    }

    public static MeasurementsGridFragment newInstance() {
        return new MeasurementsGridFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepository = Repository.getInstance(mContext);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurements_dashboard, container, false);
        ButterKnife.bind(this, view);
        initComponents();
        initRecyclerView();
        refreshManagerBLE();
        return view;
    }

    //TODO Próxima sprint
    private void downloadLastMeasurements() {
        DisposableManager.add(mRepository
                .getAllMeasurementsByType(patient.get_id(), "blood_glucose", "-timestamp", null, null, 1, 1)
                .subscribe(measurements -> {
                    if (!measurements.isEmpty()) {
                        Log.w(LOG_TAG, Arrays.toString(measurements.toArray()));
                        Measurement measurement = measurements.get(0);
                        updateMeasurement(String.valueOf(measurement.getValue()), measurement.getUnit(), measurement.getTimestamp(), ItemGridType.BLOOD_GLUCOSE);
                    }
                }, throwable -> {
                    Log.w(LOG_TAG, throwable.getMessage());
                }));

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionMenu fab = ((MainActivity) getActivity()).patientActionsMenu;
        if (fab == null) {
            Log.w("AAA", "fab null");
            return;
        }

//        gridMeasurement.addOnScrollListener(new RecyclerView.OnScrollListener() {
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                if (dy > 0 || dy < 0 && fab.isShown())
////                    fab.hideMenu(true);
//                    fab.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                Log.w("AAA", "onScrollStateChanged");
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
////                    fab.hideMenu(false);
//                    fab.setVisibility(View.VISIBLE);
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//        });
//        refreshManagerBLE();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;

        try {
            communicator = (DashboardChartsFragment.Communicator) context;
        } catch (ClassCastException ignored) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRegisteredDevices();
        refreshManagerBLE();
        //downloadLastMeasurements();
    }

    @Override
    public void onPause() {
        super.onPause();
        thermometerManager = null;
        scaleManager = null;
        bloodPressureManager = null;
        glucoseManager = null;
        heartRateManager = null;
//        if (simpleBleScanner != null) simpleBleScanner.stopScan();
    }

    /**
     * Start scan device registered.
     */
    private void startScan() {
        if (BluetoothAdapter.getDefaultAdapter() != null
                && BluetoothAdapter.getDefaultAdapter().isEnabled()
                && ((MainActivity) getActivity()).hasLocationPermissions()) {
            simpleBleScanner.startScan(simpleScannerCallback);
        } else if (isAdded() && getActivity() != null) {
//            communicator.showMessageConnection(R.string.bluetooth_disabled);
        }
    }

    /**
     * Callback of scan result of bluetooth devices.
     */
    SimpleScannerCallback simpleScannerCallback = new SimpleScannerCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int i, ScanResult scanResult) {
            String address = scanResult.getDevice().getAddress();
            Device device = getDeviceRegisteredFromAddress(address);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (device != null) {
                switch (device.getType()) {
                    case DeviceType.THERMOMETER:
                        if (thermometerManager != null &&
                                thermometerManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                            thermometerManager.connectDevice(bluetoothAdapter.getRemoteDevice(address));
                        Log.i(LOG_TAG, "Connecting Thermometer...");
                        break;
                    case DeviceType.BLOOD_PRESSURE:
                        if (bloodPressureManager != null &&
                                bloodPressureManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                            bloodPressureManager.connectDevice(bluetoothAdapter.getRemoteDevice(address));
                        Log.i(LOG_TAG, "Connecting Blood Pressure...");
                        break;
                    case DeviceType.GLUCOMETER:
                        if (glucoseManager != null &&
                                glucoseManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                            glucoseManager.connectDevice(bluetoothAdapter.getRemoteDevice(address));
                        Log.i(LOG_TAG, "Connecting Glucometer...");
                        break;
                    case DeviceType.BODY_COMPOSITION:
                        if (scaleManager != null &&
                                scaleManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED) {
                            scaleManager.connectDevice(bluetoothAdapter.getRemoteDevice(address));
                            Log.i(LOG_TAG, "Connecting Scale...");
                        }
                        break;
                    case DeviceType.HEART_RATE:
                        if (heartRateManager != null &&
                                heartRateManager.getConnectionState() != BluetoothGatt.STATE_CONNECTED)
                            heartRateManager.connectDevice(bluetoothAdapter.getRemoteDevice(address));
                        Log.i(LOG_TAG, "Connecting Heart Rate...");
                        break;
                    default:
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
            startScan();
        }
    };

    TemperatureDataCallback temperatureDataCallback = new TemperatureDataCallback() {
        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Connected on Thermometer");
            getMeasurementMonitor(ItemGridType.TEMPERATURE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Disconnected on Thermometer");
            getMeasurementMonitor(ItemGridType.TEMPERATURE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, double temp,
                                          String unit, String timestamp) {
            Log.i(LOG_TAG, "Receiver measurement of Thermometer");
            String result = decimalFormat.format(temp);
            updateMeasurement(result, unit, timestamp, ItemGridType.TEMPERATURE);
            if (temp == 0) return;
            Measurement measurement = new Measurement();
            measurement.setValue(temp);
            measurement.setTimestamp(DateUtils.getCurrentDateTimeUTC());
            measurement.setUserId(patient.get_id());
            measurement.setUnit(unit);
            measurement.setType(MeasurementType.BODY_TEMPERATURE);
//            DeviceOB device1 = getDeviceRegistered(DeviceType.THERMOMETER);
//            if (device1 != null) measurement.setDeviceId(device1.get_id());
            sendMeasurementToServer(measurement);
        }
    };

    /**
     * Data callback of Blood Pressure.
     */
    BloodPressureDataCallback bloodPressureDataCallback = new BloodPressureDataCallback() {
        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Connected on BloodPressure");
            getMeasurementMonitor(ItemGridType.BLOOD_PRESSURE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Disconnected on Blood Pressure");
            getMeasurementMonitor(ItemGridType.BLOOD_PRESSURE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device,
                                          int systolic, int diastolic, int pulse,
                                          String unit, String timestamp) {
            Log.i(LOG_TAG, "Receiver measurement of Blood Pressure");
            String result = String.valueOf(systolic).concat("/").concat(String.valueOf(diastolic));
            updateMeasurement(result, unit, timestamp, ItemGridType.BLOOD_PRESSURE);

            if (systolic == 0 || diastolic == 0) return;

            Measurement measurement = new Measurement();
            measurement.setDiastolic(diastolic);
            measurement.setSystolic(systolic);
            measurement.setTimestamp(timestamp);

            if (pulse > 0) measurement.setPulse(pulse);

            measurement.setUserId(patient.get_id());
            measurement.setUnit(getString(R.string.unit_glucose_mg_dL));

//            DeviceOB device1 = getDeviceRegistered(DeviceType.BLOOD_PRESSURE);
//            if (device1 != null) measurement.setDeviceId(device1.get_id());

            sendMeasurementToServer(measurement);
        }
    };

    /**
     * Data callback of Scale.
     */
    ScaleDataCallback scaleDataCallback = new ScaleDataCallback() {
        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Connected on Scale");
            Objects.requireNonNull(getMeasurementMonitor(ItemGridType.WEIGHT))
                    .setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Disconnected on Scale");
            Objects.requireNonNull(getMeasurementMonitor(ItemGridType.WEIGHT))
                    .setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceiving(double bodyMass, String bodyMassUnit) {
            Log.i(LOG_TAG, "Receiving measurement of Scale");
            String result = decimalFormat.format(bodyMass);
            result = result.equals(".0") ? "00.0" : result;
            Objects.requireNonNull(getMeasurementMonitor(ItemGridType.WEIGHT))
                    .setStatus(MeasurementMonitor.RECEIVING);
            updateMeasurement(result, bodyMassUnit, DateUtils.getCurrentDateTimeUTC(),
                    ItemGridType.WEIGHT);
            communicator.notifyNewMeasurement(result);
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device,
                                          double bodyMass, String bodyMassUnit,
                                          double bodyFat, String timestamp) {
            Log.i(LOG_TAG, "Receiver measurement of Scale");

            String result = decimalFormat.format(bodyMass);
            result = result.equals(".0") ? "00.0" : result;
            updateMeasurement(result, bodyMassUnit, timestamp, ItemGridType.WEIGHT);
            communicator.notifyNewMeasurement(result);

            if (bodyMass == 0) return;

            Log.i(LOG_TAG, "bodyMass > 0");
            Measurement measurement = new Measurement();
            measurement.setValue(bodyMass);

            if (bodyFat > 0) {
                List<BodyFat> bodyFats = new ArrayList<>();
                BodyFat bodyFat1 = new BodyFat();
                bodyFat1.setValue(bodyFat);
                bodyFat1.setTimestamp(timestamp);
                measurement.setBodyFat(bodyFats);
            }
            measurement.setType(MeasurementType.BODY_MASS);
            measurement.setTimestamp(timestamp);
            measurement.setUserId(patient.get_id());
            measurement.setUnit((bodyMassUnit != null && bodyMassUnit.equals("")) ? "kg" : bodyMassUnit);

//            DeviceOB device1 = getDeviceRegistered(DeviceType.BODY_COMPOSITION);
//            if (device1 != null) measurement.setDeviceId(device1.get_id());

            sendMeasurementToServer(measurement);
        }
    };

    /**
     * Data callback of Glucose.
     */
    BloodGlucoseDataCallback glucoseDataCallback = new BloodGlucoseDataCallback() {
        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Connected on Glucose");
            getMeasurementMonitor(ItemGridType.BLOOD_GLUCOSE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Disconnected on Glucose");
            getMeasurementMonitor(ItemGridType.BLOOD_GLUCOSE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, int glucose,
                                          String meal, String timestamp) {
            Log.i(LOG_TAG, "Receiver measurement of Glucose");
            updateMeasurement(String.valueOf(glucose), meal, timestamp, ItemGridType.BLOOD_GLUCOSE);

            if (glucose == 0) return;

            Measurement measurement = new Measurement();
            measurement.setValue(glucose);
            measurement.setMeal(meal);
            measurement.setTimestamp(timestamp);
            measurement.setType(MeasurementType.BLOOD_GLUCOSE);
            measurement.setUserId(patient.get_id());
            measurement.setUnit(getString(R.string.unit_glucose_mg_dL));

//            DeviceOB device1 = getDeviceRegistered(DeviceType.GLUCOMETER);
//            if (device1 != null) measurement.setDeviceId(device1.get_id());

            sendMeasurementToServer(measurement);
        }
    };

    /**
     * Data callback Heart Rate.
     */
    HeartRateDataCallback heartRateDataCallback = new HeartRateDataCallback() {
        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Connected on Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.CONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(LOG_TAG, "Disconnected on Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.DISCONNECTED);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, int heartRate, String timestamp) {
            Log.i(LOG_TAG, "Receiver measurement of Heart Rate");
            getMeasurementMonitor(ItemGridType.HEART_RATE).setStatus(MeasurementMonitor.CONNECTED);
            updateMeasurement(String.valueOf(heartRate), "bpm", timestamp, ItemGridType.HEART_RATE);

            if (heartRate == 0) return;

            HeartRateItem heartRateItem = new HeartRateItem();
            heartRateItem.setValue(heartRate);
            heartRateItem.setTimestamp(timestamp);
            heartRateItems.add(heartRateItem);
        }
    };

    private void sendMeasurementToServer(Measurement measurement) {
        Log.i(LOG_TAG, "Saving " + measurement.toJson());
        DisposableManager.add(mRepository
                .saveMeasurement(measurement)
                .doAfterSuccess(measurement1 -> {
                    Log.i(LOG_TAG, "Saved " + measurement1.toJson());
                })
                .subscribe(measurement1 -> {
                    showToast(getString(R.string.measurement_save));
                }, this::errorHandler));
    }

    /**
     * Save collected list of HeartRate measurements.
     */
    public void saveHeartRateCollection() {

        if (!prefSettings.getBoolean(getResources().getString(R.string.key_heart_rate), false)) {
            Intent intent = new Intent(getContext(), NutritionalEvaluationActivity.class);
            intent.putExtra("type", "nutrition");
            startActivity(intent);
        }

        ProgressDialog dialog = ProgressDialog.show(getContext(), getString(R.string.title_synchronize),
                getString(R.string.loading_synchronize), true);
        dialog.show();

        Measurement measurement = new Measurement();
        measurement.setUserId(patient.get_id());
        measurement.setUnit("bpm");
        measurement.setType(MeasurementType.HEART_RATE);
        measurement.setDataset(heartRateItems);

//        DeviceOB device1 = getDeviceRegistered(DeviceType.HEART_RATE);
//        if (device1 != null) measurement.setDeviceId(device1.get_id());

//        DisposableManager.add(mRepository
//        .getAllMeasurementsByType(patient.get_id(), MeasurementType.HEART_RATE, null, null, null, 1, 10000));

        List<Measurement> measurements = mRepository.listMeasurements(MeasurementType.HEART_RATE, patient.get_id(), 100, 1000);
        if (measurements == null) measurements = new ArrayList<>();
        measurements.add(measurement);

        DisposableManager.add(mRepository
                .saveMeasurement(measurements)
                .doAfterTerminate(() -> {
                    dialog.cancel();
                    Intent intent = new Intent(getContext(), NutritionalEvaluationActivity.class);
                    intent.putExtra("type", "nutrition");
                    startActivity(intent);
                })
                .subscribe(measurement1 -> {
                    Log.w(LOG_TAG, measurement1.toString());
                    heartRateItems.clear();
                    mRepository.removeAllMeasurements(patient.get_id());
                }, throwable -> {
                    Log.w(LOG_TAG, throwable.getMessage());
                    mRepository.saveMeasurement(measurement);
                }));
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        showToast(getContext()
                .getResources()
                .getString(R.string.error_500));
    }

    /**
     * Show message in Toast.
     *
     * @param menssage
     */
    private void showToast(final String menssage) {
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            Toast toast = Toast.makeText(getContext(), menssage, Toast.LENGTH_LONG);
            toast.show();
        });
    }


    /**
     * Update measurement in grid.
     *
     * @param value     MeasurementOB value
     * @param unit      MeasurementOB unit
     * @param timestamp Datetime of collection.
     * @param type      Type measurement in grid
     */
    private void updateMeasurement(String value, String unit, String timestamp, int type) {
        if (isAdded()) {
            new Handler().post(() -> {
                for (MeasurementMonitor measurementMonitor : measurementMonitors) {
                    if (measurementMonitor.getType() == type) {
                        measurementMonitor.setMeasurementValue(value);
                        measurementMonitor.setTime(DateUtils.convertDateTimeUTCToLocale(timestamp, getString(R.string.time_format_simple)));
                        mAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            });
        }
    }

    /**
     * Get device registered from type.
     *
     * @param type {@link DeviceType}
     * @return DeviceOB
     */
    private Device getDeviceRegistered(String type) {

        for (Device device1 : devices)
            if (device1.getType().equals(type)) return device1;
        return null;
    }

    private Device getDeviceRegisteredFromAddress(String address) {

        for (Device device1 : devices)
            if (device1.getAddress().equals(address)) return device1;
        return null;
    }

    /**
     * Setup monitor item.
     *
     * @param type
     */
    private void setupMonitorItem(int type) {
        String deviceType = "";
        MeasurementMonitor measurementMonitor = null;
        if (type == R.string.key_temperature) {
            deviceType = DeviceType.THERMOMETER;
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.ic_action_thermometer,
                    getResources().getString(R.string.temperature),
                    "", ItemGridType.TEMPERATURE,
                    getString(R.string.unit_celsius));
        } else if (type == R.string.key_weight) {
            deviceType = DeviceType.BODY_COMPOSITION;
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.xweight,
                    getResources().getString(R.string.weight),
                    "", ItemGridType.WEIGHT,
                    getString(R.string.unit_kg));
        } else if (type == R.string.key_heart_rate) {
            deviceType = DeviceType.HEART_RATE;
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.xcardiogram,
                    getResources().getString(R.string.heart_rate),
                    "", ItemGridType.HEART_RATE,
                    getString(R.string.unit_heart_rate));
        } else if (type == R.string.key_blood_glucose) {
            deviceType = DeviceType.GLUCOMETER;
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.xglucosemeter,
                    getResources().getString(R.string.blood_glucose),
                    "", ItemGridType.BLOOD_GLUCOSE,
                    getString(R.string.unit_glucose_mg_dL));
        } else if (type == R.string.key_blood_pressure) {
            deviceType = DeviceType.BLOOD_PRESSURE;
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.xblood_pressure,
                    getResources().getString(R.string.blood_pressure),
                    "", ItemGridType.BLOOD_PRESSURE,
                    getString(R.string.unit_pressure));
        } else if (type == R.string.key_anthropometric) {
            measurementMonitor = new MeasurementMonitor(
                    mContext, R.drawable.xshape,
                    getResources().getString(R.string.anthropometric),
                    "", ItemGridType.ANTHROPOMETRIC,
                    getString(R.string.unit_percentage));
        }
        if (measurementMonitor != null) {
            if (getDeviceRegistered(deviceType) != null)
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
            measurementMonitors.add(measurementMonitor);
        }
    }

    private void refreshRegisteredDevices() {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            String deviceType = "";
            switch (measurementMonitor.getType()) {
                case ItemGridType.TEMPERATURE:
                    deviceType = DeviceType.THERMOMETER;
                    break;
                case ItemGridType.BLOOD_GLUCOSE:
                    deviceType = DeviceType.GLUCOMETER;
                    break;
                case ItemGridType.BLOOD_PRESSURE:
                    deviceType = DeviceType.BLOOD_PRESSURE;
                    break;
                case ItemGridType.HEART_RATE:
                    deviceType = DeviceType.HEART_RATE;
                    break;
                case ItemGridType.WEIGHT:
                    deviceType = DeviceType.BODY_COMPOSITION;
                    break;
            }
            if (getDeviceRegistered(deviceType) != null)
                measurementMonitor.setStatus(MeasurementMonitor.DISCONNECTED);
            else measurementMonitor.setStatus(MeasurementMonitor.NO_REGISTERED);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Refresh BLE Manager of devices for monitoring.
     */
    public void refreshManagerBLE() {
        measurementMonitors.clear();
        if (prefSettings.getBoolean(getResources().getString(R.string.key_temperature), false)) {
            if (thermometerManager == null) {
                thermometerManager = new ThermometerManager(mContext);
                thermometerManager.setSimpleCallback(temperatureDataCallback);
                Device device = getDeviceRegistered(DeviceType.THERMOMETER);
                if (device != null) builder.addFilterAddress(device.getAddress());
            }
            setupMonitorItem(R.string.key_temperature);
        }
        if (prefSettings.getBoolean(getResources().getString(R.string.key_weight), false)) {
            if (scaleManager == null) {
                scaleManager = new ScaleManager(mContext);
                scaleManager.setSimpleCallback(scaleDataCallback);
                Device device = getDeviceRegistered(DeviceType.BODY_COMPOSITION);
                if (device != null) builder.addFilterAddress(device.getAddress());
            }
            setupMonitorItem(R.string.key_weight);
        }
        if (prefSettings.getBoolean(getResources().getString(R.string.key_heart_rate), false)) {
            if (heartRateManager == null) {
                heartRateManager = new HeartRateManager(mContext);
                heartRateManager.setSimpleCallback(heartRateDataCallback);
                Device device = getDeviceRegistered(DeviceType.HEART_RATE);
                if (device != null) builder.addFilterAddress(device.getAddress());
            }
            setupMonitorItem(R.string.key_heart_rate);
        }
        if (prefSettings.getBoolean(getResources().getString(R.string.key_blood_glucose), false)) {
            if (glucoseManager == null) {
                glucoseManager = new GlucoseManager(mContext);
                glucoseManager.setSimpleCallback(glucoseDataCallback);
                Device device = getDeviceRegistered(DeviceType.GLUCOMETER);
                if (device != null) builder.addFilterAddress(device.getAddress());
            }
            setupMonitorItem(R.string.key_blood_glucose);
        }
        if (prefSettings.getBoolean(getResources().getString(R.string.key_blood_pressure), false)) {
            if (bloodPressureManager == null) {
                bloodPressureManager = new BloodPressureManager(mContext);
                bloodPressureManager.setSimpleCallback(bloodPressureDataCallback);
                Device device = getDeviceRegistered(DeviceType.BLOOD_PRESSURE);
                if (device != null) builder.addFilterAddress(device.getAddress());
            }
            setupMonitorItem(R.string.key_blood_pressure);
        }
        if (prefSettings.getBoolean(getResources().getString(R.string.key_anthropometric), false))
            setupMonitorItem(R.string.key_anthropometric);

        builder.addScanPeriod(Integer.MAX_VALUE);
        refreshListMonitor();
        simpleBleScanner = builder.build();
        startScan();
    }

    /**
     * Update monitor list.
     */
    private void refreshListMonitor() {
        mAdapter.clearItems();
        mAdapter.addItems(measurementMonitors);

        if (measurementMonitors.isEmpty()) {
            message.setVisibility(View.VISIBLE);
            editMonitor.setVisibility(View.INVISIBLE);
        } else {
            message.setVisibility(View.INVISIBLE);
            editMonitor.setVisibility(View.VISIBLE);
        }
    }

    /**
     * On click of Monitor Edit.
     */
    private View.OnClickListener editMonitorClick = v -> {
        Intent it = new Intent(mContext, SettingsActivity.class);
        it.putExtra(SettingsActivity.SETTINGS_TYPE, SettingsActivity.SETTINGS_MEASUREMENTS);
        startActivity(it);
    };

    /**
     * Init basics components.
     */
    private void initComponents() {
        decimalFormat = new DecimalFormat(getString(R.string.format_number2),
                new DecimalFormatSymbols(Locale.US));
        prefSettings = PreferenceManager.getDefaultSharedPreferences(mContext);
        mAdapter = new MeasurementMonitorAdapter(mContext);
        measurementMonitors = new ArrayList<>();
        appPreferencesHelper = AppPreferencesHelper.getInstance(mContext);
        user = appPreferencesHelper.getUserLogged();
        patient = appPreferencesHelper.getLastPatient();
//        deviceDAO = DeviceDAO.getInstance(mContext);
//        Log.w("AAA", Arrays.toString(deviceDAO.list(user.get_id()).toArray()));

        DisposableManager.add(
                mRepository.getAllDevices(user.get_id())
                        .subscribe(devices1 -> devices = devices1));

        builder = new SimpleBleScanner.Builder();
        pilotStudy = appPreferencesHelper.getLastPilotStudy();
        if (heartRateItems == null) heartRateItems = new ArrayList<>();

        editMonitor.setOnClickListener(editMonitorClick);
        addMonitor.setOnClickListener(editMonitorClick);
    }

    /**
     * Init recycler view of list devices.
     */
    private void initRecyclerView() {
        mAdapter.setHasStableIds(true);
        gridMeasurement.setHasFixedSize(true);

        deviceTypeTag = gridMeasurement.getTag().toString();
        if (deviceTypeTag.equals("tablet")) {
            gridMeasurement.setLayoutManager(new GridLayoutManager(mContext,
                    calculateNoOfColumns(mContext)));
        } else {
            gridMeasurement.setLayoutManager(new LinearLayoutManager(mContext));
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
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                Collections.swap(measurementMonitors, viewHolder.getAdapterPosition(), target.getAdapterPosition());
                mAdapter.notifyItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                        RecyclerView.ViewHolder viewHolder) {
                if (deviceTypeTag.equals("tablet")) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                            ItemTouchHelper.DOWN | ItemTouchHelper.UP |
                                    ItemTouchHelper.START | ItemTouchHelper.END);
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
     * @param context {@link Context}
     * @return int
     */
    public int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = 1;

        if (this.deviceTypeTag.equals("tablet")) {
            // 260 is the size for tablets
            noOfColumns = (int) (dpWidth / 260);
        } else {
            // 160 is the size for smart phones
            noOfColumns = (int) (dpWidth / 160);
        }
        return noOfColumns;
    }

    /**
     * Get MeasurementOB Monitor item.
     *
     * @param type int
     * @return MeasurementMonitor
     */
    private MeasurementMonitor getMeasurementMonitor(int type) {
        for (MeasurementMonitor measurementMonitor : measurementMonitors) {
            if (measurementMonitor.getType() == type) {
                return measurementMonitor;
            }
        }
        return null;
    }

    /**
     * On click item of measurement monitor list.
     *
     * @param item {@link MeasurementMonitor}
     */
    @Override
    public void onItemClick(MeasurementMonitor item) {
        switch (item.getType()) {
            case ItemGridType.TEMPERATURE:
                if (thermometerManager != null) thermometerManager.close();
                startActivity(new Intent(mContext, ThermometerActivity.class));
                break;
            case ItemGridType.BLOOD_GLUCOSE:
                if (glucoseManager != null) glucoseManager.close();
                startActivity(new Intent(mContext, GlucoseActivity.class));
                break;
            case ItemGridType.HEART_RATE:
                if (heartRateManager != null) heartRateManager.close();
                startActivity(new Intent(mContext, HeartRateActivity.class));
                break;
            case ItemGridType.WEIGHT:
                if (scaleManager != null) scaleManager.close();
                startActivity(new Intent(mContext, ScaleActivity.class));
                break;
            case ItemGridType.BLOOD_PRESSURE:
                startActivity(new Intent(mContext, BloodPressureActivity.class));
                break;
        }
    }

    @Override
    public void onLongItemClick(View v, MeasurementMonitor item) {
    }

    @Override
    public void onMenuContextClick(View v, MeasurementMonitor item) {
        int type = item.getType();
        Intent it = new Intent(mContext, AddMeasurementActivity.class);

        switch (type) {
            case ItemGridType.TEMPERATURE:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType),
                                ItemGridType.TEMPERATURE);
                startActivity(it);
                break;
            case ItemGridType.BLOOD_GLUCOSE:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_GLUCOSE);
                startActivity(it);
                break;
            case ItemGridType.BLOOD_PRESSURE:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;
            case ItemGridType.WEIGHT:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.WEIGHT);
                startActivity(it);
                break;
            case ItemGridType.HEART_RATE:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.HEART_RATE);
                startActivity(it);
                break;
            case ItemGridType.ANTHROPOMETRIC:
                appPreferencesHelper
                        .saveInt(getResources().getString(R.string.measurementType), ItemGridType.ANTHROPOMETRIC);
                startActivity(it);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemSwiped(MeasurementMonitor item, int position) {

    }
}