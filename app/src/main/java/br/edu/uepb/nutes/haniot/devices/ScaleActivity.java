package br.edu.uepb.nutes.haniot.devices;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.BodyCompositionChartActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.BodyCompositionAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ScaleManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
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
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;
    private final int LIMIT_PER_PAGE = 20;

    private boolean mConnected = false;
    private boolean showAnimation = true;
    private Animation animation;
    private Device mDevice;
    private AppPreferencesHelper appPreferencesHelper;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private DecimalFormat decimalFormat;
    private BodyCompositionAdapter mAdapter;
    private Params params;
    private ScaleManager scaleManager;
    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.body_mass_textview)
    TextView bodyMassTextView;

    @BindView(R.id.unit_body_mass_textview)
    TextView bodyMassUnitTextView;

    @BindView(R.id.title_body_fat_textview)
    TextView titleBodyFatTextView;

    @BindView(R.id.body_fat_textview)
    TextView bodyFatTextView;

    @BindView(R.id.unit_body_fat_textview)
    TextView unitBodyFatTextView;

    @BindView(R.id.title_bmi_fat_textview)
    TextView titleBmiTextView;

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

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddButton;

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition);
        ButterKnife.bind(this);
        checkPermissions();

        // synchronization with server
        synchronizeWithServer();

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));
        params = new Params(appPreferencesHelper.getUserLogged().get_id(), MeasurementType.BODY_MASS);
        scaleManager = new ScaleManager(this);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        mDevice = deviceDAO.getByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.BODY_COMPOSITION);
        messageError.setOnClickListener(v -> checkPermissions());

        scaleManager.setSimpleCallback(scaleDataCallback);
        initComponents();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    ScaleDataCallback scaleDataCallback = new ScaleDataCallback() {
        @Override
        public void onConnected() {
            mConnected = true;
            updateConnectionState(true);
        }

        @Override
        public void onDisconnected() {
            mConnected = false;
            updateConnectionState(false);
        }

        @Override
        public void onMeasurementReceived(Measurement measurementScale) {

            if (mDevice != null)
                measurementScale.setDevice(mDevice);

            /**
             * Save in local
             * Send to server saved successfully
             */
            if (measurementDAO.save(measurementScale)) {
                synchronizeWithServer();
                loadData();
            }
            updateUILastMeasurement(measurementScale, true);
        }

        @Override
        public void onMeasurementReceiving(String bodyMassMeasurement, long timeStamp, String bodyMassUnit) {
            runOnUiThread(() -> {
                bodyMassTextView.setText(bodyMassMeasurement);
                bodyMassUnitTextView.setText(bodyMassUnit);
            });
        }
    };

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

        mAdapter.setListener(new OnRecyclerViewListener<Measurement>() {
            @Override
            public void onItemClick(Measurement item) {
                Log.w(TAG, "onItemClick()");
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
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            if (itShouldLoadMore)
                loadData();
        });
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mAdapter.addItems(measurementDAO.list(MeasurementType.BODY_MASS, appPreferencesHelper.getUserLogged().getId(), 0, 100));

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
        mAdapter.clearItems();

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            Historical historical = new Historical.Query()
                    .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                    .params(params) // Measurements of the body mass type, associated to the user
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
                .params(params) // Measurements of the body mass type, associated to the user
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
        runOnUiThread(() -> {
            if (!enabled) {
                mDataSwipeRefresh.setRefreshing(false);
                itShouldLoadMore = true;
            } else {
                mDataSwipeRefresh.setRefreshing(true);
                itShouldLoadMore = false;
            }
        });
    }

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    private void toggleNoDataMessage(boolean visible) {
        runOnUiThread(() -> {
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
        });
    }

    /**
     * Print Toast Messages.
     *
     * @param message
     */
    private void printMessage(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    private void showMessage(@StringRes int str) {
        if (str != -1) {
            String message = getString(str);

            messageError.setText(message);
            runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                boxMessage.setVisibility(View.VISIBLE);
            });
        } else {
            runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                boxMessage.setVisibility(View.INVISIBLE);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDevice != null)
            scaleManager.connect(BluetoothAdapter.getDefaultAdapter()
                    .getRemoteDevice(mDevice.getAddress())).useAutoConnect(true).enqueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);

        if (scaleManager.getConnectionState() != BluetoothProfile.STATE_CONNECTED && mDevice != null) {
            scaleManager.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                scaleManager.disconnect();
                super.onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final boolean isConnected) {
        runOnUiThread(() -> {
            mCircularProgressBar.setProgress(0);
            mCircularProgressBar.setProgressWithAnimation(100); // Default animate duration = 1500ms

            if (isConnected) {
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
            } else {
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });
    }

    /**
     * Return value of BMI.
     * formula: bodyMass(kg)/height(m)^2
     *
     * @param bodyMass double
     * @return double
     */
    private double calcBMI(double bodyMass) {
        //double height = (session.getUserLogged().getHeight()) / 100D;
        double height = 1.0;
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

        runOnUiThread(() -> {
            bodyMassTextView.setText(formatNumber(measurement.getValue()));
            bodyMassUnitTextView.setText(measurement.getUnit());
            mDateLastMeasurement.setText(DateUtils.abbreviatedDate(
                    getApplicationContext(), measurement.getRegistrationDate()));

            /**
             * Relations
             */
            for (Measurement m : measurement.getMeasurements()) {
                if (m.getTypeId() == MeasurementType.BMI) {
                    bmiTextView.setText(formatNumber(m.getValue()));
                    titleBmiTextView.setVisibility(View.VISIBLE);
                } else if (m.getTypeId() == MeasurementType.BODY_FAT) {
                    bodyFatTextView.setText(formatNumber(m.getValue()));
                    unitBodyFatTextView.setText(m.getUnit());
                    titleBodyFatTextView.setVisibility(View.VISIBLE);
                }
            }

            if (applyAnimation) bodyMassTextView.startAnimation(animation);
        });
    }

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    showMessage(R.string.bluetooth_disabled);

                } else if (state == BluetoothAdapter.STATE_ON) {
                    showMessage(-1);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), BodyCompositionChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                it.putExtra(getResources().getString(R.string.measurementType),
                        ItemGridType.WEIGHT);
                startActivity(it);
                break;
            default:
                break;
        }
    }


    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    public void checkPermissions() {
        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            requestBluetoothEnable();
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    public boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Location permission.
     */
    protected void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if ((requestCode == REQUEST_ENABLE_LOCATION) &&
                (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            requestLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                requestBluetoothEnable();
            } else {
                requestLocationPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}