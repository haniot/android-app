package br.edu.uepb.nutes.haniot.devices.base;

import android.Manifest;
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
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BluetoothManager;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseDeviceActivity extends AppCompatActivity implements View.OnClickListener {
    protected final int REQUEST_ENABLE_BLUETOOTH = 1;
    protected final int REQUEST_ENABLE_LOCATION = 2;
    private final String BLUETOOTH = "bluetooth";
    private final String WIRELESS = "wifi";
    private final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    private final int LIMIT_PER_PAGE = 20;
    private final int INITIAL_PAGE = 1;
    private int page = INITIAL_PAGE;

    protected boolean mConnected = false;
    //    private boolean showAnimation = true;
    protected Animation animation;
    protected Device mDevice;
    protected AppPreferencesHelper appPreferencesHelper;
    protected Repository mRepository;
    protected DecimalFormat decimalFormat;
    private BaseAdapter mAdapter;
    protected BluetoothManager manager;

    protected Patient patient;

    private boolean wifiRequest;
    private boolean bluetoothRequest;
    private boolean isFirst;

    private List<Measurement> measurementsToDelete;
    private Handler handler;
    private Runnable runnable;
    private Snackbar snackbar;
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

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        mRepository = Repository.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));
        measurementsToDelete = new ArrayList<>();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        patient = appPreferencesHelper.getLastPatient();

        if (isTablet(this)) {
            Log.i(getTag(), "is tablet");
            boxMeasurement.getLayoutParams().height = 600;
            mCollapsingToolbarLayout.getLayoutParams().height = 630;
            boxMeasurement.requestLayout();
            mCollapsingToolbarLayout.requestLayout();
        }
        initComponents();
        isFirst = true;

        IntentFilter filterBluetooth = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filterBluetooth);
        IntentFilter filterInternet = new IntentFilter(CONNECTIVITY_CHANGE);
        registerReceiver(mReceiver, filterInternet);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status = NetworkUtil.getConnectivityStatusString(context);

            if (CONNECTIVITY_CHANGE.equals(action)) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    Log.w(getTag(), "mReceiver: wifi desligado");
                    showMessageConnection(WIRELESS, true);
                } else {
                    Log.w(getTag(), "mReceiver: wifi ligado");
                    showMessageConnection(WIRELESS, false);

                    if (!isFirst) {
                        loadData(true);
                    }
                    isFirst = false;
                }
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Log.w(getTag(), "mReceiver: Bluetooth desligado");
                    showMessageConnection(BLUETOOTH, true);
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Log.w(getTag(), "mReceiver: Bluetooth ligado");
                    appPreferencesHelper.saveBluetoothMode(true);
                    showMessageConnection(BLUETOOTH, false);
                }
            }
        }
    };

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
            } else {
                noDataMessage.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Check if is tablet.
     *
     * @param context Context
     * @return True if is tablet, False otherwise
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

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mCollapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), android.R.color.transparent));
        mCollapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorTextDark));

        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                mCollapsingToolbarLayout.setTitle(getTitleActivity());
            } else {
                mCollapsingToolbarLayout.setTitle("");
            }
        });
    }

    /**
     * Init RecyclerView
     */
    private void initRecyclerView() {
        mAdapter = getAdapter();

        RecyclerView mRecyclerView = getRecyclerView();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter.setListener(new OnRecyclerViewListener<Measurement>() {
            @Override
            public void onItemClick(Measurement item) {
                Log.w(getTag(), "onItemClick()");
            }

            @Override
            public void onLongItemClick(View v, Measurement item) {
            }

            @Override
            public void onMenuContextClick(View v, Measurement item) {
            }

            @Override
            public void onItemSwiped(Measurement item, int position) {
                mAdapter.removeItem(item);
                measurementsToDelete.add(item);
                handler = new Handler();
                runnable = () -> {
                    removePendingMeasurements();
                };
                handler.postDelayed(runnable, 4000);

                snackbar = Snackbar
                        .make(findViewById(R.id.root), getString(R.string.confirm_remove_measurement), Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), view -> {
                    mAdapter.restoreItem(item, position);
                    mRecyclerView.scrollToPosition(position);
                    measurementsToDelete.remove(item);
//                    handler.removeCallbacks(runnable);
                });
                snackbar.show();
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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
        mAdapter.enableSwipe(this);
    }

    /**
     * Initialize SwipeRefresh
     */
    private void initDataSwipeRefresh() {
        mDataSwipeRefresh.setOnRefreshListener(() -> {
            loadData(true);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        new Thread() {
            @Override
            public void run() {
                Log.w("XXX", "onPause() - run()");
                if (snackbar != null && snackbar.isShown()) snackbar.dismiss();
                if (handler != null) {
                    removePendingMeasurements();
                    handler.removeCallbacks(runnable);
                }
            }
        }.start();
    }

    private void removePendingMeasurements() {
        Log.w("XXX", "removePendingMeasurements()");
        if (measurementsToDelete == null || measurementsToDelete.isEmpty()) return;
        for (Measurement measurement : measurementsToDelete) {
            measurement.setUserId(patient.get_id());

            DisposableManager.add(mRepository
                    .deleteMeasurement(measurement)
                    .subscribe(() -> {
                        measurementsToDelete.remove(measurement);
                    }, throwable -> {
                        Log.i("REMOVER", "removePendingMeasurements: ERROR, não removido");
                    }));
            measurementsToDelete.remove(measurement);
        }
    }

//    /**
//     * Load data from the local database.
//     * It should only be called when there is no internet connection or
//     * when an error occurs on the first request with the server.
//     */
//    private void loadDataLocal() {
//        page = INITIAL_PAGE; // returns to initial page
//        mAdapter.addItems(mRepository.listMeasurements(getMeasurementType(), patient.get_id(), 0, 100));
//
//        if (!mAdapter.itemsIsEmpty()) {
//            updateUILastMeasurement((Measurement) mAdapter.getFirstItem(), false);
//        } else {
//            toggleNoDataMessage(true); // Enable message no data
//        }
//        toggleLoading(false);
//    }

    /**
     * Load data.
     * If there is no internet connection, we can display the local database.
     * Otherwise it displays from the remote server.
     *
     * @param clearList True if clearList
     */
    private void loadData(boolean clearList) {
        if (clearList) {
            page = INITIAL_PAGE;
            mAdapter.clearItems();
        }

        removePendingMeasurements();
        DisposableManager.add(mRepository
                .getAllMeasurementsByType(patient.get_id(),
                        getMeasurementType(), "-timestamp",
                        null, null, page, LIMIT_PER_PAGE)
                .doOnSubscribe(disposable -> {
                    Log.w(getTag(), "loadData - doOnSubscribe");
                    toggleLoading(true);
                    toggleNoDataMessage(false);
                })
                .doAfterTerminate(() -> {
                    Log.w(getTag(), "loadData - doAfterTerminate");
                    toggleLoading(false); // Disable loading
                })
                .subscribe(measurements -> {
                    Log.w(getTag(), "loadData - onResult()");
                    if (measurements != null && measurements.size() > 0) {
                        mAdapter.addItems(measurements);
                        itShouldLoadMore = true;
                        if (page == INITIAL_PAGE) {
                            updateUILastMeasurement((Measurement) mAdapter.getFirstItem(), false);
                        }
                        page++;
                    } else {
                        toggleLoading(false);
                        if (mAdapter.itemsIsEmpty())
                            toggleNoDataMessage(true); // Enable message no data
                        itShouldLoadMore = false;
                    }
                }, erro -> {
                    Log.w(getTag(), "loadData - onError()");
                    if (mAdapter.itemsIsEmpty()) printMessage(getString(R.string.error_500));
                }));

//        mRepository.getMeasurements(patient.get_id(), getMeasurementType(), "-timestamp", page, LIMIT_PER_PAGE);
//
//        if (!ConnectionUtils.internetIsEnabled(this)) {
//            loadDataLocal();
//        } else {
//
//        }
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
     * Print Toast Messages.
     *
     * @param message Message
     */
    protected void printMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    /**
     * Displays message.
     */
    public void showMessageConnection(String typeMessageError, boolean show) {

        if (typeMessageError.equals(WIRELESS)) {
            if (show) {
                wifiRequest = true;
                messageError.setOnClickListener(null);
                messageError.setText(getString(R.string.wifi_disabled));
            } else {
                wifiRequest = false;
                if (bluetoothRequest) {
                    showMessageConnection(BLUETOOTH, true);
                }
            }
        } else if (typeMessageError.equals(BLUETOOTH)) {
            if (show) {
                bluetoothRequest = true;
                messageError.setText(getString(R.string.bluetooth_disabled));
                messageError.setOnClickListener(v -> {
                    appPreferencesHelper.saveBluetoothMode(true);
                    checkPermissions();
                });
            } else {
                bluetoothRequest = false;
                if (wifiRequest) {
                    showMessageConnection(WIRELESS, true);
                }
            }
        }

        if (wifiRequest || bluetoothRequest) {
            runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                boxMessage.setVisibility(View.VISIBLE);
            });
        } else {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
            boxMessage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        if (!wifiRequest && !bluetoothRequest) {
            boxMessage.setVisibility(View.GONE);
        }
        checkPermissions();
        loadData(true);
        updateConnectionState();

        if (mDevice != null && manager != null)
            manager.connect(BluetoothAdapter.getDefaultAdapter()
                    .getRemoteDevice(mDevice.getAddress())).useAutoConnect(true).enqueue();

        if (manager != null)
            if (manager.getConnectionState() != BluetoothProfile.STATE_CONNECTED && mDevice != null) {
                manager.connect(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mDevice.getAddress()));
            }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        DisposableManager.clear();
        if (manager != null) manager.close();
        unregisterReceiver(mReceiver);
    }

    protected void updateConnectionState() {
        Log.w(getTag(), "updateConnectionState: " + mConnected);

        runOnUiThread(() -> {
            mCircularProgressBar.setProgress(0);
            mCircularProgressBar.setProgressWithAnimation(100); // Default animate duration = 1500ms

            if (mConnected) {
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButtonDanger));
            } else {
                mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButtonDanger));
                mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });
    }

    /**
     * Performs routine for data synchronization with server.
     *
     * @param measurement MeasurementOB to save in server
     */
    protected void saveMeasurement(Measurement measurement) {
        DisposableManager.add(mRepository
                .saveMeasurement(measurement)
                .doAfterSuccess(measurement1 -> {
                    printMessage(getString(R.string.measurement_save));
                    Log.w(getTag(), "SINCRONIZAR...");
                    loadData(true);
                })
                .subscribe(measurement1 -> {
                }, error -> {
                    Log.w(getTag(), error.getMessage());
                    printMessage(getString(R.string.error_500));
                }));
    }

    protected abstract String getMeasurementType();

    protected abstract int getLayout();

    protected abstract RecyclerView getRecyclerView();

    protected abstract BaseAdapter getAdapter();

    protected abstract String getTitleActivity();

    protected abstract String getTag();

    protected abstract void updateUILastMeasurement(Measurement firstItem, boolean b);

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (manager != null) manager.disconnect();
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    public void checkPermissions() {
        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            showMessageConnection(BLUETOOTH, true);
            if (appPreferencesHelper.getBluetoothMode())
                requestBluetoothEnable();
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    protected void requestBluetoothEnable() {
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
                appPreferencesHelper.saveBluetoothMode(false);
                showMessageConnection(BLUETOOTH, true);
            } else {
                appPreferencesHelper.saveBluetoothMode(true);
                requestLocationPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
