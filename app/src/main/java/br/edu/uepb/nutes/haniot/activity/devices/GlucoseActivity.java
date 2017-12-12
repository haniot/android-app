package br.edu.uepb.nutes.haniot.activity.devices;

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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the glucose data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseActivity extends AppCompatActivity implements View.OnClickListener, ListView.OnItemClickListener {
    private final String LOG = "GlucoseActivity";

    private final int ACTION_ALL_RECORDS = 0;
    private final int ACTION_FIRST_RECORD = 1;
    private final int ACTION_LAST_RECORD = 2;
    private final String LIST_MEASUREMENT = "measurement";
    private final String LIST_TIME_STAMP = "timestamp";

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicMeasurement, characteristicMeasurementContext, characteristicRecordAccess;
    private boolean mConnected = false;
    private BluetoothGattService mGattService = null;
    private List<MeasurementGlucose> mListMeasurementGlucosesContext;

    private Menu mMenu;
    private String mDeviceAddress;
    private Animation animation;
    private SimpleAdapter mAdapterRecords;
    private List<Map<String, String>> mListRecords;
    private int mActionRecords = -1;

    private Device mDevice;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.connection_state)
    TextView mConnectionStateTextView;

    @BindView(R.id.glucose_concentration)
    TextView mGlucoseConcentrationTextView;

    @BindView(R.id.glucose_meal)
    TextView mGlucoseMealTextView;

    @BindView(R.id.button_all_records)
    Button mAllRecordsButton;

    @BindView(R.id.button_first_records)
    Button mFirstRecordButton;

    @BindView(R.id.button_last_records)
    Button mLastRecordButton;

    @BindView(R.id.list_view_glucose_records)
    ListView mRecordsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose);
        ButterKnife.bind(this);
        mDeviceAddress = "00:60:19:60:68:62";
        mDevice = new Device(mDeviceAddress, "Accu-Check Performa Connect");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.glucose_measurement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mConnectionStateTextView.setText(getString(R.string.device_connection_state, getString(R.string.disconnected)));
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        mAllRecordsButton.setOnClickListener(this);
        mFirstRecordButton.setOnClickListener(this);
        mLastRecordButton.setOnClickListener(this);
        mRecordsListView.setOnItemClickListener(this);

        mListRecords = new ArrayList<>();
//        mAdapterRecords = new SimpleAdapter(this, convertToListItems(mDevice.getMeasurements()),
//                android.R.layout.simple_list_item_2, new String[]{LIST_MEASUREMENT, LIST_TIME_STAMP}, new int[]{android.R.id.text1, android.R.id.text2});
//        mRecordsListView.setAdapter(mAdapterRecords);

        mListMeasurementGlucosesContext = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            boolean result = mBluetoothLeService.connect(mDeviceAddress);
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_all_records:
                mAdapterRecords.notifyDataSetChanged();
                mGlucoseConcentrationTextView.setText(getString(R.string.value_default));

                getAllRecords();
                break;
            case R.id.button_first_records:
                getFirstRecord();
                break;
            case R.id.button_last_records:
                getLastRecord();
                break;
            default:
                break;
        }
    }

    private void updateConnectionState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionStateTextView.setText(getString(R.string.device_connection_state, state));
            }
        });
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
        Log.i(LOG, "getAllRecords()");
        if (mGattService == null) return;

        mActionRecords = ACTION_ALL_RECORDS;
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));

        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x01; // all records
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void getFirstRecord() {
        Log.i(LOG, "getFirstRecord()");
        if (mGattService == null) return;

        mActionRecords = ACTION_FIRST_RECORD;
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x05; // first record
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private void getLastRecord() {
        Log.i(LOG, "getLastRecord()");
        if (mGattService == null) return;

        mActionRecords = ACTION_LAST_RECORD;
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x06; // last record
        characteristic.setValue(data);

        setCharacteristicRecordAccess();
        mBluetoothLeService.writeCharacteristic(characteristic);
    }

    private List<Map<String, String>> convertToListItems(List<MeasurementGlucose> measurements) {
        if (measurements != null) {
            for (final MeasurementGlucose measurement : measurements) {
                Map<String, String> listItemMap = new HashMap<>();
                listItemMap.put(LIST_MEASUREMENT, valueToString(measurement));
                listItemMap.put(LIST_TIME_STAMP, DateUtils.getDatetime(measurement.getRegistrationTime(), getString(R.string.datetime_format)) + (measurement.getMeal() == null ? "" : " | " + measurement.getMeal()));

                if (!mListRecords.contains(listItemMap))
                    mListRecords.add(listItemMap);
            }
        }

        return mListRecords;
    }

    private String valueToString(MeasurementGlucose measurement) {
        // TODO CONCERTAR!!!
//        String unit = measurement.getUnit();
//        float value = measurement.getValue();
//
//        if (measurement.getUnit().equals("kg/L")) {
//            unit = "mg/dL";
//            value = value * 100000;
//        }
//
//        String value_formated = String.valueOf(value);
//        if (value > 600) {
//            value_formated = "HI"; // The blood glucose value may be above the reading range of the system.
//        } else if (value < 0) {
//            value_formated = "LO"; // The blood glucose value may be below the reading range of the system.
//        } else {
//            value_formated = String.valueOf((int) value) + unit;
//        }
//
//        return value_formated;
        return null;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(LOG, "Unable to initialize Bluetooth");
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
                updateConnectionState(getString(R.string.connected));
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(getString(R.string.disconnected));
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mGattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_GLUCOSE));
                if (mGattService != null)
                    initCharacteristic();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final MeasurementGlucose measurement = (MeasurementGlucose) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                final MeasurementGlucose measurementContext = (MeasurementGlucose) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA_CONTEXT);

                if (measurementContext != null) {
                    Log.i("CONTEXT", measurementContext.toString());
                    if (!mListMeasurementGlucosesContext.contains(measurementContext)) {
                        mListMeasurementGlucosesContext.add(measurementContext);
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (measurement != null) {
                            String value_formated = valueToString(measurement);

                            relationGlucoseContext(measurement);

                            if (mActionRecords == ACTION_ALL_RECORDS) {
                                Log.i(LOG, "ACTION_ALL_RECORDS");

//                                if (!mDevice.getMeasurements().contains(measurement)) {
//                                    mDevice.addMeasurement(measurement);
//                                    convertToListItems(mDevice.getMeasurements());
//                                    mAdapterRecords.notifyDataSetChanged();
//                                }
                            } else {
                                mActionRecords = -1;
                                mGlucoseConcentrationTextView.setText(value_formated);
                                mGlucoseMealTextView.setText(measurement.getMeal());
                                mGlucoseConcentrationTextView.startAnimation(animation);
                            }

                            Log.i(LOG, measurement.toString());
                        }
                    }
                }, 300);
            }
        }
    };

    private MeasurementGlucose relationGlucoseContext(MeasurementGlucose measurement) {
        for (MeasurementGlucose m : mListMeasurementGlucosesContext) {
            if (m.getSequenceNumber() == measurement.getSequenceNumber()) {
                measurement.setMeal(m.getMeal());
                measurement.setStatusAnnunciation(m.getStatusAnnunciation());

                return measurement;
            }
        }
        return null;
    }

    private void initCharacteristic() {
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        String valueSelected = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
        String valueMeal = ((TextView) view.findViewById(android.R.id.text2)).getText().toString();
        mGlucoseConcentrationTextView.setText(valueSelected);

        String[] temp = valueMeal.split(" ");
        mGlucoseMealTextView.setText(temp.length >= 3 ? temp[3] : "");
    }
}
