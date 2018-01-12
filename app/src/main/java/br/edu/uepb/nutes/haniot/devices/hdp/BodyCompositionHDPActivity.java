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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.DeviceType;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.IEEE11073BCParser;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BodyCompositionHDPActivity extends AppCompatActivity {
    private final String TAG = "BodyCompositionHDPActivity";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.body_mass_measurement)
    TextView bodyMassTextView;

    @BindView(R.id.body_mass_unit_measurement)
    TextView bodyMassUnitTextView;

    @BindView(R.id.body_fat_textview)
    TextView bodyFatTextView;

    @BindView(R.id.bmi_textview)
    TextView bmiTextView;

    private Animation animation;
    private Device mDevice;
    private Session session;
    private DeviceDAO deviceDAO;
    private Measurement measurement;

    private int[] specs = {0x100F}; // 0x100F - Body Weight Scale
    private Handler tm;
    private HealthServiceAPI api;
    private String xmlDevice;
    private DecimalFormat decimalFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition_hdp);
        ButterKnife.bind(this);

        initializeToolBar();

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        session = new Session(this);
        deviceDAO = DeviceDAO.getInstance(this);
        decimalFormat = new DecimalFormat(getString(R.string.temperature_format), new DecimalFormatSymbols(Locale.US));

        tm = new Handler();
        Intent intent = new Intent("com.signove.health.service.HealthService");
        startService(intent);
        bindService(intent, serviceConnection, 0);
        Log.w("HST", "Activity created");

        SynchronizationServer.getInstance(this).run();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.body_weight_scale));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void RequestConfig(String dev) {
        try {
            Log.w("HST", "Getting configuration ");
            String xmldata = api.GetConfiguration(dev);
            Log.w("HST", "Received configuration");
            Log.w("HST", ".." + xmldata);
        } catch (RemoteException e) {
            Log.w("HST", "Exception (RequestConfig)");
        }
    }

    private void RequestDeviceAttributes(String dev) {
        try {
            Log.w("HST", "Requested device attributes");
            api.RequestDeviceAttributes(dev);
        } catch (RemoteException e) {
            Log.w("HST", "Exception (RequestDeviceAttributes)");
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.w("HST", "Service connection established");

            // that's how we get the client side of the IPC connection
            api = HealthServiceAPI.Stub.asInterface(service);
            try {
                Log.w("HST", "Configuring...");
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
            Log.w("HST", "Connected " + dev);
            Log.w("HST", "..." + addr);

            // TODO REMOVER!!! Pois o cadastro do device deverá ser no processo de emparelhamento
            if (mDevice == null) {
                mDevice = deviceDAO.get(addr, session.getIdLogged());

                if (mDevice == null) {
                    mDevice = new Device(addr, "BODY WEIGHT SCALE", "OMRON", "HBF-206IT", DeviceType.BODY_COMPOSITION, session.getUserLogged());
                    mDevice.set_id("3b1847dfd7bcdd2448000cc6");
                    if (!deviceDAO.save(mDevice)) finish();
                }
            }
        }

        @Override
        public void Associated(String dev, String xmldata) {
            final String idev = dev;
            Log.w("HST", "Associated " + dev);
            Log.w("HST", "...." + xmldata);
            d(TAG, "ASSOCIATED: " + xmldata);

            Runnable req1 = new Runnable() {
                public void run() {
                    RequestConfig(idev);
                }
            };
            Runnable req2 = new Runnable() {
                public void run() {
                    RequestDeviceAttributes(idev);
                }
            };
            tm.postDelayed(req1, 1);
            tm.postDelayed(req2, 500);
        }

        @Override
        public void MeasurementData(String dev, String xmldata) {
            d(TAG, "MEASUREMENT: " + xmldata);

            try {
                JSONObject jsonObject = IEEE11073BCParser.parse(xmldata);
                handleMeasurement(jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void DeviceAttributes(String dev, String xmldata) {
            d(TAG, "DeviceAttributes: " + xmldata);
            xmlDevice = xmldata;
        }

        @Override
        public void Disassociated(String dev) {
            Log.w("HST", "Disassociated " + dev);
        }

        @Override
        public void Disconnected(String dev) {
            Log.w("HST", "Disconnected " + dev);
        }
    };

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

                    Measurement bodyMass = JsonToMeasurementParser.bodyMass(xmldata);
                    bodyMass.setUser(user);
                    bodyMass.setDevice(mDevice);

                    Measurement bodyFat = JsonToMeasurementParser.bodyFat(xmldata);
                    bodyFat.setUser(user);
                    bodyFat.setDevice(mDevice);

                    Measurement bmi = JsonToMeasurementParser.bmi(xmldata);
                    bmi.setUser(user);
                    bmi.setDevice(mDevice);

                    Measurement rmr = JsonToMeasurementParser.rmr(xmldata);
                    rmr.setUser(user);
                    rmr.setDevice(mDevice);

                    Measurement muscleMass = JsonToMeasurementParser.muscleMass(xmldata);
                    muscleMass.setUser(user);
                    muscleMass.setDevice(mDevice);

                    Measurement visceralFat = JsonToMeasurementParser.visceralFat(xmldata);
                    visceralFat.setUser(user);
                    visceralFat.setDevice(mDevice);

                    Measurement bodyAge = JsonToMeasurementParser.bodyAge(xmldata);
                    bodyAge.setUser(user);
                    bodyAge.setDevice(mDevice);

                    /**
                     * Update UI
                     */
                    bodyMassTextView.setText(decimalFormat.format(bodyMass.getValue()));
                    bodyMassUnitTextView.setText(bodyMass.getUnit());
                    bmiTextView.setText(decimalFormat.format(bmi.getValue()));
                    bodyFatTextView.setText(decimalFormat.format(bodyFat.getValue()));
                    bodyMassTextView.startAnimation(animation);

                    /**
                     * Add relationships, save and send to server
                     */
                    bodyMass.addMeasurement(bodyFat, bmi, rmr, muscleMass, visceralFat, bodyAge);
                    if (MeasurementDAO.getInstance(getApplicationContext()).save(bodyMass))
                        SynchronizationServer.getInstance(getApplicationContext()).run();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // TODO Apenas para debug
    public void d(String TAG, String message) {
        int maxLogSize = 2000;
        for (int i = 0; i <= message.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > message.length() ? message.length() : end;
            android.util.Log.d(TAG, message.substring(start, end));
        }
    }

}
