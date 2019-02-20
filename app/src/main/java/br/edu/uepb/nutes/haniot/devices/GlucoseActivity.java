package br.edu.uepb.nutes.haniot.devices;

import android.app.ProgressDialog;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.charts.GlucoseChartActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.GlucoseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.JsonToContextParser;
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
 * Activity to capture the glucose data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "GlucoseActivity";
    private final int LIMIT_PER_PAGE = 20;

    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattCharacteristic characteristicMeasurement,
            characteristicMeasurementContext, characteristicRecordAccess;
    private boolean mConnected = false;

    private BluetoothGattService mGattService;
    private Animation animation;
    private Device mDevice;
    private Session session;
    private Measurement glucose;
    private List<ContextMeasurement> contextMeasurements;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private GlucoseAdapter mAdapter;
    private Params params;
    private String jsonGlucoseData;
    private String jsonGlucoseContextData;
    private String action;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.glucose_textview)
    TextView mBloodGlucoseTextView;

    @BindView(R.id.unit_glucose_textview)
    TextView mUnitBloodGlucoseTextView;

    @BindView(R.id.date_last_measurement_textView)
    TextView mDateLastMeasurement;

    @BindView(R.id.no_data_textView)
    TextView noDataMessage;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.glucose_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddButton;

    private ProgressDialog progressDialog;
    private boolean isGetAllMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose);
        ButterKnife.bind(this);

        // synchronization with server
        synchronizeWithServer();

        session = new Session(this);
        for (Device device : DeviceDAO.getInstance(this).list(session.getIdLogged()))
            if (device.getTypeId() == DeviceType.GLUCOMETER) mDevice = device;

        if (mDevice == null) Log.i(TAG, "No device registered");
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        params = new Params(session.get_idLogged(), MeasurementType.BLOOD_GLUCOSE);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        isGetAllMonitor = false;
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

    /**
     * Initialize ToolBar
     */
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

    /**
     * Init RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = new GlucoseAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setListener(new OnRecyclerViewListener<Measurement>() {
            @Override
            public void onItemClick(Measurement item) {
            }

            @Override
            public void onLongItemClick(View v, Measurement item) {

            }

            @Override
            public void onMenuContextClick(View v, Measurement item) {

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    mAddButton.hide();
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) loadMoreData();
                    }
                } else {
                    mAddButton.show();
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
                if (itShouldLoadMore) loadData();
            }
        });
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mAdapter.addItems(measurementDAO.list(MeasurementType.BLOOD_GLUCOSE, session.getIdLogged(), 0, 100));

        if (!mAdapter.itemsIsEmpty()) {
            updateUILastMeasurement(mAdapter.getFirstItem(), false);
        } else {
            toggleNoDataMessage(true); // Enable message no data
        }
        toggleLoading(false);
    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     */
    private void loadData() {
        mAdapter.clearItems(); // clear list

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            Historical historical = new Historical.Query()
                    .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                    .params(params) // Measurements of the blood glucose type, associated to the user
                    .pagination(0, LIMIT_PER_PAGE)
                    .build();

            historical.request(this, new CallbackHistorical<Measurement>() {
                @Override
                public void onBeforeSend() {
                    toggleLoading(true); // Enable loading
                    toggleNoDataMessage(false); // Disable message no data
                }

                @Override
                public void onError(JSONObject result) {
                    if (mAdapter.itemsIsEmpty()) printMessage(getString(R.string.error_500));
                    else loadDataLocal();
                }

                @Override
                public void onResult(List<Measurement> result) {
                    if (result != null && result.size() > 0) {
                        mAdapter.addItems(result);
                        updateUILastMeasurement(mAdapter.getFirstItem(), false);
                    } else {
                        toggleNoDataMessage(true); // Enable message no data
                    }
                }

                @Override
                public void onAfterSend() {
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
                .params(params) // Measurements of the blood glucose type, associated to the user
                .pagination(mAdapter.getItemCount(), LIMIT_PER_PAGE)
                .build();

        historical.request(this, new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() {
                toggleLoading(true); // Enable loading
            }

            @Override
            public void onError(JSONObject result) {
                printMessage(getString(R.string.error_500));
            }

            @Override
            public void onResult(List<Measurement> result) {
                if (result != null && result.size() > 0) mAdapter.addItems(result);
                else printMessage(getString(R.string.no_more_data));
            }

            @Override
            public void onAfterSend() {
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

    /**
     * @param isConnected
     */
    private void updateConnectionState(final boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && !isConnected && isGetAllMonitor) {
                    isGetAllMonitor = false;
                    progressDialog.dismiss();
                    synchronizeWithServer();
                    loadData();
                }
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null && mDevice != null) mBluetoothLeService.connect(mDevice.getAddress());
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
            case 0:
                getMonitor();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getMonitor() {
        progressDialog = ProgressDialog.show(this, getString(R.string.get_monitor_dialog_tittle), getString(R.string.get_monitor_desc), true);
        progressDialog.setCancelable(true);
        progressDialog.show();

        new Handler().postDelayed(() -> getAllRecords(), 10000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem menuItem = menu.add(0, 0, 0, getString(R.string.get_monitor));
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);

        return true;
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
        if (mGattService == null) return;

        isGetAllMonitor = true;
        BluetoothGattCharacteristic characteristic = mGattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));

        byte[] data = new byte[2];
        data[0] = 0x01; // Report Stored records
        data[1] = 0x01; // all records
        characteristic.setValue(data);

        mBluetoothLeService.writeCharacteristic(characteristic);
        setCharacteristicRecordAccess();
    }

    private void getFirstRecord() {
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
                finish();
            }
            // Conecta-se automaticamente ao dispositivo após a inicialização bem-sucedida.
            if (mDevice != null)
            mBluetoothLeService.connect(mDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    public String getAction() {
        return action;
    }


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
            action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                mGattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_GLUCOSE));
                if (mGattService != null) {
                    initCharacteristics();
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA) != null)
                    jsonGlucoseData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                if (intent.getStringExtra(BluetoothLeService.EXTRA_DATA_CONTEXT) != null)
                    jsonGlucoseContextData = intent.getStringExtra(BluetoothLeService.EXTRA_DATA_CONTEXT);

                try {
                    if (jsonGlucoseData != null)
                        glucose = JsonToMeasurementParser.bloodGlucose(jsonGlucoseData);

                    if (jsonGlucoseContextData != null && jsonGlucoseData != null)
                        contextMeasurements = JsonToContextParser.parse(jsonGlucoseData, jsonGlucoseContextData);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (glucose != null) {

                                glucose.setDevice(mDevice);
                                glucose.setUser(session.getUserLogged());

                                /**
                                 * Add relationships
                                 */
                                if (contextMeasurements != null)
                                    glucose.addContext(contextMeasurements);

                                /**
                                 * Update UI
                                 */
                                updateUILastMeasurement(glucose, true);

                                /**
                                 * Save in local
                                 * Send to server saved successfully
                                 */
                                if (measurementDAO.save(glucose)) {
                                    if (!isGetAllMonitor) {
                                        synchronizeWithServer();
                                        loadData();
                                    }
                                }
                            }
                        }
                    }, 300);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
     * updateOrSave the UI with the last measurement.
     *
     * @param measurement {@link Measurement}
     */
    private void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(() -> {
            mBloodGlucoseTextView.setText(valueToString(measurement));
            mUnitBloodGlucoseTextView.setText(measurement.getUnit());
            mDateLastMeasurement.setText(DateUtils.abbreviatedDate(
                    getApplicationContext(), measurement.getRegistrationDate()));

            if (applyAnimation) mBloodGlucoseTextView.startAnimation(animation);
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
                startActivity(new Intent(getApplicationContext(), GlucoseChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), ManuallyAddMeasurement.class);
                it.putExtra(getResources().getString(R.string.measurementType),
                        ItemGridType.BLOOD_GLUCOSE);
                startActivity(it);
                break;
            default:
                break;
        }
    }
}
