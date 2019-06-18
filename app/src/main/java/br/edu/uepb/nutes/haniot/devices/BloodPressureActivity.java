package br.edu.uepb.nutes.haniot.devices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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
import br.edu.uepb.nutes.haniot.activity.charts.BloodPresssureChartActivity;
import br.edu.uepb.nutes.haniot.adapter.BloodPressureAdapter;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BluetoothManager;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BloodPressureActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "PressureActivity";
    private final int LIMIT_PER_PAGE = 20;
    private final int INITIAL_PAGE = 1;

    protected boolean mConnected = false;

    protected BluetoothManager manager;

    protected Animation animation;
    protected Device mDevice;
    protected AppPreferencesHelper appPreferencesHelper;
    protected MeasurementDAO measurementDAO;
    protected DeviceDAO deviceDAO;
    protected DecimalFormat decimalFormat;

    protected HaniotNetRepository haniotNetRepository;
    protected Patient patient;
    private BloodPressureAdapter mAdapter;
    public int page = INITIAL_PAGE;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.date_last_measurement_textView)
    protected
    TextView mDateLastMeasurement;

    @BindView(R.id.no_data_textView)
    TextView noDataMessage;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddButton;

    @BindView(R.id.box_measurement)
    RelativeLayout boxMeasurement;

    @BindView(R.id.unit_blood_pressure_textviewt)
    TextView mUnitBloodPressureTextView;

    @BindView(R.id.blood_pressure_dia_textviewt)
    TextView mBloodPressureDiaTextView;

    @BindView(R.id.blood_pressure_sys_textviewt)
    TextView mBloodPressureSysTextView;

    @BindView(R.id.blood_pressure_pulse_textviewt)
    TextView mBloodPressurePulseTextView;

    @BindView(R.id.unit_pulse_textviewt)
    TextView mUnitPulseTextView;

    @BindView(R.id.blood_pressure_recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        ButterKnife.bind(this);

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number1), new DecimalFormatSymbols(Locale.US));

        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        //TODO TEMP - Há problemas no cadastro dos dispositivos
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

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

    /**
     * Initialize components
     */
    protected void initComponents() {
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.blood_pressure));
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
    protected void initRecyclerView() {
        mAdapter = new BloodPressureAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
        mAdapter.replace(measurementDAO.list(MeasurementType.BLOOD_PRESSURE, patient.get_id(), 0, 100));

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
    protected void loadData(boolean clearList) {
        if (clearList) {
            mAdapter.clearItems();
            page = INITIAL_PAGE;
        }

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            DisposableManager.add(haniotNetRepository
                    .getAllMeasurementsByType(patient.get_id(),
                            MeasurementType.BLOOD_PRESSURE, "-timestamp", null,
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
    protected void toggleNoDataMessage(boolean visible) {
        runOnUiThread(() -> {
            if (visible) {
                if (!ConnectionUtils.internetIsEnabled(getApplicationContext())) {
                    noDataMessage.setText(getString(R.string.connect_network_try_again));
                } else {
                    noDataMessage.setText(getString(R.string.no_data_available));
                }
                noDataMessage.setVisibility(View.VISIBLE);
                mDateLastMeasurement.setText("");
                mUnitBloodPressureTextView.setText("");
                mBloodPressureDiaTextView.setText("");
                mBloodPressureSysTextView.setText("");
                mBloodPressurePulseTextView.setText("");
                mUnitPulseTextView.setVisibility(View.GONE);
            } else {
                noDataMessage.setVisibility(View.GONE);
                mUnitPulseTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Print Toast Messages.
     *
     * @param message
     */
    protected void printMessage(String message) {
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(true);

        if (manager != null)
            if (manager.getConnectionState() == BluetoothProfile.STATE_DISCONNECTED && mDevice != null)
                manager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                manager.disconnect();
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void updateConnectionState(final boolean isConnected) {
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
     * Performs routine for data synchronization with server.
     *
     * @param measurement Measurement to save in server
     */
    protected void saveMeasurementInServer(Measurement measurement) {
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

//    @Override
//    protected RecyclerView getmRecyclerView() {
//        return this.mRecyclerView;
//    }
//
//    @Override
//    protected void toggleNoDataMessage(boolean visible) {
//        super.toggleNoDataMessage(visible);
//        if (visible) {
//
//        }
//    }

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
    protected void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(() -> {
            mUnitBloodPressureTextView.setText(measurement.getUnit());
            mBloodPressureDiaTextView.setText(String.valueOf(measurement.getDiastolic()));
            mBloodPressureSysTextView.setText(String.valueOf(measurement.getSystolic()).concat("/"));

            mBloodPressurePulseTextView.setText(String.valueOf(measurement.getPulse()));

            String timeStamp = measurement.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }

            if (applyAnimation) {
                mBloodPressureDiaTextView.startAnimation(animation);
                mBloodPressureSysTextView.startAnimation(animation);
                mBloodPressurePulseTextView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), BloodPresssureChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;
        }
    }
}
