package br.edu.uepb.nutes.haniot.devices.hdp;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
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
//import com.signove.health.service.HealthAgentAPI;
//import com.signove.health.service.HealthServiceAPI;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.charts.BloodPresssureChartActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.BloodPressureAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.IEEE11073BPParser;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the balance data.
 *
 * @author Lucas Barbosa
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BloodPressureHDPActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "BloodPressureHDP";
    private final int LIMIT_PER_PAGE = 20;

    private Animation animation;
    private Device mDevice;
    private Session session;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private BloodPressureAdapter mAdapter;
    private Params params;

    private int[] specs = {0x1007};
    private Handler tm;
//    private HealthServiceAPI api;

    /**
     * We need this variable to lock and unlock loading more.
     * We should not charge more when a request has already been made.
     * The load will be activated when the requisition is completed.
     */
    private boolean itShouldLoadMore = true;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.blood_pressure_sys_textview)
    TextView mBloodPressureSysTextView;

    @BindView(R.id.blood_pressure_dia_textview)
    TextView mBloodPressureDiaTextView;

    @BindView(R.id.unit_blood_pressure_textview)
    TextView mBloodPressureUnitTextView;

    @BindView(R.id.blood_pressure_pulse_textview)
    TextView mBloodPressurePulseTextView;

    @BindView(R.id.unit_pulse_textview)
    TextView mBloodPressurePulseUnitTextView;

    @BindView(R.id.date_last_measurement_textView)
    TextView mDateLastMeasurement;

    @BindView(R.id.no_data_textView)
    TextView noDataMessage;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.view_pulse)
    CircularProgressBar mCircularPulse;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.blood_pressure_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.data_swiperefresh)
    SwipeRefreshLayout mDataSwipeRefresh;

    @BindView(R.id.chart_floating_button)
    FloatingActionButton mChartButton;

    @BindView(R.id.add_floating_button)
    FloatingActionButton mAddButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        ButterKnife.bind(this);

        // synchronization with server
        synchronizeWithServer();

        session = new Session(this);
        deviceDAO = DeviceDAO.getInstance(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        params = new Params(session.get_idLogged(), MeasurementType.BLOOD_PRESSURE_SYSTOLIC);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        mChartButton.setOnClickListener(this);
        mAddButton.setOnClickListener(this);

        tm = new Handler();
        Intent intent = new Intent("com.signove.health.service.HealthService");
        intent.setPackage(this.getPackageName());
        startService(intent);
        bindService(intent, serviceConnection, 0);
        Log.w("HST", "Activity created");

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
     * Initialize toolbar
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
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
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
    private void initRecyclerView() {
        mAdapter = new BloodPressureAdapter(this);
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
                }else{
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
        mAdapter.addItems(measurementDAO.list(MeasurementType.BLOOD_PRESSURE_SYSTOLIC, session.getIdLogged(), 0, 100));

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
                    .params(params) // Measurements of the blood pressure type, associated to the user
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
                .params(params) // Measurements of the blood pressure type, associated to the user
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
                mBloodPressureSysTextView.setText(
                        String.valueOf((int) measurement.getValue()).concat("/"));
                mBloodPressureUnitTextView.setText(measurement.getUnit());
                mDateLastMeasurement.setText(DateUtils.abbreviatedDate(
                        getApplicationContext(), measurement.getRegistrationDate()));

                /**
                 * Relations
                 */
                for (Measurement m : measurement.getMeasurements()) {
                    if (m.getTypeId() == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
                        mBloodPressureDiaTextView.setText(String.valueOf((int) m.getValue()));
                    else if (m.getTypeId() == MeasurementType.HEART_RATE) {
                        mBloodPressurePulseTextView.setText(String.valueOf((int) m.getValue()));
                        mBloodPressurePulseUnitTextView.setVisibility(View.VISIBLE);
                    }
                }

                if (applyAnimation) {
                    mBloodPressureSysTextView.startAnimation(animation);
                    mBloodPressureDiaTextView.startAnimation(animation);
                    mBloodPressureUnitTextView.startAnimation(animation);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            Log.w("HST", "Unconfiguring...");
//            api.Unconfigure(agent);
        } catch (Throwable t) {
            Log.w("HST", "Erro tentando desconectar");
        }
        unbindService(serviceConnection);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
                mCircularPulse.setProgress(0);
                mCircularPulse.setProgressWithAnimation(100); // Default animate duration = 1500ms

                if (isConnected) {
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                    mCircularPulse.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularPulse.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                } else {
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularPulse.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                    mCircularPulse.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                }
            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w("HST", "Service connection established");

            // that's how we get the client side of the IPC connection
//            api = HealthServiceAPI.Stub.asInterface(service);
//            try {
////                api.ConfigurePassive(agent, specs);
//            } catch (RemoteException e) {
//                Log.e("HST", "Failed to add listener", e);
//            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("HST", "Service connection closed");
        }
    };

//    private HealthAgentAPI.Stub agent = new HealthAgentAPI.Stub() {
//        @Override
//        public void Connected(String dev, String addr) {
//            updateConnectionState(true);
//
//            // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
//            mDevice = deviceDAO.get(addr, session.getIdLogged());
//
//            if (mDevice == null) {
//                mDevice = new Device(addr, "BLOOD PRESSURE MONITOR", "OMRON", "BP792IT", DeviceType.BLOOD_PRESSURE, session.getUserLogged());
//                mDevice.set_id("5a62c42dd6f33400146c9b6a");
//                if (!deviceDAO.save(mDevice)) finish();
//                mDevice = deviceDAO.get(addr, session.getIdLogged());
//            }
//        }
//
//        @Override
//        public void Associated(String dev, String xmldata) {
//            Runnable req1 = new Runnable() {
//                public void run() {
//                    RequestConfig(dev);
//                }
//            };
//            Runnable req2 = new Runnable() {
//                public void run() {
//                    RequestDeviceAttributes(dev);
//                }
//            };
//            tm.postDelayed(req1, 1);
//            tm.postDelayed(req2, 500);
//        }
//
//        @Override
//        public void MeasurementData(String dev, String xmldata) {
//            br.edu.uepb.nutes.haniot.utils.Log.d(TAG, "MeasurementData: " + xmldata);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        JSONObject data = IEEE11073BPParser.parse(xmldata);
//                        handleMeasurement(data.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } catch (XmlPullParserException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//        }
//
//        @Override
//        public void DeviceAttributes(String dev, String xmldata) {
//            Log.w("HST", ".." + xmldata);
//            br.edu.uepb.nutes.haniot.utils.Log.d(TAG, "DeviceAttributes: " + xmldata);
//        }
//
//        @Override
//        public void Disassociated(String dev) {
//            Log.w("HST", "Disassociated " + dev);
//        }
//
//        @Override
//        public void Disconnected(String dev) {
//            Log.w("HST", "Disconnected " + dev);
//            updateConnectionState(false);
//        }
//    };

//    private void RequestConfig(String dev) {
//        try {
//            String xmldata = api.GetConfiguration(dev);
//        } catch (RemoteException e) {
//            Log.w("HST", "Exception (RequestConfig)");
//        }
//    }
//
//    private void RequestDeviceAttributes(String dev) {
//        try {
//            api.RequestDeviceAttributes(dev);
//        } catch (RemoteException e) {
//            Log.w("HST", "Exception (RequestDeviceAttributes)");
//        }
//    }

    /**
     * Treats the measurement by breaking down types of measurements.
     * Add Relationships, saves to the local database and sends it to the server.
     *
     * @param xmldata String
     */
    private void handleMeasurement(String xmldata) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    User user = session.getUserLogged();

                    Measurement systolic = JsonToMeasurementParser.systolic(xmldata);
                    systolic.setUser(user);
                    systolic.setDevice(mDevice);

                    Measurement diastolic = JsonToMeasurementParser.diastolic(xmldata);
                    diastolic.setUser(user);
                    diastolic.setDevice(mDevice);

                    Measurement heartRate = JsonToMeasurementParser.heartRate(xmldata);
                    heartRate.setUser(user);
                    heartRate.setDevice(mDevice);

                    /**
                     * Add relationships
                     */
                    systolic.addMeasurement(diastolic, heartRate);

                    /**
                     * Update UI
                     */
                    updateUILastMeasurement(systolic, true);

                    /**
                     * Save in local
                     * Send to server saved successfully
                     */
                    if (measurementDAO.save(systolic)) {
                        synchronizeWithServer();
                        loadData();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
                startActivity(new Intent(getApplicationContext(), BloodPresssureChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), ManuallyAddMeasurement.class);
                it.putExtra(getResources().getString(R.string.measurementType),
                        ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;

            default:
                break;
        }
    }
}