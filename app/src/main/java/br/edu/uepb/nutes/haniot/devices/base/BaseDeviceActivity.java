package br.edu.uepb.nutes.haniot.devices.base;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.BluetoothManager;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseDeviceActivity extends AppCompatActivity implements View.OnClickListener {
    protected final int REQUEST_ENABLE_BLUETOOTH = 1;
    protected final int REQUEST_ENABLE_LOCATION = 2;
    private final int LIMIT_PER_PAGE = 20;
    private final int INITIAL_PAGE = 1;
    private int page = INITIAL_PAGE;

    protected boolean mConnected = false;
    private boolean showAnimation = true;
    protected Animation animation;
    protected Device mDevice;
    protected AppPreferencesHelper appPreferencesHelper;
    private MeasurementDAO measurementDAO;
    protected DeviceDAO deviceDAO;
    protected DecimalFormat decimalFormat;
    private BaseAdapter mAdapter;
    protected BluetoothManager manager;

    protected HaniotNetRepository haniotNetRepository;
    protected Patient patient;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayout());
        ButterKnife.bind(this);

        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        haniotNetRepository = HaniotNetRepository.getInstance(this);
        patient = appPreferencesHelper.getLastPatient();

        if (isTablet(this)) {
            Log.i(getTag(), "is tablet");
            boxMeasurement.getLayoutParams().height = 600;
            mCollapsingToolbarLayout.getLayoutParams().height = 630;
            boxMeasurement.requestLayout();
            mCollapsingToolbarLayout.requestLayout();
        }
        initComponents();
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
            } else {
                noDataMessage.setVisibility(View.GONE);
            }
        });
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
                    mCollapsingToolbarLayout.setTitle(getTitleActivity());
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
                final Handler handler = new Handler();
                Runnable runnable = () -> DisposableManager.add(haniotNetRepository
                        .deleteMeasurement(patient.get_id(), item.get_id()).subscribe(() -> {
                        }));
                handler.postDelayed(runnable, 4000);

                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.root), getString(R.string.confirm_remove_measurement), Snackbar.LENGTH_LONG);
                snackbar.setAction(getString(R.string.undo), view -> {
                    mAdapter.restoreItem(item, position);
                    mRecyclerView.scrollToPosition(position);
                    handler.removeCallbacks(runnable);
                });
                snackbar.show();
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

    /**
     * Load data from the local database.
     * It should only be called when there is no internet connection or
     * when an error occurs on the first request with the server.
     */
    private void loadDataLocal() {
        page = INITIAL_PAGE; // returns to initial page
        mAdapter.addItems(measurementDAO.list(getMeasurementType(), patient.get_id(), 0, 100));

        if (!mAdapter.itemsIsEmpty()) {
            updateUILastMeasurement((Measurement) mAdapter.getFirstItem(), false);
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
            page = INITIAL_PAGE;
            mAdapter.clearItems();
        }

        if (!ConnectionUtils.internetIsEnabled(this)) {
            loadDataLocal();
        } else {
            DisposableManager.add(haniotNetRepository
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
                        else loadDataLocal();
                    }));
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
     * Print Toast Messages.
     *
     * @param message
     */
    protected void printMessage(String message) {
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onResume() {
        loadData(true);

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
        DisposableManager.dispose();
        if (manager != null) manager.close();
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
    protected void synchronizeWithServer(Measurement measurement) {
        DisposableManager.add(haniotNetRepository
                .saveMeasurement(measurement)
                .doAfterSuccess(measurement1 -> {
                    printMessage(getString(R.string.measurement_save));
                    loadData(true);
                })
                .subscribe(measurement1 -> {
                }, error -> {
                    measurementDAO.save(measurement);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                if (manager != null) manager.disconnect();
                super.onBackPressed();
                break;
            default:
                break;
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
                requestBluetoothEnable();
            } else {
                requestLocationPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
