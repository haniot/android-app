package br.edu.uepb.nutes.haniot.devices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.adapter.TemperatureAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ThermometerManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
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
    private final int LIMIT_PER_PAGE = 20;
    private final int INITIAL_PAGE = 1;

    private boolean mConnected = false;

    private Animation animation;
    private Device mDevice;
    private AppPreferencesHelper appPreferencesHelper;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private DecimalFormat decimalFormat;
    private TemperatureAdapter mAdapter;
    private ThermometerManager thermometerManager;

    private HaniotNetRepository haniotNetRepository;
    private Patient patient;
    public int page = INITIAL_PAGE;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.temperature_textview)
    TextView mTemperatureTextView;

    @BindView(R.id.unit_temperature_textview)
    TextView mUnitTemperatureTextView;

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

    @BindView(R.id.temperature_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddButton;

    @BindView(R.id.box_measurement)
    RelativeLayout boxMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thermometer);
        ButterKnife.bind(this);

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number1), new DecimalFormatSymbols(Locale.US));
        thermometerManager = new ThermometerManager(this);
        thermometerManager.setSimpleCallback(temperatureDataCallback);

        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();


        //TODO TEMP - Há problemas no cadastro dos dispositivos
        mDevice = deviceDAO.getByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.THERMOMETER);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        if (isTablet(this)) {
            Log.i(TAG, "is tablet");
            boxMeasurement.getLayoutParams().height = 600;
            mCollapsingToolbarLayout.getLayoutParams().height = 630;
            boxMeasurement.requestLayout();
            mCollapsingToolbarLayout.requestLayout();
        }
        initComponents();
    }

    /**
     * Check if is tablet.
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private TemperatureDataCallback temperatureDataCallback = new TemperatureDataCallback() {
        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, double temp, String unit, String timestamp) {
            Measurement measurement = new Measurement();
            measurement.setTimestamp(DateUtils.getCurrentDateTimeUTC());
            measurement.setValue(temp);
            measurement.setUnit(unit);
//            if (mDevice != null) measurement.setDeviceId(mDevice.get_id());
            measurement.setUserId(patient.get_id());
            measurement.setType(MeasurementType.BODY_TEMPERATURE);

            // Save in local
            // Send to server saved successfully
            if (measurementDAO.save(measurement)) {
                saveMeasurementInServer(measurement);
            }
        }

        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            mConnected = true;
            updateConnectionState(true);
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            mConnected = false;
            updateConnectionState(false);
        }
    };

    /**
     * Initialize components
     */
    private void initComponents() {
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.temperature));
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
        mAdapter = new TemperatureAdapter(this);
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
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) loadData(false);
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
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            loadData(true);
        });
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        page = INITIAL_PAGE; // returns to initial page
        mAdapter.replace(measurementDAO.list(MeasurementType.BODY_TEMPERATURE, patient.get_id(), 0, 100));

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
     *
     * @param clearList True if clearList
     */
    private void loadData(boolean clearList) {
        if (clearList) {
            mAdapter.clearItems();
            page = INITIAL_PAGE;
        }

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            DisposableManager.add(haniotNetRepository
                    .getAllMeasurementsByType(patient.get_id(),
                            MeasurementType.BODY_TEMPERATURE, "-timestamp", null,
                            null, page, LIMIT_PER_PAGE)
                    .doOnSubscribe(disposable -> {
                        Log.w(TAG, "loadData - doOnSubscribe");
                        toggleLoading(true);
                        toggleNoDataMessage(false);
                    })
                    .doAfterTerminate(() -> {
                        Log.w(TAG, "loadData - doAfterTerminate");
                        toggleLoading(false); // Disable loading
                    })
                    .subscribe(measurements -> {
                                Log.w(TAG, "loadData - onResult()");
                                if (measurements != null && measurements.size() > 0) {
                                    mAdapter.addItems(measurements);
                                    page++;
                                    itShouldLoadMore = true;
                                    updateUILastMeasurement(mAdapter.getFirstItem(), false);
                                } else {
                                    toggleLoading(false);
                                    if (mAdapter.itemsIsEmpty())
                                        toggleNoDataMessage(true); // Enable message no data
                                    itShouldLoadMore = false;
                                }
                            }, error -> {
                                Log.w(TAG, "loadData - onError()");
                                if (mAdapter.itemsIsEmpty()) {
                                    printMessage(getString(R.string.error_500));
                                } else loadDataLocal();
                            }
                    )
            );
        }
    }


    /**
     * Enable/Disable display loading data.
     *
     * @param enabled boolean
     */
    private void toggleLoading(boolean enabled) {
        runOnUiThread(() -> {
            mDataSwipeRefresh.setRefreshing(enabled);
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

    @Override
    protected void onResume() {
        super.onResume();
        loadData(true);

        if (thermometerManager.getConnectionState() == BluetoothProfile.STATE_DISCONNECTED && mDevice != null)
            thermometerManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
        thermometerManager.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                thermometerManager.disconnect();
                super.onBackPressed();
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
     * updateOrSave the UI with the last measurement.
     *
     * @param m {@link Measurement}
     */
    private void updateUILastMeasurement(Measurement m, boolean applyAnimation) {
        if (m == null) return;

        runOnUiThread(() -> {
            mTemperatureTextView.setText(decimalFormat.format(m.getValue()));
            mUnitTemperatureTextView.setText(m.getUnit());

            String timeStamp = m.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }
            if (applyAnimation) mTemperatureTextView.startAnimation(animation);
        });
    }

    /**
     * Performs routine for data synchronization with server.
     *
     * @param measurement Measurement to save in server
     */
    private void saveMeasurementInServer(Measurement measurement) {
        DisposableManager.add(haniotNetRepository
                .saveMeasurement(measurement)
                .doAfterSuccess(measurement1 -> {
                    printMessage(getString(R.string.measurement_save));
                    loadData(true);
                })
                .subscribe(measurement1 -> {
                }, error -> {
                    Log.w(TAG, error.getMessage());
                    printMessage(getString(R.string.error_500));
                }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
//                startActivity(new Intent(getApplicationContext(), TemperatureChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType), ItemGridType.TEMPERATURE);
                startActivity(it);
                break;
        }
    }
}
