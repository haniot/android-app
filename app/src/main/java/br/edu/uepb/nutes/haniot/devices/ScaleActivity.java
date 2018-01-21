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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.graphs.ScaleGraphActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.BodyCompositionAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.User;
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
import br.edu.uepb.nutes.haniot.utils.DateUtils;
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
public class ScaleActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "ScaleActivity";
    private final int LIMIT_PER_PAGE = 20;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private boolean showAnimation = true;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceAddress;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private DecimalFormat decimalFormat;
    private BodyCompositionAdapter mAdapter;
    private Params params;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.body_mass_measurement)
    TextView bodyMassTextView;

    @BindView(R.id.body_mass_unit_measurement)
    TextView bodyMassUnitTextView;

    @BindView(R.id.body_fat_textview)
    TextView bodyFatTextView;

    @BindView(R.id.bmi_textview)
    TextView bmiTextView;

    @BindView(R.id.date_last_measurement_textView)
    TextView mDateLastMeasurement;

    @BindView(R.id.no_data_textView)
    TextView noDataMessage;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.body_composition_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition);
        ButterKnife.bind(this);

        // synchronization with server
        synchronizeWithServer();

        mDeviceAddress = "D4:36:39:91:75:71";
        session = new Session(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));
        params = new Params(session.get_idLogged(), MeasurementType.BODY_MASS);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        initComponents();
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();
        loadData();
    }


    private void initToolBar() {
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.body_weight_scale));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Init RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new BodyCompositionAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setListener(new OnRecyclerViewListener() {
            @Override
            public void onItemClick(Measurement item) {
                Log.w(TAG, "onItemClick()");
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) loadMoreData();
                    }
                }
            }
        });

        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (itShouldLoadMore)
                    loadData();
            }
        });
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mAdapter.addItems(measurementDAO.list(MeasurementType.BODY_MASS, session.getIdLogged(), 0, 100));

        if (!mAdapter.itemsIsEmpty()) {
            updateUILastMeasurement(mAdapter.getFirstItem(), false);
        } else {
            toggleNoDataMessage(true); // Enable message no data
            toggleLoading(false);
        }
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        mAdapter.clearItems();

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            Historical historical = new Historical.Query()
                    .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                    .params(params) // Measurements of the temperature type, associated to the user
                    .pagination(0, LIMIT_PER_PAGE)
                    .build();

            historical.request(this, new CallbackHistorical<Measurement>() {
                @Override
                public void onBeforeSend() {
                    Log.w(TAG, "loadData - onBeforeSend()");
                    toggleLoading(true); // Enable loading
                    toggleNoDataMessage(false); // Disable message no data
                }

                @Override
                public void onError(JSONObject result) {
                    Log.w(TAG, "loadData - onError()");
                    if (!mAdapter.itemsIsEmpty()) printMessage(getString(R.string.error_500));
                    else loadDataLocal();
                }

                @Override
                public void onResult(List<Measurement> result) {
                    Log.w(TAG, "loadData - onResult()");
                    if (result != null && result.size() > 0) {
                        mAdapter.addItems(result);
                        updateUILastMeasurement(mAdapter.getItems().get(0), false);
                    } else {
                        toggleNoDataMessage(true); // Enable message no data
                    }
                }

                @Override
                public void onAfterSend() {
                    Log.w(TAG, "loadData - onAfterSend()");
                    toggleLoading(false); // Disable loading
                }
            });
        }
    }

    /**
     * List more itemsList from the remote server.
     */
    private void loadMoreData() {
        if (!ConnectionUtils.internetIsEnabled(this))
            return;

        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params) // Measurements of the temperature type, associated to the user
                .pagination(mAdapter.getItemCount(), LIMIT_PER_PAGE)
                .build();

        historical.request(this, new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() {
                Log.w(TAG, "loadMoreData - onBeforeSend()");
                toggleLoading(true); // Enable loading
            }

            @Override
            public void onError(JSONObject result) {
                Log.w(TAG, "loadMoreData - onError()");
                printMessage(getString(R.string.error_500));
            }

            @Override
            public void onResult(List<Measurement> result) {
                Log.w(TAG, "loadMoreData - onResult()");
                if (result != null && result.size() > 0) mAdapter.addItems(result);
                else printMessage(getString(R.string.no_more_data));
            }

            @Override
            public void onAfterSend() {
                Log.w(TAG, "loadMoreData - onAfterSend()");
                toggleLoading(false); // Disable loading
            }
        });
    }

    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void toggleLoading(boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!enabled) {
                    mDataSwipeRefresh.setRefreshing(false);
                    itShouldLoadMore = true;
                } else {
                    mDataSwipeRefresh.setRefreshing(true);
                    itShouldLoadMore = false;
                }
            }
        });
    }

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    private void toggleNoDataMessage(boolean visible) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visible) {
                    if (!ConnectionUtils.internetIsEnabled(getApplicationContext())) {
                        noDataMessage.setText(getString(R.string.connect_network_try_again));
                    } else {
                        noDataMessage.setText(getString(R.string.no_data_available));
                    }
                    noDataMessage.setVisibility(View.VISIBLE);
                } else {
                    noDataMessage.setVisibility(View.GONE);
                }
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

    @Override
    protected void onStart() {
        super.onStart();

        // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
        mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

        if (mDevice == null) {
            mDevice = new Device(mDeviceAddress, "YUNMAI SCALE", "YUNMAI", "5031", DeviceType.BODY_COMPOSITION, session.getUserLogged());
            mDevice.set_id("5a62bf80d6f33400146c9b64");
            if (!deviceDAO.save(mDevice)) finish();
            mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonData = new JSONObject(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));

                            final boolean isFinalized = jsonData.getBoolean("isFinalized");
                            final String bodyMassMeasurement = formatNumber(jsonData.getDouble("bodyMass"));
                            final String bodyMassUnit = jsonData.getString("bodyMassUnit");

                            bodyMassTextView.setText(bodyMassMeasurement);
                            bodyMassUnitTextView.setText(bodyMassUnit);

                            if (isFinalized && showAnimation) {
                                showAnimation = false;

                                User user = session.getUserLogged();

                                Measurement bodyMass = JsonToMeasurementParser.bodyMass(jsonData.toString());
                                bodyMass.setUser(user);
                                bodyMass.setDevice(mDevice);

                                Measurement bmi = new Measurement(calcBMI(bodyMass.getValue()),
                                        "kg/m2", bodyMass.getRegistrationDate(), MeasurementType.BMI);
                                bmi.setUser(user);
                                bmi.setDevice(mDevice);

                                Measurement bodyFat = JsonToMeasurementParser.bodyFat(jsonData.toString());
                                bodyFat.setUser(user);
                                bodyFat.setDevice(mDevice);

                                /**
                                 * Add relationships
                                 */
                                bodyMass.addMeasurement(bmi, bodyFat);

                                /**
                                 * Update UI
                                 */
                                updateUILastMeasurement(bodyMass, true);

                                /**
                                 * Save in local
                                 * Send to server saved successfully
                                 */
                                if (measurementDAO.save(bodyMass)) {
                                    synchronizeWithServer();
                                    loadData();
                                }
                            } else {
                                showAnimation = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
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

    /**
     * Return value of BMI.
     * formula: bodyMass(kg)/height(m)^2
     *
     * @param bodyMass double
     * @return double
     */
    private double calcBMI(double bodyMass) {
        double height = (session.getUserLogged().getHeight()) / 100D;
        return bodyMass / (Math.pow(height, 2));
    }

    /**
     * Format value for XX.X
     *
     * @param value double
     * @return String
     */
    private String formatNumber(double value) {
        String result = decimalFormat.format(value);
        return result.equals(".0") ? "00.0" : result;
    }

    /**
     * updateOrSave the UI with the last measurement.
     *
     * @param measurement {@link Measurement}
     */
    private void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDateLastMeasurement.setText(DateUtils.abbreviatedDate(
                        getApplicationContext(), measurement.getRegistrationDate()));
                bodyMassTextView.setText(formatNumber(measurement.getValue()));
                bodyMassUnitTextView.setText(measurement.getUnit());

                /**
                 * Relations
                 */
                for (Measurement m : measurement.getMeasurements()) {
                    if (m.getTypeId() == MeasurementType.BMI)
                        bmiTextView.setText(formatNumber(m.getValue()));
                    else if (m.getTypeId() == MeasurementType.BODY_FAT)
                        bodyFatTextView.setText(formatNumber(m.getValue()));
                }

                if (applyAnimation) bodyMassTextView.startAnimation(animation);
            }
        });
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), ScaleGraphActivity.class));
                break;
            default:
                break;
        }
    }
}