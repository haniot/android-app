package br.edu.uepb.nutes.haniot.activity.devices;

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
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.service.BluetoothLeService;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity to capture the thermometer data.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BloodPressureActivity extends AppCompatActivity {
    private final String LOG = "BloodPressureActivity";

    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private Menu mMenu;
    private String mDeviceAddress;
    Animation animation;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.connection_state)
    TextView mConnectionStateTextView;

    @BindView(R.id.blood_pressure_measurement)
    TextView mBloodPressureTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blood_pressure);
        ButterKnife.bind(this);

        mDeviceAddress = "A0:E5:CF:AD:27:54";

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.blood_pressure_measurement);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mConnectionStateTextView.setText(getString(R.string.device_connection_state, getString(R.string.disconnected)));

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);


        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());

        if (mBluetoothLeService != null) {
            boolean result = mBluetoothLeService.connect(mDeviceAddress);
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

    private void updateConnectionState(final String state) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionStateTextView.setText(getString(R.string.device_connection_state, state));
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
                    mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, false);
                    mNotifyCharacteristic = null;
                }
                mBluetoothLeService.readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                mNotifyCharacteristic = characteristic;
                mBluetoothLeService.setCharacteristicNotification(characteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, true);
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
                Log.e(LOG, "Unable to initialize Bluetooth");
                finish();
            }
            // Conecta-se automaticamente ao dispositivo após a inicialização bem-sucedida.
            mBluetoothLeService.connect(mDeviceAddress);
            Log.i(LOG, mDeviceAddress);
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
                updateConnectionState(getString(R.string.connected));
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(getString(R.string.disconnected));
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Mostra todos os serviços e características suportados.
                BluetoothGattService gattService = null;
                for (BluetoothGattService gService : mBluetoothLeService.getSupportedGattServices()) {
                    Log.i(LOG, gService.getUuid() + " | " + gService.getType());
                    if (gService.getUuid().equals(UUID.fromString(GattAttributes.SERVICE_BLOOD_PRESSURE))) {
                        gattService = gService;
                        break;
                    }
                }

                if (gattService != null) {
                    BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_BLOOD_PRESSURE_MEASUREMENT));
                    if (characteristic != null)
                        setCharacteristicNotification(characteristic);
                }
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String measurement = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);

                mBloodPressureTextView.setText(measurement);
                mBloodPressureTextView.startAnimation(animation);
            }
        }
    };

}
