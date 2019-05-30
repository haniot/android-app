package br.edu.uepb.nutes.haniot.devices;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
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
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.HeartRateChartActivity;
import br.edu.uepb.nutes.haniot.adapter.HeartRateAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.HeartRateManager;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the heart rate data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HeartRateActivity extends AppCompatActivity implements View.OnClickListener, GenericDialogFragment.OnClickDialogListener {
    private final String TAG = "HeartRateActivity";
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_DEVICE_INFORMATIONS = "device_informations";
    public final int DIALOG_SAVE_DATA = 1;
    private final int LIMIT_PER_PAGE = 20;
    private final int INITIAL_PAGE = 1;

    private boolean mConnected = false;

    private ObjectAnimator heartAnimation;
    private Device mDevice;
    private AppPreferencesHelper appPreferencesHelper;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private HeartRateAdapter mAdapter;
    private HeartRateManager heartRateManager;

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

    @BindView(R.id.heart_rate_textview)
    TextView mHeartRateTextView;

    @BindView(R.id.heart_rate_unit_textview)
    TextView mUnitHeartRateTextView;

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

    @BindView(R.id.heart_rate_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

//    @BindView(R.id.record_floating_button)
//    FloatingActionButton mRecordHeartRateButton;

    @BindView(R.id.heart_imageview)
    ImageView mHeartImageView;

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddMeasurementButton;

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    @BindView(R.id.box_measurement)
    RelativeLayout boxMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        ButterKnife.bind(this);
        checkPermissions();

        // synchronization with server
        synchronizeWithServer();

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        heartRateManager = new HeartRateManager(this);
//        heartRateManager.setSimpleCallback(heartRateDataCallback);

        patient = appPreferencesHelper.getLastPatient();
        haniotNetRepository = HaniotNetRepository.getInstance(this);

        if (isTablet(this)){
            Log.i(TAG, "is tablet");
            boxMeasurement.getLayoutParams().height= 600;
            mCollapsingToolbarLayout.getLayoutParams().height= 630;
            boxMeasurement.requestLayout();
            mCollapsingToolbarLayout.requestLayout();
        }

        mDevice = deviceDAO.getByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.HEART_RATE);
        mChartButton.setOnClickListener(this);
//        mRecordHeartRateButton.setOnClickListener(this);
        mAddMeasurementButton.setOnClickListener(this);
        messageError.setOnClickListener(v -> checkPermissions());

        initComponents();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    /**
     * Check if is tablet.
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
//
//    HeartRateDataCallback heartRateDataCallback = new HeartRateDataCallback() {
//        @Override
//        public void onConnected() {
//            mConnected = true;
//            updateConnectionState(true);
//        }
//
//        @Override
//        public void onDisconnected() {
//            mConnected = false;
//            updateConnectionState(false);
//        }
//
//        @Override
//        public void onMeasurementReceived(Measurement measurementHeartRate) {
//            if (mDevice != null)
//                measurementHeartRate.setDevice(mDevice);
//
//            /**
//             * Save in local
//             * Send to server saved successfully
//             */
//            if (measurementDAO.save(measurementHeartRate)) {
//                synchronizeWithServer();
//                loadData();
//            }
//            updateUILastMeasurement(measurementHeartRate, true);
//        }
//    };

    /**
     * Initialize components
     */
    private void initComponents() {
        initAnimation();
        initToolBar();
        initRecyclerView();
        initDataSwipeRefresh();
        loadData();
    }

    /**
     * Animation for heart
     */
    private void initAnimation() {
        /**
         * Setting heartAnimation in heart imageview
         */
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(mHeartImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        heartAnimation.setDuration(500);
        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.heart_rate));
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
        mAdapter = new HeartRateAdapter(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setListener(new OnRecyclerViewListener<Measurement>() {
            @Override
            public void onItemClick(Measurement item) {
                Log.w(TAG, "onItemClick()");
            }

            @Override
            public void onLongItemClick(View v, Measurement item) { }

            @Override
            public void onMenuContextClick(View v, Measurement item) { }
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
                        if (itShouldLoadMore) loadData();
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
            page = INITIAL_PAGE;
            loadData();
        });
    }

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        mAdapter.addItems(measurementDAO.list(MeasurementType.HEART_RATE, patient.getId(), 0, 100));

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
        if (page == INITIAL_PAGE)
            mAdapter.clearItems(); // clear list

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            DisposableManager.add(haniotNetRepository.
                    getAllMeasurementsByType(patient.get_id(), MeasurementType.HEART_RATE,
                            "-timestamp", null, null, page, LIMIT_PER_PAGE)
            .doOnSubscribe(disposable -> {
                Log.w(TAG, "loadData - doOnSubscribe");
                toggleLoading(true);
                toggleNoDataMessage(false);
            })
            .doAfterTerminate(()-> {
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
                if (mAdapter.itemsIsEmpty())
                    printMessage(getString(R.string.error_500));
                else
                    loadDataLocal();
            }));
        }
    }

    /**
     * List more itemsList from the remote server.
     */
    private void loadMoreData() {
//        if (!ConnectionUtils.internetIsEnabled(this))
//            return;
//
//        Historical historical = new Historical.Query()
//                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
//                .params(params) // Measurements of the blood heart rate type, associated to the user
//                .pagination(mAdapter.getItemCount(), LIMIT_PER_PAGE)
//                .build();
//
//        historical.request(this, new CallbackHistorical<Measurement>() {
//            @Override
//            public void onBeforeSend() {
//                Log.w(TAG, "loadMoreData - onBeforeSend()");
//                toggleLoading(true); // Enable loading
//            }
//
//            @Override
//            public void onError(JSONObject result) {
//                Log.w(TAG, "loadMoreData - onError()");
//                printMessage(getString(R.string.error_500));
//            }
//
//            @Override
//            public void onResult(List<Measurement> result) {
//                Log.w(TAG, "loadMoreData - onResult()");
//                if (result != null && result.size() > 0) mAdapter.addItems(result);
//                else printMessage(getString(R.string.no_more_data));
//            }
//
//            @Override
//            public void onAfterSend() {
//                Log.w(TAG, "loadMoreData - onAfterSend()");
//                toggleLoading(false); // Disable loading
//            }
//        });
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
        if (heartRateManager.getConnectionState() == BluetoothProfile.STATE_DISCONNECTED && mDevice != null)
            heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);

        if (heartRateManager.getConnectionState() == BluetoothProfile.STATE_DISCONNECTED && mDevice != null)
            heartRateManager.connectDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DisposableManager.dispose();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                heartRateManager.disconnect();
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
                heartAnimation.start();
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
            } else {
                heartAnimation.pause();
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });
    }

    /**
     * updateOrSave the UI with the last measurement.
     *
     * @param measurement {@link Measurement}
     */
    private void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(() -> {
            mHeartRateTextView.setText(String.format("%03d", (int) measurement.getDataset().get(0).getValue()));
            mUnitHeartRateTextView.setText(measurement.getUnit());

            String timeStamp = measurement.getDataset().get(0).getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp,"MMMM dd, EEE"
                ));
            }
            mHeartImageView.setVisibility(View.VISIBLE);
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
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.record_floating_button:
//                Intent intent = new Intent(this, RecordHeartRateActivity.class);
//                if (mDevice != null)
//                    intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, mDevice.getAddress());
//                startActivity(intent);
//                break;
            case R.id.chart_floating_button:
//                startActivity(new Intent(getApplicationContext(), HeartRateChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType),
                        ItemGridType.HEART_RATE);
                startActivity(it);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickDialog(int id, int button) {
//        if (id == DIALOG_SAVE_DATA && button == DialogInterface.BUTTON_POSITIVE) {
//            /**
//             * Add relations
//             */
//            measurementSave.setUser(appPreferencesHelper.getUserLogged());
//            if (mDevice != null)
//                measurementSave.setDevice(mDevice);
//
//            /**
//             * Save in local
//             * Send to server saved successfully
//             */
//            if (measurementDAO.save(measurementSave)) {
//                synchronizeWithServer();
//                loadData();
//            }
//        }
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