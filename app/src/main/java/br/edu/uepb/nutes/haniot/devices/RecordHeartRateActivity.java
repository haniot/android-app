package br.edu.uepb.nutes.haniot.devices;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.fragment.GenericDialogFragment;
import br.edu.uepb.nutes.haniot.fragment.RealTimeFragment;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
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
    private final int REQUEST_ENABLE_BLUETOOTH = 1;
    private final int REQUEST_ENABLE_LOCATION = 2;
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
    private AppPreferencesHelper appPreferencesHelper;
    private MeasurementDAO MeasurementDAO;
    private DeviceDAO deviceDAO;
    private int fcMinimum, fcMaximum, fcAccumulate, fcTotal;
    private long registrationTimeStart, durationRegistration;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    List<Measurement> data = new ArrayList<>();
    @BindView(R.id.heart_rate_textview)
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

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    RealTimeFragment realTimeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate_record);

        ButterKnife.bind(this);
        checkPermissions();
        initComponents();

        mButtonRecordPausePlay.setOnClickListener(this);
        mButtonRecordStop.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        deviceDAO = DeviceDAO.getInstance(this);
        MeasurementDAO = MeasurementDAO.getInstance(this);
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
        messageError.setOnClickListener(v -> checkPermissions());

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    private void showMessage(@StringRes int str) {
        if (str != -1) {
            String message = getString(str);

            messageError.setText(message);
            runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                boxMessage.setVisibility(View.VISIBLE);
            });
        } else {
            runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_out));
                boxMessage.setVisibility(View.INVISIBLE);
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);

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
        unregisterReceiver(mReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
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
                    //sendMeasurements(); //For tests
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
        runOnUiThread(() -> {
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
                Log.e(TAG, "Unable to initializeCharacteristic Bluetooth");
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

    public String getAction() {
        return action;
    }

    /**
     * Manipula vários eventos desencadeados pelo Serviço.
     * <p>
     * ACTION_GATT_CONNECTED: conectado a um servidor GATT.
     * ACTION_GATT_DISCONNECTED: desconectado a um servidor GATT.
     * ACTION_GATT_SERVICES_DISCOVERED: serviços GATT descobertos.
     * ACTION_DATA_AVAILABLE: recebeu dados do dispositivo. Pode ser resultado de operações de leitura ou notificação.
     */
    String action;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();

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
                if (!mConnected) {
                    mConnected = true;
                    updateConnectionState(mConnected);
                }
//
//                new Handler().postDelayed(() -> {
//                    try {
//                        Measurement measurement = JsonToMeasurementParser.heartRate(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
//                        Log.i("MeasurementTO", measurement.toString());
//
//                        sendMeasurements(measurement);
//                        int bpm = (int) measurement.getValue();
//                        mHeartRateTextView.setText(String.format("%03d", (int) measurement.getValue()));
//
//                        fcAccumulate += bpm;
//                        fcMinimum = (bpm > 0 && bpm < fcMinimum) ? bpm : fcMinimum;
//                        fcMaximum = (bpm > fcMaximum) ? bpm : fcMaximum;
//                        fcTotal++;
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }, 100);
            }
        }
    };

    @Override
    public void onClickDialog(int id, int button) {
        if (button == DialogInterface.BUTTON_POSITIVE) {
            //TODO CONSERTAR!!! Deverá ser salvo como um treino
        }
        // close activity
        finish();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    showMessage(R.string.bluetooth_disabled);

                } else if (state == BluetoothAdapter.STATE_ON) {
                    showMessage(-1);
                }
            }
        }
    };

    /**
     * Performs routine for data synchronization with server.
     */
    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

    /**
     * Send Measurements for chart.
     *
     * @param measurement
     */
    public void sendMeasurements(Measurement measurement) {
        data.add(measurement);
        realTimeFragment.sendMeasurement(measurement);
    }

    /**
     * Initialize components
     */
    private void initComponents() {
        initToolBar();
        initFragments();
    }

    private void initFragments() {
        realTimeFragment = RealTimeFragment.newInstance(this);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_realtime, realTimeFragment);
        transaction.commit();
    }

    /**
     * Initialize ToolBar
     */
    private void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    /**
     * Checks if you have permission to use.
     * Required bluetooth ble and location.
     */
    public void checkPermissions() {
        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            requestBluetoothEnable();
        } else if (!hasLocationPermissions()) {
            requestLocationPermission();
        }
    }

    /**
     * Request Bluetooth permission
     */
    private void requestBluetoothEnable() {
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                REQUEST_ENABLE_BLUETOOTH);
    }

    /**
     * Checks whether the location permission was given.
     *
     * @return boolean
     */
    public boolean hasLocationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Request Location permission.
     */
    protected void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ENABLE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If request is cancelled, the result arrays are empty.
        if ((requestCode == REQUEST_ENABLE_LOCATION) &&
                (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            requestLocationPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode != Activity.RESULT_OK) {
                requestBluetoothEnable();
            } else {
                requestLocationPermission();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
