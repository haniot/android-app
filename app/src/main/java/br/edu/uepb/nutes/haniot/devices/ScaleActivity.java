package br.edu.uepb.nutes.haniot.devices;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.BodyCompositionChartActivity;
import br.edu.uepb.nutes.haniot.adapter.BodyCompositionAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.model.BodyFat;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.type.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.devices.base.BaseDeviceActivity;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.ScaleManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;

/**
 * Activity to capture the balance data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ScaleActivity extends BaseDeviceActivity {
    private final String TAG = "ScaleActivity";

    @BindView(R.id.body_mass_textview)
    TextView bodyMassTextView;

    @BindView(R.id.unit_body_mass_textview)
    TextView bodyMassUnitTextView;

    @BindView(R.id.title_body_fat_textview)
    TextView titleBodyFatTextView;

    @BindView(R.id.body_fat_textview)
    TextView bodyFatTextView;

    @BindView(R.id.unit_body_fat_textview)
    TextView unitBodyFatTextView;

    @BindView(R.id.title_bmi_fat_textview)
    TextView titleBmiTextView;

    @BindView(R.id.bmi_textview)
    TextView bmiTextView;

    @BindView(R.id.body_composition_recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = new ScaleManager(this);

        mDevice = mRepository.getDeviceByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.BODY_COMPOSITION);
    }

    @Override
    protected void onResume() {
        ((ScaleManager) manager).setSimpleCallback(scaleDataCallback);
        super.onResume();
    }

    @Override
    protected void onPause() {
        ((ScaleManager) manager).setSimpleCallback(null);
        super.onPause();
    }

    /**
     * @param bodyMass
     * @param bodyMassUnit
     */
    private void setValueLastMeasurement(double bodyMass, String bodyMassUnit) {
        bodyMassTextView.setText(String.valueOf(formatNumber(bodyMass)));
        bodyMassUnit = bodyMassUnit.equals("") ? "kg" : bodyMassUnit;
        bodyMassUnitTextView.setText(bodyMassUnit);
    }

    ScaleDataCallback scaleDataCallback = new ScaleDataCallback() {
        @Override
        public void onMeasurementReceiving(double bodyMass, String bodyMassUnit) {
            runOnUiThread(() -> {
                setValueLastMeasurement(bodyMass, bodyMassUnit);
            });
        }

        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, double bodyMass,
                                          String bodyMassUnit, double bodyFat, String timestamp) {
            Measurement measurement = new Measurement();
            measurement.setUserId(patient.get_id());
            measurement.setTimestamp(timestamp);
            measurement.setType(MeasurementType.BODY_MASS);
            measurement.setUnit("kg");
            measurement.setValue(bodyMass);

            if (bodyFat != 0)
                measurement.setFat(new BodyFat(bodyFat, "%"));

//            if (mDevice != null)
//                measurement.setDeviceId(mDevice.get_id());

            if (bodyMass > 0) {
                synchronizeWithServer(measurement);
                updateUILastMeasurement(measurement, true);
            }
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

    @Override
    protected String getTitleActivity() {
        return getString(R.string.body_weight_scale);
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new BodyCompositionAdapter(this);
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_body_composition;
    }

    @Override
    protected String getMeasurementType() {
        return MeasurementType.BODY_MASS;
    }

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    protected void toggleNoDataMessage(boolean visible) {
        super.toggleNoDataMessage(visible);
        if (visible) {
            bodyMassTextView.setText("");
            bodyMassUnitTextView.setText("");
            bodyFatTextView.setText("");
            unitBodyFatTextView.setText("");
            titleBmiTextView.setVisibility(View.GONE);
            titleBodyFatTextView.setVisibility(View.GONE);
            bmiTextView.setText("");
        } else {
            titleBmiTextView.setVisibility(View.VISIBLE);
            titleBodyFatTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Return value of BMI.
     * formula: bodyMass(kg)/height(m)^2
     *
     * @param bodyMass double
     * @param height   in cm
     * @return double
     */
    private double calcBMI(double bodyMass, double height) {
        height /= 100;
        return bodyMass / (Math.pow(height, 2));
    }

    /**
     * Format value for XX.X
     *
     * @param value double
     * @return String
     */
    private String formatNumber(double value) {
        String result = decimalFormat.format(value);
        return result.equals(".0") ? "00.0" : result;
    }

    /**
     * updateOrSave the UI with the last measurement.
     *
     * @param measurement {@link Measurement}
     */
    protected void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(() -> {
            setValueLastMeasurement(measurement.getValue(), measurement.getUnit());

            String timeStamp = measurement.getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }

            if (measurement.getFat() != null) {
                bodyFatTextView.setText(formatNumber(measurement.getFat().getValue()));
                unitBodyFatTextView.setText(measurement.getFat().getUnit());
                titleBodyFatTextView.setVisibility(View.VISIBLE);
            }

            DisposableManager.add(haniotNetRepository.
                    getAllMeasurementsByType(patient.get_id(),
                            MeasurementType.HEIGHT, "-timestamp", null, null, 1, 1)
                    .subscribe(measurements -> {

                        if (measurements != null && measurements.size() > 0) {
                            double height = measurements.get(0).getValue();
                            double bmi = calcBMI(measurement.getValue(), height);

                            bmiTextView.setText(formatNumber(bmi));
                            titleBmiTextView.setVisibility(View.VISIBLE);
                        }
                    }, error -> {
                        Log.w(TAG, "Error to process BMI");
                    }));

            if (applyAnimation) bodyMassTextView.startAnimation(animation);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), BodyCompositionChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(
                        getResources().getString(R.string.measurementType), ItemGridType.WEIGHT);
                startActivity(it);
                break;
        }
    }
}