package br.edu.uepb.nutes.haniot.devices;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.TemperatureChartActivity;
import br.edu.uepb.nutes.haniot.adapter.TemperatureAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.type.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import br.edu.uepb.nutes.haniot.devices.base.BaseDeviceActivity;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ThermometerManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;

/**
 * Activity to capture the thermometer data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ThermometerActivity extends BaseDeviceActivity {
    private final String TAG = "ThermometerActivity";

    @BindView(R.id.temperature_textview)
    TextView mTemperatureTextView;

    @BindView(R.id.unit_temperature_textview)
    TextView mUnitTemperatureTextView;

    @BindView(R.id.temperature_recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new ThermometerManager(this);
        ((ThermometerManager) manager).setSimpleCallback(temperatureDataCallback);

//        mDevice = mRepository.getDeviceByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.THERMOMETER);
    }

    private TemperatureDataCallback temperatureDataCallback = new TemperatureDataCallback() {
        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, double temp, String unit, String timestamp) {
            Measurement measurement = new Measurement();
            measurement.setTimestamp(DateUtils.getCurrentDateTimeUTC());
            measurement.setValue(temp);
            measurement.setUnit(unit);
//            if (mDevice != null) measurement.setDeviceId(mDevice.get_id());
            measurement.setUserId(patient.get_id());
            measurement.setType(MeasurementType.BODY_TEMPERATURE);

            saveMeasurement(measurement);
        }

        @Override
        public void onConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            mConnected = true;
            updateConnectionState();
        }

        @Override
        public void onDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            mConnected = false;
            updateConnectionState();
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
            mTemperatureTextView.setText("");
            mUnitTemperatureTextView.setText("");
        }
    }
    @Override
    protected String getMeasurementType() {
        return MeasurementType.BODY_TEMPERATURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_thermometer;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new TemperatureAdapter(this);
    }

    @Override
    protected String getTitleActivity() {
        return getString(R.string.temperature);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    /**
     * updateOrSave the UI with the last measurement.
     *
     * @param m {@link Measurement}
     */
    protected void updateUILastMeasurement(Measurement m, boolean applyAnimation) {
        if (m == null) return;

        runOnUiThread(() -> {
            mTemperatureTextView.setText(decimalFormat.format(m.getValue()));
            mUnitTemperatureTextView.setText(m.getUnit());

            String timeStamp = m.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }
            if (applyAnimation) mTemperatureTextView.startAnimation(animation);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), TemperatureChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType), ItemGridType.TEMPERATURE);
                startActivity(it);
                break;
        }
    }
}
