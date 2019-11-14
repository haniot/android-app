package br.edu.uepb.nutes.haniot.devices;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.charts.HeartRateChartActivity;
import br.edu.uepb.nutes.haniot.adapter.HeartRateAdapter;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.type.DeviceType;
import br.edu.uepb.nutes.haniot.data.model.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import br.edu.uepb.nutes.haniot.devices.base.BaseDeviceActivity;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.HeartRateManager;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.HeartRateDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;

/**
 * Activity to capture the heart rate data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HeartRateActivity extends BaseDeviceActivity implements GenericDialogFragment.OnClickDialogListener {
    private final String TAG = "HeartRateActivity";
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_DEVICE_INFORMATIONS = "device_informations";
    public final int DIALOG_SAVE_DATA = 1;

    private ObjectAnimator heartAnimation;

    @BindView(R.id.heart_rate_textview)
    TextView mHeartRateTextView;

    @BindView(R.id.heart_rate_unit_textview)
    TextView mUnitHeartRateTextView;

    @BindView(R.id.heart_rate_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.heart_imageview)
    ImageView mHeartImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        manager = new HeartRateManager(this);
        ((HeartRateManager) manager).setSimpleCallback(heartRateDataCallback);

        mDevice = mRepository.getDeviceByType(appPreferencesHelper.getUserLogged().get_id(), DeviceType.HEART_RATE);
    }

    HeartRateDataCallback heartRateDataCallback = new HeartRateDataCallback() {
        @Override
        public void onMeasurementReceived(@NonNull BluetoothDevice device, int heartRate, String timestamp) {
            Measurement measurement = new Measurement();
            measurement.setUser_id(patient.get_id());
            measurement.setUnit("bpm");
            measurement.setType(MeasurementType.HEART_RATE);
            measurement.setTimestamp(DateUtils.getCurrentDateTimeUTC());
            List<HeartRateItem> heartRateItems = new ArrayList<>();
            heartRateItems.add(new HeartRateItem(heartRate, DateUtils.getCurrentDateTimeUTC()));
            measurement.setDataset(heartRateItems);
//            measurement.setDataset(heartRateItems);

//            if (mDevice != null)
//                measurement.setDeviceId(mDevice.get_id());

            saveMeasurement(measurement);
            updateUILastMeasurement(measurement, true);
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
     * Initialize components
     */
    protected void initComponents() {
        initAnimation();
        super.initComponents();
    }

    /**
     * Animation for heart
     */
    private void initAnimation() {
        /**
         * Setting heartAnimation in heart imageview
         */
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(mHeartImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        heartAnimation.setDuration(500);
        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);
    }

    /**
     * Enable/Disable display messgae no data.
     *
     * @param visible boolean
     */
    protected void toggleNoDataMessage(boolean visible) {
        super.toggleNoDataMessage(visible);
        if (visible) {
            mHeartRateTextView.setText("");
            mUnitHeartRateTextView.setText("");
            mHeartImageView.setVisibility(View.GONE);
        } else {
            mHeartImageView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * updateOrSave the UI with the last measurement.
     *
     * @param measurement {@link Measurement}
     */
    protected void updateUILastMeasurement(Measurement measurement, boolean applyAnimation) {
        if (measurement == null) return;

        runOnUiThread(() -> {
            if (measurement.getDataset() != null && measurement.getDataset().isEmpty()) return;
            mHeartRateTextView.setText(String.format("%03d", (int) measurement.getDataset().get(0).getValue()));
            mUnitHeartRateTextView.setText(measurement.getUnit());

            String timeStamp = measurement.getDataset().get(0).getTimestamp();

            if (DateUtils.isToday(DateUtils.convertDateTime(timeStamp).getTime())) {
                mDateLastMeasurement.setText(R.string.today_text);
            } else {
                mDateLastMeasurement.setText(DateUtils.convertDateTimeUTCToLocale(
                        timeStamp, "MMMM dd, EEE"
                ));
            }
            mHeartImageView.setVisibility(View.VISIBLE);
        });
        if (applyAnimation) mHeartRateTextView.startAnimation(animation);
    }

    @Override
    protected String getMeasurementType() {
        return MeasurementType.HEART_RATE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_heart_rate;
    }

    @Override
    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    protected BaseAdapter getAdapter() {
        return new HeartRateAdapter(this);
    }

    @Override
    protected String getTitleActivity() {
        return getString(R.string.heart_rate);
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chart_floating_button:
                startActivity(new Intent(getApplicationContext(), HeartRateChartActivity.class));
                break;
            case R.id.add_floating_button:
                Intent it = new Intent(getApplicationContext(), AddMeasurementActivity.class);
                appPreferencesHelper.saveInt(getResources().getString(R.string.measurementType),
                        ItemGridType.HEART_RATE);
                startActivity(it);
                break;
        }
    }

    @Override
    public void onClickDialog(int id, int button) {
//        if (id == DIALOG_SAVE_DATA && button == DialogInterface.BUTTON_POSITIVE) {
//            /**
//             * Add relations
//             */
//            measurementSave.setUser(appPreferencesHelper.getUserLogged());
//            if (mDevice != null)
//                measurementSave.setDevice(mDevice);
//
//            /**
//             * Save in local
//             * Send to server saved successfully
//             */
//            if (measurementDAO.save(measurementSave)) {
//                synchronizeWithServer();
//                loadData();
//            }
//        }
    }
}