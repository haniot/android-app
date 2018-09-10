package br.edu.uepb.nutes.blesimpleconnect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.util.UUID;

public class BLEBroadcast {

    //Conexão com os dispositivos (Autoconexão)
    private final String TAG = "BLEBroadcast";
    private Context mContext;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;
    private CallbackBroadcast callbackBroadcast;

    public BLEBroadcast(Context mContext) {
        this.mContext = mContext;
    }

    public boolean connect(final String address, CallbackBroadcast callbackBroadcast) {
        // if (mBluetoothAdapter == null || address == null) return false;

//        // Previously connected device.  Try to reconnect.
//        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
//            if (mBluetoothGatt.connect()) {
//                mConnectionState = STATE_CONNECTING;
//                return true;
//            } else {
//                return false;
//            }
//        }
        this.callbackBroadcast = callbackBroadcast;
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) return false;

        mBluetoothGatt = device.connectGatt(mContext, true, mGattCallback);

        return true;
    }

    //Métodos de monitoramento de serviços (Configurar características...)
    //Envio e recebimento de dados (Callback, broadcast...)
    //Cadastrar dispositivos
    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.i(TAG, "Connection state changed");
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected");
                callbackBroadcast.onStateConnected(gatt.getDevice());
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected");
                callbackBroadcast.onStateDisconnected();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            callbackBroadcast.onServiceDiscovered(gatt.getServices());
            Log.i(TAG, "Services discovered");

            BluetoothGattCharacteristic characteristic = null;
            if (gatt.getServices().contains(getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER)))){
                characteristic = getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER))
                        .getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));

            } else if (gatt.getServices().contains(getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE)))){
                characteristic = getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER))
                        .getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT));

            } else if (gatt.getServices().contains(getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE)))) {
                characteristic = getGattService(UUID.fromString(GattAttributes.SERVICE_SCALE))
                        .getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT));

            }
            //BluetoothGattService gattService = getGattService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER));
            //BluetoothGattService gattService = getGattService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE));

           // if (gattService != null) {
              //  Log.i(TAG, gattService.toString());
                Log.i(TAG, "gattService is not null");
               // BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));
                //BluetoothGattCharacteristic characteristic = gattService.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT));

                if (characteristic != null)
                Log.i(TAG, "characteristic " + characteristic.getValue());

                    Log.i(TAG, "characteristic is not null");
                setCharacteristicNotification(characteristic);
           // }


        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.i(TAG, "onCharacteristicRead()");

            //measurement = JsonToMeasurementParser.heartRate(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            String msg = null;
            try {
                msg = GattHTParser.parse(characteristic).toString();
                //msg = GattHRParser.parse(characteristic).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("MeasurementTO", msg);

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "onCharacteristicChanged()");
            String msg = null;
            try {
                msg = GattHTParser.parse(characteristic).toString();
                //msg = GattHRParser.parse(characteristic).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("MeasurementTO", msg);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite()");
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.i("MeasurementTO", characteristic.getValue().toString());
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.i(TAG, "onDescriptorRead() " + descriptor.getCharacteristic().getUuid());
        }
    };

    public BluetoothGattService getGattService(UUID uuid) {
        if (mBluetoothGatt != null) {
            for (BluetoothGattService gattService : mBluetoothGatt.getServices()) {
                if (gattService.getUuid().equals(uuid)) return gattService;
            }
        }
        return null;
    }

    private BluetoothGattCharacteristic mNotifyCharacteristic;

    public boolean setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (characteristic != null) {
            final int charaProp = characteristic.getProperties();
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                // Se houver uma notificação ativa sobre uma característica, primeiro limpe-a,
                // caso contrário não atualiza o campo de dados na interface do usuário.
                Log.i(TAG, "Propriety Read");
                if (mNotifyCharacteristic != null) {
                    Log.i(TAG, "mNotifyChara is not null");
                    //setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, false);
                    setCharacteristicNotification(mNotifyCharacteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, false);
                    mNotifyCharacteristic = null;
                }
                readCharacteristic(characteristic);
            }
            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                Log.i(TAG, "Propriety Notify");
                mNotifyCharacteristic = characteristic;
                //setCharacteristicNotification(characteristic, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE, true);
                setCharacteristicNotification(characteristic, BluetoothGattDescriptor.ENABLE_INDICATION_VALUE, true);
            }
            return true;
        }
        return false;
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        if (descriptor == null) return false;

        return mBluetoothGatt.writeDescriptor(descriptor);
    }

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, byte[] descriptorValue, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;
        Log.i(TAG, "BLE is not null");
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(descriptorValue);

            Log.i(TAG, "Client Charac Builder");
            Log.i(TAG, String.valueOf(writeDescriptor(descriptor)));
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.i(TAG, "mBluetooth Gatt is null");
            return;
        }
//PIN
//        if (mBluetoothGatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
//            mBluetoothGatt.getDevice().createBond();
//        }

        Log.i(TAG, "read Charac");
        mBluetoothGatt.readCharacteristic(characteristic);
    }

}
