package br.edu.uepb.nutes.haniot.devices;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity for recording heart rate.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class RecordHeartRateActivity extends AppCompatActivity implements View.OnClickListener, GenericDialogFragment.OnClickDialogListener {
    private final String TAG = "RecordHeartRateActivity";
    public final int DIALOG_SAVE_DATA = 1;

    private long lastPause = 0;

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Device mDevice;
    private String mDeviceAddress;
    private String[] deviceInformations;
    private ObjectAnimator heartAnimation;
    private boolean isChronometerRunnig;
    private Session session;
    private MeasurementDAO MeasurementDAO;
    private DeviceDAO deviceDAO;
    private int fcMinimum, fcMaximum, fcAccumulate, fcTotal;
    private long registrationTimeStart, durationRegistration;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.heart_rate_measurement)
    TextView mHeartRateTextView;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.heart_imageview)
    ImageView mHeartImageView;

    @BindView(R.id.chronometer_heart_rate)
    Chronometer mChronometer;

    @BindView(R.id.floating_button_record_pause_play)
    FloatingActionButton mButtonRecordPausePlay;

    @BindView(R.id.floating_button_record_stop)
    FloatingActionButton mButtonRecordStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_heart_rate);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.heart_rate);

        mButtonRecordPausePlay.setOnClickListener(this);
        mButtonRecordStop.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        session = new Session(this);
        deviceDAO = DeviceDAO.getInstance(this);
        MeasurementDAO = MeasurementDAO.getInstance(this);
        registrationTimeStart = DateUtils.getCurrentDatetime();
        lastPause = 0;
        fcMinimum = Integer.MAX_VALUE;
        fcMaximum = Integer.MIN_VALUE;

        /**
         * Setting animation in heart imageview
         */
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(mHeartImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        heartAnimation.setDuration(500);
        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);

        mDeviceAddress = getIntent().getStringExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS);
        deviceInformations = getIntent().getStringArrayExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mDevice == null) {
            mDevice = deviceDAO.get(mDeviceAddress, session.getIdLogged());

            if (mDevice == null) {
                mDevice = new Device(mDeviceAddress, "HEART RATE SENSOR", deviceInformations[0], deviceInformations[1], 4, session.getUserLogged());

//                if (!deviceDAO.save(mDevice)) finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_button_record_pause_play:
                if (isChronometerRunnig) {
                    pauseChronometer();
                    mButtonRecordPausePlay.setImageResource(R.drawable.ic_action_play_arrow);
                } else {
                    mButtonRecordPausePlay.setImageResource(R.drawable.ic_action_pause);
                    startChronometer();
                }
                break;
            case R.id.floating_button_record_stop:
                stopChronometer();
                /**
                 * Open dialog
                 */
                GenericDialogFragment dialogSaveData = GenericDialogFragment.newDialog(DIALOG_SAVE_DATA,
                        R.string.title_save_captured_data, R.string.message_save_captured_data, 0, new int[]{R.string.bt_save, R.string.bt_discard}, null);
                dialogSaveData.show(getSupportFragmentManager());
                break;
            default:
                break;
        }
    }

    /**
     * Stop Chronometer
     */
    private void stopChronometer() {
        lastPause = SystemClock.elapsedRealtime() - mChronometer.getBase();
        isChronometerRunnig = false;
        mChronometer.stop();

        durationRegistration = lastPause;
    }

    private void pauseChronometer() {
        isChronometerRunnig = false;
        mChronometer.stop();
    }

    /**
     * Start Chronometer
     */
    private void startChronometer() {
        mChronometer.setBase(SystemClock.elapsedRealtime() - lastPause);
        isChronometerRunnig = true;
        mChronometer.start();
    }

    private void updateConnectionState(final boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCircularProgressBar.setProgress(0);
                mCircularProgressBar.setProgressWithAnimation(100); // Default animate duration = 1500ms

                if (isConnected) {
                    heartAnimation.start();
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));

                    startChronometer();
                } else {
                    heartAnimation.pause();
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                }
            }
        });
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        return intentFilter;
    }

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // Se houver uma notificação ativa sobre uma característica, primeiro limpe-a,
                // caso contrário não atualiza o campo de dados na interface do usuário.
                if (mNotifyCharacteristic != null) {
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(characteristic, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, true);
            }
            return true;
        }
        return false;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Conecta-se automaticamente ao dispositivo após a inicialização bem-sucedida.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /**
     * Manipula vários eventos desencadeados pelo Serviço.
     * <p>
     * ACTION_GATT_CONNECTED: conectado a um servidor GATT.
     * ACTION_GATT_DISCONNECTED: desconectado a um servidor GATT.
     * ACTION_GATT_SERVICES_DISCOVERED: serviços GATT descobertos.
     * ACTION_DATA_AVAILABLE: recebeu dados do dispositivo. Pode ser resultado de operações de leitura ou notificação.
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(mConnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothGattService gattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE));

                if (gattService != null) {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT));
                    if (characteristic != null)
                        setCharacteristicNotification(characteristic);
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                final Measurement measurement = jsonToMeasuremnt(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.i("MeasurementTO", measurement.toString());
                if(!mConnected) {
                    mConnected = true;
                    updateConnectionState(mConnected);
                }

                // display data
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int bpm = measurement.getValue() == null ? 0 : new Integer(measurement.getValue());
                        mHeartRateTextView.setText(String.format("%03d", bpm));

                        fcAccumulate += bpm;
                        fcMinimum = (bpm > 0 && bpm < fcMinimum) ? bpm : fcMinimum;
                        fcMaximum = (bpm > fcMaximum) ? bpm : fcMaximum;
                        fcTotal++;
                    }
                }, 1000);
            }
        }
    };

    /**
     * Convert json to Measurement object.
     *
     * @param json
     * @return Measurement
     */
    private Measurement jsonToMeasuremnt(String json) {
        Measurement measurement = null;

        try {
            JSONObject jsonObject = new JSONObject(json);

            measurement = new Measurement(
                    jsonObject.getString("heartRate"),
                    jsonObject.getString("heartRateUnit"),
                    jsonObject.getLong("timestamp"),
                    MeasurementType.HEART_RATE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return measurement;
    }

    @Override
    public void onClickDialog(int id, int button) {
        if (button == DialogInterface.BUTTON_POSITIVE) {
            //TODO CONCERTAR!!! Deverá ser salvo como um treino
//            /**
//             * Total de bpms medidos
//             */
//            if (fcTotal > 0) {
//                /**
//                 * Prepare object for save data in local and send to server
//                 */
//                Measurement Measurement = new Measurement();
//                Measurement.setRegistrationTime(registrationTimeStart);
//                Measurement.setDurationTime(durationRegistration);
//                Measurement.setBpms(String.valueOf(bpms));
//                Measurement.setFcMinimum(fcMinimum);
//                Measurement.setFcMaximum(fcMaximum);
//                Measurement.setFcAverage(fcAccumulate / fcTotal);
//
//                /**
//                 * Save in local
//                 * Send to server saved successfully
//                 */
//                Measurement.setDeviceAddress(mDevice.getAddress());
//                Measurement.setUserId(session.getIdLogged());
//
//                if (MeasurementDAO.save(Measurement))
//                    sendMeasurementToServer();
//            }
        }
        // close activity
        finish();
    }
}
