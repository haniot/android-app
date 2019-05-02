package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.HeartRateDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class HeartRateManager extends BluetoothManager {
    private static final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit

    private HeartRateDataCallback heartRateDataCallback;

    public HeartRateManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
    }

    public void setSimpleCallback(HeartRateDataCallback simpleCallback) {
        this.heartRateDataCallback = simpleCallback;
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_HEART_RATE));
        if (service != null) {
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
    }

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            /*
             * false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
             * true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
             */
            final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

            // heart rate value is 8 or 16 bit long
            int heartRate = data.getIntValue(value16bit ? Data.FORMAT_UINT16 :
                    Data.FORMAT_UINT8, offset++); // bits per minute

            heartRateDataCallback.onMeasurementReceived(device, heartRate, DateUtils.getCurrentDateTimeUTC());
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            heartRateDataCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            heartRateDataCallback.onDisconnected(device);
        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Log.i(TAG, "Services Discovered from " + device.getName());
        }

        @Override
        public void onDeviceReady(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingRequired(@NonNull BluetoothDevice device) {
            Log.i(TAG, "onBondingRequired > " + device.getName());
        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onError(@NonNull BluetoothDevice device, @NonNull String message, int errorCode) {
            Log.i(TAG, "Error from " + device.getName() + " - " + message);
        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {

        }
    };
}
