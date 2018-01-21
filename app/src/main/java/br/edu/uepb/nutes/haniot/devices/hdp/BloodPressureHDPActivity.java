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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.signove.health.service.HealthAgentAPI;
import com.signove.health.service.HealthServiceAPI;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.graphs.BloodPresssureHDPGraphActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.IEEE11073BPParser;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
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
    private final String TAG = "BloodPressureHDPActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.view_pulse)
    CircularProgressBar mCircularPulse;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.box_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.pressure_measurement_sys)
    TextView mBloodPressureSysTextView;

    @BindView(R.id.pressure_measurement_dia)
    TextView mBloodPressureDiaTextView;

    @BindView(R.id.pressure_measurement_pulse)
    TextView mBloodPressurePulseTextView;

    private Animation animation;
    private Device mDevice;
    private Session session;
    private MeasurementDAO measurementDAO;
    private DeviceDAO deviceDAO;
    private Measurement measurement;

    private int[] specs = {0x1007};
    private Handler tm;
    private HealthServiceAPI api;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blood_pressure_hdp);
        ButterKnife.bind(this);

        initializeToolBar();

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        session = new Session(this);
        measurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);

        tm = new Handler();
        Intent intent = new Intent("com.signove.health.service.HealthService");
        intent.setPackage(this.getPackageName());
        startService(intent);
        bindService(intent, serviceConnection, 0);
        Log.w("HST", "Activity created");

        // synchronization with server
        synchronizeWithServer();
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
            api.Unconfigure(agent);
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.blood_pressure));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w("HST", "Service connection established");

            // that's how we get the client side of the IPC connection
            api = HealthServiceAPI.Stub.asInterface(service);
            try {
                api.ConfigurePassive(agent, specs);
            } catch (RemoteException e) {
                Log.e("HST", "Failed to add listener", e);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.w("HST", "Service connection closed");
        }
    };

    private HealthAgentAPI.Stub agent = new HealthAgentAPI.Stub() {
        @Override
        public void Connected(String dev, String addr) {
            updateConnectionState(true);

            // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
            mDevice = deviceDAO.get(addr, session.getIdLogged());

            if (mDevice == null) {
                mDevice = new Device(addr, "BLOOD PRESSURE MONITOR", "OMRON", "BP792IT", DeviceType.BLOOD_PRESSURE, session.getUserLogged());
                mDevice.set_id("5a62c42dd6f33400146c9b6a");
                if (!deviceDAO.save(mDevice)) finish();
                mDevice = deviceDAO.get(addr, session.getIdLogged());
            }
        }

        @Override
        public void Associated(String dev, String xmldata) {
            Runnable req1 = new Runnable() {
                public void run() {
                    RequestConfig(dev);
                }
            };
            Runnable req2 = new Runnable() {
                public void run() {
                    RequestDeviceAttributes(dev);
                }
            };
            tm.postDelayed(req1, 1);
            tm.postDelayed(req2, 500);
        }

        @Override
        public void MeasurementData(String dev, String xmldata) {
            br.edu.uepb.nutes.haniot.utils.Log.d(TAG, "MeasurementData: " + xmldata);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject data = IEEE11073BPParser.parse(xmldata);
                        handleMeasurement(data.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void DeviceAttributes(String dev, String xmldata) {
            Log.w("HST", ".." + xmldata);
            br.edu.uepb.nutes.haniot.utils.Log.d(TAG, "DeviceAttributes: " + xmldata);
        }

        @Override
        public void Disassociated(String dev) {
            Log.w("HST", "Disassociated " + dev);
        }

        @Override
        public void Disconnected(String dev) {
            Log.w("HST", "Disconnected " + dev);
            updateConnectionState(false);
        }
    };

    private void RequestConfig(String dev) {
        try {
            String xmldata = api.GetConfiguration(dev);
        } catch (RemoteException e) {
            Log.w("HST", "Exception (RequestConfig)");
        }
    }

    private void RequestDeviceAttributes(String dev) {
        try {
            api.RequestDeviceAttributes(dev);
        } catch (RemoteException e) {
            Log.w("HST", "Exception (RequestDeviceAttributes)");
        }
    }

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
                     * Update UI
                     */
                    mBloodPressureSysTextView.setText(String.format("%03d", (int) systolic.getValue()));
                    mBloodPressureDiaTextView.setText(String.format("%03d", (int) diastolic.getValue()));
                    mBloodPressurePulseTextView.setText(String.format("%03d", heartRate.getValue()));

                    mBloodPressureSysTextView.startAnimation(animation);
                    mBloodPressureDiaTextView.startAnimation(animation);
                    mBloodPressurePulseTextView.startAnimation(animation);

                    /**
                     * Add relationships, save and send to server
                     */
                    systolic.addMeasurement(diastolic, heartRate);
                    if (measurementDAO.save(systolic)) synchronizeWithServer();
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
            case R.id.view_circle:
                startActivity(new Intent(getApplicationContext(),BloodPresssureHDPGraphActivity.class));
                break;

            default:
                break;
        }
    }
}