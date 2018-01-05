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
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.BodyCompositionAdapter;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the balance data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ScaleActivity extends AppCompatActivity implements View.OnClickListener, BodyCompositionAdapter.OnItemClickListener {
    private final String TAG = "ScaleActivity";

    private BluetoothLeService mBluetoothLeService;
    private boolean showAnimation = true;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceAddress;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private List<Measurement> measurementBodyCompositionList;
    private RecyclerView.Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.body_mass_measurement)
    TextView mBodyMassMeasurement;

    @BindView(R.id.body_mass_unit_measurement)
    TextView mBodyMassUnitMeasurement;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.history_body_composition_listview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition);
        ButterKnife.bind(this);

        mDeviceAddress = "D4:36:39:91:75:71";
        initializeToolBar();

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        session = new Session(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);

        measurementBodyCompositionList = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new BodyCompositionAdapter(measurementBodyCompositionList, this, this);
        mRecyclerView.setAdapter(mAdapter);

        mCircularProgressBar.setOnClickListener(this);

        SynchronizationServer.getInstance(this).run();
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
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(getString(R.string.scale_measurement));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
        deviceDAO.removeAll(session.getIdLogged());
        if (mDevice == null) {
            mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

            if (mDevice == null) {
                mDevice = new Device(mDeviceAddress, "YUNMAI SCALE", "YUNMAI", "5031", DeviceType.BODY_COMPOSITION, session.getUserLogged());
                mDevice.set_id("3e4647dfd7bcdd2448000ed63");
                if (!deviceDAO.save(mDevice)) finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
        }

        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
//        measurementBodyCompositionList.clear();
//        for (Measurement m : measurementDAO.list(mDeviceAddress, session.getIdLogged(), 0, 10)) {
//            measurementBodyCompositionList.add(m);
//            mAdapter.notifyDataSetChanged();
//        }
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        Log.w(TAG, "setCharacteristicNotification() " + characteristic.getUuid());
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (mNotifyCharacteristic != null) {
                    Log.w(TAG, "PROPERTY_READ");
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                Log.w(TAG, "PROPERTY_NOTIFY");
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(characteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, true);
            }
            return true;
        }
        return false;
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
            // Tenta conecta-se automaticamente ao dispositivo após a inicialização bem-sucedida do service.
            tryingConnect();
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
                Log.i(TAG, "ACTION_GATT_CONNECTED");
                updateConnectionState(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_DISCONNECTED");
                updateConnectionState(false);

                tryingConnect();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.i(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                BluetoothGattService gattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_SCALE));

                if (gattService != null) {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT));
                    if (characteristic != null)
                        setCharacteristicNotification(characteristic);
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    DecimalFormat df = new DecimalFormat(getString(R.string.temperature_format), new DecimalFormatSymbols(Locale.US));
                    final boolean isFinalized = jsonObject.getBoolean("isFinalized");
                    final String bodyMass = df.format(Float.parseFloat(jsonObject.getString("bodyMass")));
                    final String bodyMassUnit = jsonObject.getString("bodyMassUnit");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBodyMassMeasurement.setText(bodyMass.equals(".0") ? "00.0" : bodyMass);
                            mBodyMassUnitMeasurement.setText(bodyMassUnit);

                            /**
                             * Save in local
                             * Send to server saved successfully
                             */
                            if (isFinalized && showAnimation) {
                                showAnimation = false;
                                mBodyMassMeasurement.startAnimation(animation);

                                Measurement measurementBodyMass = jsonToMeasuremntBodyMass(jsonObject);
                                Measurement measurementBodyFat = jsonToMeasuremntBodyFat(jsonObject);
                                measurementBodyMass.addMeasurement(measurementBodyFat);

                                if (measurementDAO.save(measurementBodyMass))
                                    SynchronizationServer.getInstance(getApplicationContext()).run();

                                refreshRecyclerView();
                            } else {
                                showAnimation = true;
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private boolean tryingConnect() {
        Log.i(TAG, "tryingConnect()");
        if (mBluetoothLeService != null) {
            Log.i(TAG, "tryingConnect -- entrou()");
            return mBluetoothLeService.connect(mDeviceAddress);
        }

        return false;
    }

    @Override
    public void onItemClick(Measurement item) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_circle:
                Log.i(TAG, "onClick() - view_circle");
                // Chamada para tela de gráfico

                break;
            default:
                break;
        }
    }

    /**
     * Convert json to Measurement object.
     *
     * @param jsonObject
     * @return Measurement
     */
    private Measurement jsonToMeasuremntBodyMass(JSONObject jsonObject) {
        Measurement measurement = null;

        try {
             measurement = new Measurement(
                     jsonObject.getString("bodyMass"),
                     jsonObject.getString("bodyMassUnit"),
                     jsonObject.getLong("timestamp"),
                     MeasurementType.BODY_MASS);
            measurement.setUser(session.getUserLogged());
            measurement.setDevice(mDevice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measurement;
    }

    /**
     * Convert json to Measurement object.
     *
     * @param jsonObject
     * @return Measurement
     */
    private Measurement jsonToMeasuremntBodyFat(JSONObject jsonObject) {
        Measurement measurement = null;

        try {
            measurement = new Measurement(
                    jsonObject.getString("bodyFat"),
                    jsonObject.getString("bodyFatUnit"),
                    jsonObject.getLong("timestamp"),
                    MeasurementType.BODY_FAT);
            measurement.setUser(session.getUserLogged());
            measurement.setDevice(mDevice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measurement;
    }
}
