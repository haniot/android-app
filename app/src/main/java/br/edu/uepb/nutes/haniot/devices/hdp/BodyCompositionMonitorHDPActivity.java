package br.edu.uepb.nutes.haniot.devices.hdp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BodyCompositionMonitorHDPActivity extends AppCompatActivity {

    @BindView(R.id.connection_state)
    TextView mConnectionStateTextView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.body_composition_hdp_measurement)
    TextView mBodyCompositionHdpTextView;

    private Animation animation;

    private Device mDevice;
    private Session session;

    private Measurement measurement;

//    private Deviceshdp device = new Deviceshdp() {
//        @Override
//        public void connect(String addr) {
//            Log.w("XML", "Antes de Chegar");
//            mDevice = new Device(addr, "Body Composition Monitor");
//            Log.w("XML", "Agora");
//            mConnectionStateTextView.setText(getString(R.string.device_connection_state, getString(R.string.connect)));
//            Log.w("XML", "Foi");
//        }
//
//        @Override
//        public void disconnect() {
//            try {
//                mDevice.setManufacturer(getInfoDevice().get("manufacturer").toString());
//                mDevice.setModelNumber(getInfoDevice().get("model-number").toString());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
////            if (MeasurementDAO.getInstance().save(measurement) > 0)
//                sendMeasurementToServer(measurement);
//
//            mConnectionStateTextView.setText(getString(R.string.device_connection_state, getString(R.string.disconnected)));
//
//            ArrayList<String> measurements = getAuxMeasurement();
//            Collections.reverse(measurements);
//
//            String result = "";
//            for (String medicao: measurements ) {
//                if (!medicao.equals("@")){
//                    result += medicao + " ";
//                }else {
//                    result += "\n";
//                }
//            }
//
//            mBodyCompositionHdpTextView.setText(result);
//            mBodyCompositionHdpTextView.startAnimation(animation);
//
//            Log.w("JSONDisposito", getAuxTimeStamp().toString());
//            Log.w("JSONDisposito", getAuxMeasurement().toString());
//            Log.w("JSONDisposito", getInfoDevice().toString());
//        }
//
//        @Override
//        public void receiveData() {
//            measurement = new Measurement();
//
//            Log.w("XML", getMeasurement().toString());
//            Log.w("XML", getAuxMeasurement().toString());
//            Iterator itr = getMeasurement().keys();
//            while (itr.hasNext()){
//                try {
//                    String name = itr.next().toString();
//                    ArrayList<String> value;
//
//                    switch(name) {
//                        case "kg":
//                            value = (ArrayList<String>) getMeasurement().get("kg");
//                            measurement.setKg(Double.parseDouble(value.get(0)));
//                            measurement.setUnitKg("kg");
//                            break;
//                        case "cm":
//                            value = (ArrayList<String>) getMeasurement().get("cm");
//                            measurement.setCm(Double.parseDouble(value.get(0)));
//                            measurement.setUnitCm("cm");
//                            break;
//                        case "kg m-2":
//                            value = (ArrayList<String>) getMeasurement().get("kg m-2");
//                            measurement.setKg_m2(Double.parseDouble(value.get(0)));
//                            measurement.setUnitKg_m2("kg m-2");
//                            break;
//                        case "cal":
//                            value = (ArrayList<String>) getMeasurement().get("cal");
//                            measurement.setCal(Double.parseDouble(value.get(0)));
//                            measurement.setUnitCal("cal");
//                            break;
//                        case "y":
//                            value = (ArrayList<String>) getMeasurement().get("y");
//                            measurement.setY(Double.parseDouble(value.get(0)));
//                            measurement.setUnitY("y");
//                            break;
//                        case "%":
//                            value = (ArrayList<String>) getMeasurement().get("%");
//                            measurement.setPorcent1(Double.parseDouble(value.get(0)));
//                            measurement.setPorcent2(Double.parseDouble(value.get(1)));
//                            measurement.setPorcent3(Double.parseDouble(value.get(2)));
//                            measurement.setUnitPorcent1("%");
//                            measurement.setUnitPorcent2("%");
//                            measurement.setUnitPorcent3("%");
//                            break;
//                        case "undefined":
//                            value = (ArrayList<String>) getMeasurement().get("undefined");
//                            measurement.setUndefined(Double.parseDouble(value.get(1)));
//                            break;
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//
//
//            measurement.setRegistrationTime(timeStamp(getMeasurement()));
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_composition_monitor_hdp);

        ButterKnife.bind(this);

        mConnectionStateTextView.setText(getString(R.string.device_connection_state, getString(R.string.disconnected)));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.body_composition_measurement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent("com.signove.health.service.HealthService");
        startService(intent);
//        bindService(intent, device.serviceConnection, 0);
        Log.w("HST", "Activity created");

        mBodyCompositionHdpTextView.setText("--");
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        session = new Session(this);

//
//        try {
//            device.agent.Connected("device","xx:xx:xx:xx:xx");
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }

        SynchronizationServer.getInstance(this).run();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDevice == null) {
            mDevice = new Device();
        }
    }

    @Override
    public void onDestroy()
    {
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

    private void sendMeasurementToServer(final Measurement Measurement) {
//        Headers headers = new Headers.Builder()
//                .add("Authorization", "JWT ".concat(session.getTokenLogged()))
//                .build();
//
//        JsonObject jsonObject = new JsonObject();
//        GsonBuilder gson = new GsonBuilder();
//
//        JsonObject jsonDevice = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(mDevice);
//        JsonObject jsonMeasurement = (JsonObject) gson.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create().toJsonTree(Measurement);
//
//        /**
//         * Removes unnecessary data for server
//         */
//        jsonMeasurement.remove("hasSent");
//        jsonMeasurement.remove("userIdDevice");
//
//        /**
//         * Mount the json to send to the server
//         */
//        jsonObject.add("measurement", jsonMeasurement);
//        jsonObject.add("device", jsonDevice);
//
//        Server.getInstance(BodyCompositionMonitorHDPActivity.this).post("healths", jsonObject.toString(), headers, new Server.Callback() {
//            @Override
//            public void onError(JSONObject result) {
//            }
//
//            @Override
//            public void onSuccess(JSONObject result) {
//
//            }
//        });
    }
}
