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

import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.activity.graphs.TemperatureGraphActivity;
import br.edu.uepb.nutes.haniot.adapter.TemperatureAdapter;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.MeasurementThermometer;
import br.edu.uepb.nutes.haniot.model.dao.DeviceClientDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementThermometerDAO;
import br.edu.uepb.nutes.haniot.model.dao.server.Server;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Headers;

/**
 * Activity to capture the thermometer data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ThermometerActivity extends AppCompatActivity implements TemperatureAdapter.OnItemClickListener {
    private final String LOG = "ThermometerActivity";

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceAddress;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private List<MeasurementThermometer> measurementThermometerList;
    private MeasurementThermometerDAO measurementThermometerDAO;
    private DeviceClientDAO deviceDAO;
    private RecyclerView.Adapter mAdapter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.thermometer_measurement)
    TextView mTemperatureTextView;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.history_temperature_listview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        ButterKnife.bind(this);
        initializeToolBar();

        mDeviceAddress = "1C:87:74:01:73:10";
        session = new Session(this);
        measurementThermometerDAO = MeasurementThermometerDAO.getInstance(this);
        deviceDAO = DeviceClientDAO.getInstance(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        measurementThermometerList = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new TemperatureAdapter(measurementThermometerList, this, this);
        mRecyclerView.setAdapter(mAdapter);
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.temperature_measurement));
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

        if (mDevice == null) {
            mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

            if (mDevice == null) {
                mDevice = new Device(mDeviceAddress, "EAR THERMOMETER", "PHILIPS", "DL8740", 1, session.getIdLogged());
                if (!deviceDAO.save(mDevice)) {
                    finish();
                }
            }
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

    private void refreshRecyclerView() {
        measurementThermometerList.clear();
        for (MeasurementThermometer m : measurementThermometerDAO.list(mDeviceAddress, session.getIdLogged(), 0, 10)) {
            measurementThermometerList.add(m);
            mAdapter.notifyDataSetChanged();
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
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // Se houver uma notificação ativa sobre uma característica, primeiro limpe-a,
                // caso contrário não atualiza o campo de dados na interface do usuário.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
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
                updateConnectionState(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                updateConnectionState(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothGattService gattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER));

                if (gattService != null) {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));
                    if (characteristic != null)
                        setCharacteristicNotification(characteristic);
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final MeasurementThermometer measurement = (MeasurementThermometer) intent.getSerializableExtra(BluetoothLeService.EXTRA_DATA);
                measurement.setRegistrationTime(DateUtils.getCurrentDatetime());

                // display data
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTemperatureTextView.setText(String.valueOf(measurement.getValue()));
                        mTemperatureTextView.startAnimation(animation);

                        /**
                         * Save in local
                         * Send to server saved successfully
                         */
                        measurement.setDeviceAddress(mDevice.getAddress());
                        measurement.setUserId(session.getIdLogged());

                        if (measurementThermometerDAO.save(measurement))
                            sendMeasurementToServer();

                        refreshRecyclerView();
                    }
                });
            }
        }
    };

    /**
     * Send measurement to server.
     *
     */
    private void sendMeasurementToServer() {
        if (!ConnectionUtils.internetIsEnabled(this))
            return;

        List<MeasurementThermometer> measurementThermometerNotSent = measurementThermometerDAO.getNotSent(mDevice.getAddress(), session.getIdLogged());

        /**
         * Get the required user token in request authentication
         */
        Headers headers = new Headers.Builder()
                .add("Authorization", "JWT ".concat(session.getTokenLogged()))
                .build();

        for (final MeasurementThermometer m : measurementThermometerNotSent) {
            JsonObject jsonObject = new JsonObject();
            GsonBuilder gson = new GsonBuilder();

            JsonObject jsonDevice = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(mDevice);
            JsonObject jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);

            /**
             * Removes unnecessary data for server
             */
            jsonMeasurement.remove("hasSent");

            /**
             * Mount the json to send to the server
             */
            jsonObject.add("measurement", jsonMeasurement);
            jsonObject.add("device", jsonDevice);

            Server.getInstance(this).post("healths", jsonObject.toString(), headers, new Server.Callback() {
                @Override
                public void onError(JSONObject result) {
                }

                @Override
                public void onSuccess(JSONObject result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            m.setHasSent(1);
                            measurementThermometerDAO.update(m);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onItemClick(MeasurementThermometer item) {
        Intent it = new Intent(getApplicationContext(), TemperatureGraphActivity.class);
        startActivity(it);
    }
}
