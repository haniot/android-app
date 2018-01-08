package br.edu.uepb.nutes.haniot.devices;

import android.content.Intent;
import android.os.Bundle;
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

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the balance data.
 *
 * @author Lucas Barbosa
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BloodPressureHDPActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.view_pulse)
    CircularProgressBar mCircularPulse;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
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
    private MeasurementDAO MeasurementDAO;
    private DeviceDAO deviceDAO;

    Measurement measurement;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_blood_pressure_hdp);
        ButterKnife.bind(this);

        initializeToolBar();

        Intent intent = new Intent("com.signove.health.service.HealthService");
        startService(intent);
//        bindService(intent, device.serviceConnection, 0);
        Log.w("HST", "Activity created");

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        session = new Session(this);
        MeasurementDAO = MeasurementDAO.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);

//		try {
//			device.agent.Connected("device","xx:xx:xx:xx:xx");
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}

        SynchronizationServer.getInstance(this).run();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        device.finalize();
//        unbindService(device.serviceConnection);
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

//    private Deviceshdp device = new Deviceshdp() {
//        @Override
//        public void connect(String addr) {
//            if (mDevice == null) {
//                mDevice = deviceDAO.get(addr, session.getIdLogged());
//                if (mDevice == null) {
//                    try {
//                        mDevice = new Device(addr, "BLOOD PRESSURE MONITOR",
//                                getInfoDevice().get("manufacturer").toString(),
//                                getInfoDevice().get("model-number").toString(),
//                                5, session.getIdLogged());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    deviceDAO.save(mDevice);
//                }
//            }
//
//            updateConnectionState(true);
//        }
//
//        @Override
//        public void disconnect() {
//            updateConnectionState(false);
//
//            mBloodPressureSysTextView.setText(measurement.getSystolic()+"");
//            mBloodPressureDiaTextView.setText(measurement.getDiastolic()+"");
//            mBloodPressurePulseTextView.setText(measurement.getHeartFate()+"");
//
//            /**
//             * Save and send to server
//             */
//            if (MeasurementDAO.save(measurement))
//                sendMeasurementToServer();
//        }
//
//        @Override
//        public void receiveData() {
//            measurement = new Measurement();
//
//            Log.w("XML", getMeasurement().toString());
//            Log.w("XML", getAuxMeasurement().toString());
//
//            Iterator itr = getMeasurement().keys();
//            while (itr.hasNext()) {
//                try {
//                    String name = itr.next().toString();
//                    ArrayList<String> value;
//
//                    switch (name) {
//                        case "mmHg":
//                            value = (ArrayList<String>) getMeasurement().get("mmHg");
//                            measurement.setSystolic((int) Double.parseDouble(value.get(0)));
//                            measurement.setUnitSystolic("mmHg");
//                            measurement.setDiastolic((int) Double.parseDouble(value.get(1)));
//                            measurement.setUnitDiastolic("mmHg");
//                            measurement.setFrequency((int) Double.parseDouble(value.get(2)));
//                            measurement.setUnitFrequency("mmHg");
//                            break;
//                        case "bpm":
//                            value = (ArrayList<String>) getMeasurement().get("bpm");
//                            measurement.setHeartFate((int) Double.parseDouble(value.get(0)));
//                            measurement.setUnitHeartFate("bpm");
//                            break;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            measurement.setRegistrationTime(timeStamp(getMeasurement()));
//
//        }
//    };

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
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbarLayout.setTitle(getString(R.string.scale_measurement));
                    isShow = true;
                } else if (isShow) {
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    /**
     * Send measurement to server.
     */
    private void sendMeasurementToServer() {
        if (!ConnectionUtils.internetIsEnabled(this))
            return;

//        List<Measurement> MeasurementNotSent = MeasurementDAO.listAll(mDevice.getAddress(), session.getIdLogged());
//
//        /**
//         * Get the required user token in request authentication
//         */
//        Headers headers = new Headers.Builder()
//                .add("Authorization", "JWT ".concat(session.getTokenLogged()))
//                .build();
//
//        for (final Measurement m : MeasurementNotSent) {
//            JsonObject jsonObject = new JsonObject();
//            GsonBuilder gson = new GsonBuilder();
//
//            JsonObject jsonDevice = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(mDevice);
//            JsonObject jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(m);
//
//            /**
//             * Removes unnecessary data for server
//             */
//            jsonMeasurement.remove("hasSent");
//
//            /**
//             * Mount the json to send to the server
//             */
//            jsonObject.add("measurement", jsonMeasurement);
//            jsonObject.add("device", jsonDevice);
//
//            Server.getInstance(this).post("healths", jsonObject.toString(), headers, new Server.Callback() {
//                @Override
//                public void onError(JSONObject result) {
//                }
//
//                @Override
//                public void onSuccess(JSONObject result) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                        }
//                    });
//                }
//            });
//        }
    }
}