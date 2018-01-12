package br.edu.uepb.nutes.haniot.devices;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.adapter.HeartRateAdapter;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the heart rate data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HeartRateActivity extends AppCompatActivity implements View.OnClickListener, HeartRateAdapter.OnItemClickListener {
    private final String TAG = "HeartRateActivity";
    public static final String EXTRA_DEVICE_ADDRESS = "device_address";
    public static final String EXTRA_DEVICE_INFORMATIONS = "device_informations";

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private String mDeviceAddress;
    private String[] deviceInformations;

    private ObjectAnimator heartAnimation;
    private RecyclerView.Adapter mAdapter;
    private List<Measurement> listMeasurement;
    private Session session;
    private MeasurementDAO MeasurementDAO;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.floating_button_record_play)
    FloatingActionButton mPlayFloatingActionButton;

    @BindView(R.id.heart_rate_measurement)
    TextView mHeartRateTextView;

    @BindView(R.id.view_circle)
    CircularProgressBar mCircularProgressBar;

    @BindView(R.id.heart_imageview)
    ImageView mHeartImageView;

    @BindView(R.id.collapsi_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.history_heart_rate_listview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);
        ButterKnife.bind(this);

        initializeToolBar();
        session = new Session(this);
        MeasurementDAO = MeasurementDAO.getInstance(this);

        mPlayFloatingActionButton.setOnClickListener(this);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        /**
         * Recycler view
         */
        listMeasurement = new ArrayList<>();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new HeartRateAdapter(listMeasurement, this, this);
        mRecyclerView.setAdapter(mAdapter);

        iniAnimations();

        mDeviceAddress = getIntent().getStringExtra(EXTRA_DEVICE_ADDRESS);
        deviceInformations = getIntent().getStringArrayExtra(EXTRA_DEVICE_INFORMATIONS);

        SynchronizationServer.getInstance(this).run();
    }

    private void iniAnimations() {
        /**
         * Setting animation in heart imageview
         */
        heartAnimation = ObjectAnimator.ofPropertyValuesHolder(mHeartImageView,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f), PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        heartAnimation.setDuration(500);
        heartAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        heartAnimation.setRepeatMode(ObjectAnimator.REVERSE);
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
                    mCollapsingToolbarLayout.setTitle(getString(R.string.heart_rate));
                    mPlayFloatingActionButton.setVisibility(View.GONE);
                    isShow = true;
                } else if (isShow) {
                    mPlayFloatingActionButton.setVisibility(View.VISIBLE);
                    mCollapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            mBluetoothLeService.connect(mDeviceAddress);
        }
        refreshRecyclerView();
    }

    private void refreshRecyclerView() {
        listMeasurement.clear();

        for (Measurement m : MeasurementDAO.list(MeasurementType.HEART_RATE, session.getIdLogged(), 0, 20)) {
            listMeasurement.add(m);
            mAdapter.notifyDataSetChanged();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBluetoothLeService.disconnect();
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
                    heartAnimation.start();
                    mCircularProgressBar.setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
                    mCircularProgressBar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAlertDanger));
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
                updateConnectionState(true);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(false);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                BluetoothGattService gattService = mBluetoothLeService.getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE));

                if (gattService != null) {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT));
                    if (characteristic != null)
                        setCharacteristicNotification(characteristic);
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                try {
                    Measurement measurement = JsonToMeasurementParser.heartRate(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                    Log.i("MeasurementTO", measurement.toString());

                    mHeartRateTextView.setText(String.format("%03d", (int) measurement.getValue()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floating_button_record_play:
                Intent intent = new Intent(this, RecordHeartRateActivity.class);
                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, mDeviceAddress);
                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, deviceInformations);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(Measurement item) {
        Log.i("onItemClick()", item.toString());
    }
}
