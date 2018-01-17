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
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import br.edu.uepb.nutes.haniot.activity.graphs.TemperatureGraphActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.OnLoadMoreListener;
import br.edu.uepb.nutes.haniot.adapter.SeparatorDecoration;
import br.edu.uepb.nutes.haniot.adapter.TemperatureAdapter;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the thermometer data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ThermometerActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "ThermometerActivity";
    private final int LIMIT_PER_PAGE = 5;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceAddress;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private DecimalFormat decimalFormat;
    private TemperatureAdapter mAdapter;
    private List<Measurement> listMeasurements;
    private Params params;
    private Historical historical;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.thermometer_measurement)
    TextView mTemperatureTextView;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.history_temperature_listview)
    RecyclerView mRecyclerView;

    @BindView(R.id.load_data_progressbar)
    ProgressBar mLoadDataProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        ButterKnife.bind(this);
        initializeToolBar();

        // synchronization with server
        synchronizeWithServer();

        mDeviceAddress = "1C:87:74:01:73:10";
        session = new Session(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.temperature_format), new DecimalFormatSymbols(Locale.US));
        params = new Params(session.get_idLogged(), MeasurementType.TEMPERATURE);
        mCircularProgressBar.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        listMeasurements = new ArrayList<>();
        SeparatorDecoration itemDecorator = new SeparatorDecoration(this,
                ResourcesCompat.getColor(getResources(), R.color.colorItemSeparator, null),
                1);

        mAdapter = new TemperatureAdapter(this, listMeasurements);
        mAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                /**
                 * Calling loadMoreData function in Runnable to fix the
                 * java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling error
                 */
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        loadMoreData();
                    }
                });
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setAdapter(mAdapter);

        loadData();
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        listMeasurements.clear();

        if (!ConnectionUtils.internetIsEnabled(this)) {
            for (Measurement m : measurementDAO.list(MeasurementType.TEMPERATURE, session.getIdLogged(), 0, 100)) {
                listMeasurements.add(m);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            Historical historical = new Historical.Query()
                    .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                    .params(params)
                    .pagination(0, LIMIT_PER_PAGE)
                    .build();

            historical.request(this, new CallbackHistorical<Measurement>() {
                @Override
                public void onBeforeSend() {
                    toggleLoadingInitial(true);
                }

                @Override
                public void onError(JSONObject result) {
                    printMessage(getString(R.string.error_500));
                    toggleLoadingInitial(false);
                }

                @Override
                public void onResult(List<Measurement> result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (result != null && result.size() > 0) {
                                listMeasurements.addAll(result);
                                updateUILastMeasurement(listMeasurements.get(0), false);
                            }
                            mAdapter.notifyDataChanged();
                        }
                    });
                }

                @Override
                public void onAfterSend() {
                    toggleLoadingInitial(false);
                }
            });
        }
    }

    /**
     * List more items from the remote server.
     */
    private void loadMoreData() {
        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params)
                .pagination(listMeasurements.size(), LIMIT_PER_PAGE)
                .build();

        historical.request(this, new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() {
                //add loading progress view
                listMeasurements.add(null);
                mAdapter.notifyItemInserted(listMeasurements.size() - 1);
            }

            @Override
            public void onError(JSONObject result) {
                Toast.makeText(getApplicationContext(), R.string.error_500, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResult(List<Measurement> result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null && result.size() > 0) {
                            listMeasurements.addAll(result);
                        } else {
                            mAdapter.setMoreDataAvailable(false);
                            //telling adapter to stop calling loadData loadData as no loadData server data available
                            printMessage(getString(R.string.no_more_data));
                        }
                        mAdapter.notifyDataChanged();
                    }
                });
            }

            @Override
            public void onAfterSend() {
                //remove loading view
                listMeasurements.remove(listMeasurements.size() - 1);
            }
        });
    }

    /**
     * Enable/Disable loading data initial in RecyclerView
     *
     * @param enabled boolean
     */
    private void toggleLoadingInitial(boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (enabled) mLoadDataProgressBar.setVisibility(View.VISIBLE);
                else mLoadDataProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Print Toast Messages.
     *
     * @param message
     */
    private void printMessage(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * initializeToolBar
     */
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.temperature));
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
        mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

        if (mDevice == null) {
            mDevice = new Device(mDeviceAddress, "EAR THERMOMETER", "PHILIPS", "DL8740", DeviceType.THERMOMETER, session.getUserLogged());
            mDevice.set_id("3b4647dfd7bcdd2448000ff5");
            if (!deviceDAO.save(mDevice)) finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
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
                String jsonData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                try {
                    Measurement measurement = JsonToMeasurementParser.temperature(jsonData);
                    measurement.setDevice(mDevice);
                    measurement.setUser(session.getUserLogged());

                    updateUILastMeasurement(measurement, true);

                    /**
                     * Save in local
                     * Send to server saved successfully
                     */
                    if (measurementDAO.save(measurement)) {
                        synchronizeWithServer();
                        loadData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * update the UI with the last measurement.
     *
     * @param m {@link Measurement}
     */
    private void updateUILastMeasurement(Measurement m, boolean applyAnimation) {
        if (m == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTemperatureTextView.setText(decimalFormat.format(m.getValue()));
                if (applyAnimation) mTemperatureTextView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.view_circle:
                startActivity(new Intent(getApplicationContext(), TemperatureGraphActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }
}
