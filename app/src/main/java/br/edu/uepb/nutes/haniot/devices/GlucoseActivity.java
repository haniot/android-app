package br.edu.uepb.nutes.haniot.devices;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.GlucoseChartActivity;
import br.edu.uepb.nutes.haniot.adapter.GlucoseAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.devices.base.BaseDeviceActivity;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.GlucoseManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodGlucoseDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;

/**
 * Activity to capture the glucose data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.2
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseActivity extends BaseDeviceActivity {
    private final String TAG = "GlucoseActivity";

    @BindView(R.id.glucose_textview)
    TextView mBloodGlucoseTextView;

    @BindView(R.id.unit_glucose_textview)
    TextView mUnitBloodGlucoseTextView;

    @BindView(R.id.glucose_recyclerview)
    RecyclerView mRecyclerView;

    private ProgressDialog progressDialog;
    private boolean isGetAllMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissions();

        manager = new GlucoseManager(this);
        ((GlucoseManager) manager).setSimpleCallback(glucoseDataCallback);

        mDevice = deviceDAO.getByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.GLUCOMETER);

        initComponents();

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private BloodGlucoseDataCallback glucoseDataCallback = new BloodGlucoseDataCallback() {
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

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, int glucose, String meal, String timestamp) {
            Measurement measurement = new Measurement();
            measurement.setType(MeasurementType.BLOOD_GLUCOSE);
            measurement.setValue(glucose);
            measurement.setMeal(meal);
            measurement.setTimestamp(timestamp);
//            if (mDevice != null) measurement.setDeviceId(mDevice.get_id());
            measurement.setUserId(patient.get_id());

            synchronizeWithServer(measurement);
            updateUILastMeasurement(measurement, true);
        }
    };

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    protected void toggleNoDataMessage(boolean visible) {
        super.toggleNoDataMessage(visible);
        if (visible) {
            mBloodGlucoseTextView.setText("");
            mUnitBloodGlucoseTextView.setText("");
        } else {

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

//    /**
//     * Recover text from context value meal.
//     *
//     * @param contextMeasurements
//     * @return String
//     */
//    public String mealToString(List<ContextMeasurement> contextMeasurements) {
//        for (ContextMeasurement c : contextMeasurements) {
//            if (c.getTypeId() == ContextMeasurementType.GLUCOSE_MEAL)
//                return ContextMeasurementValueType.getString(this, c.getValueId());
//        }
//        return "";
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
            mBloodGlucoseTextView.setText(valueToString(measurement));
            mUnitBloodGlucoseTextView.setText(measurement.getUnit());

            String timeStamp = measurement.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }
            if (applyAnimation) mBloodGlucoseTextView.startAnimation(animation);
        });
    }

    @Override
    protected String getMeasurementType() {
        return MeasurementType.BLOOD_GLUCOSE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_glucose;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new GlucoseAdapter(this);
    }

    @Override
    protected String getTitleActivity() {
        return getString(R.string.glucose);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    printMessage(getString(R.string.bluetooth_disabled));
                } else if (state == BluetoothAdapter.STATE_ON) {
//                    showMessage(-1);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), GlucoseChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_GLUCOSE);
                startActivity(it);
                break;
        }
    }
}
