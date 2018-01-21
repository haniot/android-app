package br.edu.uepb.nutes.haniot.devices;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GlucoseAdapter;
import br.edu.uepb.nutes.haniot.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.JsonToContextParser;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the glucose data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseActivity extends AppCompatActivity {
    private final String TAG = "GlucoseActivity";

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicMeasurement, characteristicMeasurementContext, characteristicRecordAccess;
    private boolean mConnected;
    private BluetoothGattService mGattService = null;
    private String mDeviceAddress;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private List<Measurement> measurementList;
    private Measurement glucoseMeasurement;
    private List<ContextMeasurement> contextMeasurements;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private RecyclerView.Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.blood_glucose_measurement)
    TextView bloodGlucoseTextView;

    @BindView(R.id.blood_glucose_measurement_unit)
    TextView bloodGlucoseUnitTextView;

    @BindView(R.id.blood_glucose_context)
    TextView bloodGlucoseContextTextView;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.history_blood_glucose_listview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose);
        ButterKnife.bind(this);
        ButterKnife.bind(this);
        initializeToolBar();

        mDeviceAddress = "00:60:19:60:68:62";
        session = new Session(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

//        measurementList = new ArrayList<>();
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mAdapter = new GlucoseAdapter(measurementList, this, this);
//        mRecyclerView.setAdapter(mAdapter);

        // synchronization with server
        synchronizeWithServer();
    }

    private void initializeToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1)
                    scrollRange = appBarLayout.getTotalScrollRange();

                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(getString(R.string.glucose));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void updateConnectionState(final boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCircularProgressBar.setProgress(0);
                mCircularProgressBar.setProgressWithAnimation(100); // Default animate duration = 1500ms

                if (isConnected) {
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                } else {
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                }
            }
        });
    }

    private void refreshRecyclerView() {
//        measurementList.clear();
//
//        for (Measurement m : measurementDAO.list(MeasurementType.TEMPERATURE, session.getIdLogged(), 0, 20)) {
//            measurementList.add(m);
//            mAdapter.notifyDataSetChanged();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
        mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

        if (mDevice == null) {
            mDevice = new Device(mDeviceAddress, "ACCU-CHEK PERFORMA CONNECT", "ACCU-CHEK", "", DeviceType.GLUCOMETER, session.getUserLogged());
            mDevice.set_id("5a62c1a1d6f33400146c9b68");
            if (!deviceDAO.save(mDevice)) finish();
            mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }

        refreshRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBluetoothLeService.disconnect();
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public void getAllRecords() {
        Log.i(TAG, "getAllRecords()");
        if (mGattService == null) return;

        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));

        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x01; // all records
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void getFirstRecord() {
        Log.i(TAG, "getFirstRecord()");
        if (mGattService == null) return;

        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x05; // first record
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void getLastRecord() {
        Log.i(TAG, "getLastRecord()");
        if (mGattService == null) return;

        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x06; // last record
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Conecta-se automaticamente ao dispositivo após a inicialização bem-sucedida.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * Manipula vários eventos desencadeados pelo Serviço.
     * <p>
     * ACTION_GATT_CONNECTED: conectado a um servidor GATT.
     * ACTION_GATT_DISCONNECTED: desconectado a um servidor GATT.
     * ACTION_GATT_SERVICES_DISCOVERED: serviços GATT descobertos.
     * ACTION_DATA_AVAILABLE: recebeu dados do dispositivo. Pode ser resultado de operações de leitura ou notificação.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mGattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_GLUCOSE));
                if (mGattService != null)
                    initCharacteristics();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String jsonGlucoseData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                String jsonGlucoseContextData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA_CONTEXT);

                try {
                    if (jsonGlucoseData != null)
                        glucoseMeasurement = JsonToMeasurementParser.boodGlucose(jsonGlucoseData);

                    if (jsonGlucoseContextData != null)
                        contextMeasurements = JsonToContextParser.parse(jsonGlucoseData, jsonGlucoseContextData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (glucoseMeasurement != null) {
                            Log.i(TAG, "MEASUREMENT: " + glucoseMeasurement.toString());

                            glucoseMeasurement.setDevice(mDevice);
                            glucoseMeasurement.setUser(session.getUserLogged());

                            /**
                             * Update UI
                             */
                            bloodGlucoseTextView.setText(valueToString(glucoseMeasurement));
                            bloodGlucoseUnitTextView.setText(glucoseMeasurement.getUnit());
                            bloodGlucoseContextTextView.setText(mealToString(contextMeasurements));
                            bloodGlucoseTextView.startAnimation(animation);

                            /**
                             * Add relationships, save and send to server
                             */
                            if (contextMeasurements != null)
                                glucoseMeasurement.addContext(contextMeasurements);

                            if (measurementDAO.save(glucoseMeasurement))
                                synchronizeWithServer();

                            refreshRecyclerView();
                        }
                    }
                }, 300);
            }
        }
    };

    private void initCharacteristics() {
        if (characteristicMeasurement == null) {
            characteristicMeasurement = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT));
            mBluetoothLeService.setCharacteristicNotification(characteristicMeasurement, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, true);
            mBluetoothLeService.readCharacteristic(characteristicMeasurement);
        }

        if (characteristicMeasurementContext == null) {
            characteristicMeasurementContext = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT)); // read
            mBluetoothLeService.setCharacteristicNotification(characteristicMeasurementContext, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, true);
        }
    }

    private void setCharacteristicRecordAccess() {
        if (characteristicRecordAccess == null) {
            characteristicRecordAccess = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL)); // read
            mBluetoothLeService.setCharacteristicNotification(characteristicRecordAccess, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, true);
        }
    }

    /**
     * Recover text from context value meal.
     *
     * @param contextMeasurements
     * @return String
     */
    public String mealToString(List<ContextMeasurement> contextMeasurements) {
        for (ContextMeasurement c : contextMeasurements) {
            Log.i(TAG, "CONTEXTO: " + c.toString());
            if (c.getTypeId() == ContextMeasurementType.GLUCOSE_MEAL)
                return ContextMeasurementValueType.getString(this, c.getValueId());
        }
        return "";
    }

    /**
     * Convert value glucose for String.
     *
     * @param measurement
     * @return String
     */
    public String valueToString(Measurement measurement) {
        String value_formated = "";
        double value = measurement.getValue();

        if (measurement.getUnit().equals("kg/L")) {
            measurement.setUnit(getString(R.string.unit_glucose_mg_dL));
            value = value * 100000;
        }

        if (value > 600) {
            value_formated = "HI"; // The blood glucose value may be above the reading range of the system.
        } else if (value < 0) {
            value_formated = "LO"; // The blood glucose value may be below the reading range of the system.
        } else {
            value_formated = String.format("%02d", (int) value);
        }

        return value_formated;
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

}
