package br.edu.uepb.nutes.haniot.devices;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.BloodPresssureChartActivity;
import br.edu.uepb.nutes.haniot.adapter.BloodPressureAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.devices.base.BaseDeviceActivity;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;

public class BloodPressureActivity extends BaseDeviceActivity {
    private final String TAG = "PressureActivity";

    @BindView(R.id.unit_blood_pressure_textviewt)
    TextView mUnitBloodPressureTextView;

    @BindView(R.id.blood_pressure_dia_textviewt)
    TextView mBloodPressureDiaTextView;

    @BindView(R.id.blood_pressure_sys_textviewt)
    TextView mBloodPressureSysTextView;

    @BindView(R.id.blood_pressure_pulse_textviewt)
    TextView mBloodPressurePulseTextView;

    @BindView(R.id.unit_pulse_textviewt)
    TextView mUnitPulseTextView;

    @BindView(R.id.blood_pressure_recyclerviewt)
    RecyclerView mRecyclerView;

    @BindView(R.id.view_pulse)
    CircularProgressBar mCircularPulseProgressBar;

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    protected void toggleNoDataMessage(boolean visible) {
        super.toggleNoDataMessage(visible);
        if (visible) {
            mUnitBloodPressureTextView.setText("");
            mBloodPressureDiaTextView.setText("");
            mBloodPressureSysTextView.setText("");
            mBloodPressurePulseTextView.setText("");
            mUnitPulseTextView.setText("");
        } else {
            mUnitPulseTextView.setText(getString(R.string.unit_per_minutes));
        }
    }

    @Override
    protected String getMeasurementType() {
        return MeasurementType.BLOOD_PRESSURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_blood_pressure;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new BloodPressureAdapter(this);
    }

    @Override
    protected String getTitleActivity() {
        return getString(R.string.blood_pressure);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

//
//    @Override
//    protected void toggleNoDataMessage(boolean visible) {
//        super.toggleNoDataMessage(visible);
//        if (visible) {
//
//        }
//    }

    protected void updateConnectionState() {
        super.updateConnectionState();

        runOnUiThread(() -> {
            mCircularPulseProgressBar.setProgress(0);
            mCircularPulseProgressBar.setProgressWithAnimation(100); // Default animate duration = 1500ms

            if (mConnected) {
                mCircularPulseProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                mCircularPulseProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButtonDanger));
            } else {
                mCircularPulseProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorButtonDanger));
                mCircularPulseProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
            }
        });
    }

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
            mUnitBloodPressureTextView.setText(measurement.getUnit());
            mBloodPressureDiaTextView.setText(String.valueOf(measurement.getDiastolic()));
            mBloodPressureSysTextView.setText(String.valueOf(measurement.getSystolic()).concat("/"));

            mBloodPressurePulseTextView.setText(String.valueOf(measurement.getPulse()));

            String timeStamp = measurement.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }

            if (applyAnimation) {
                mBloodPressureDiaTextView.startAnimation(animation);
                mBloodPressureSysTextView.startAnimation(animation);
                mBloodPressurePulseTextView.startAnimation(animation);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), BloodPresssureChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType), ItemGridType.BLOOD_PRESSURE);
                startActivity(it);
                break;
        }
    }
}
