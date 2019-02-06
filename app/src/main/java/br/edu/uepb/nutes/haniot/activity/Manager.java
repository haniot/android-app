package br.edu.uepb.nutes.haniot.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import java.util.UUID;

import androidx.annotation.NonNull;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class Manager extends BleManager<ManagerCallback> {
    private final String TAG = "ManagerDevices";
    private BluetoothGattCharacteristic mTemperatureCharacteristic;

    public Manager(@NonNull Context context) {
        super(context);
    }


    public void connectDevice(BluetoothDevice device) {
        Log.i("Manager", "Called connect");
        super.connect(device)
                .retry(3, 100)
                .useAutoConnect(true)
                .enqueue();
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

    public final static UUID LBS_UUID_SERVICE = UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT);
//    private final DevicesCallback mLedCallback = new DevicesCallback() {
//        @Override
//        public void onDeviceConnecting(@android.support.annotation.NonNull BluetoothDevice device) {
//        }
//
//        @Override
//        public void onDeviceConnected(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onDeviceDisconnecting(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onDeviceDisconnected(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onLinkLossOccurred(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onServicesDiscovered(@android.support.annotation.NonNull BluetoothDevice device, boolean optionalServicesFound) {
//
//        }
//
//        @Override
//        public void onDeviceReady(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onBondingRequired(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onBonded(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onBondingFailed(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//        @Override
//        public void onError(@android.support.annotation.NonNull BluetoothDevice device, @android.support.annotation.NonNull String message, int errorCode) {
//
//        }
//
//        @Override
//        public void onDeviceNotSupported(@android.support.annotation.NonNull BluetoothDevice device) {
//
//        }
//
//    };
//
//    DataReceivedCallback dataReceivedCallback = new DataReceivedCallback() {
//        @Override
//        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
//            mCallbacks.onDeviceConnected(device);
//
//        }
//    };

    DataReceivedCallback dataReceivedCallback = new DataReceivedCallback() {

        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            Log.i("gatt", "onDataReceived");

            mCallbacks.measurementReceiver(device, data);
        }
    };
    /**
     * BluetoothGatt callbacks object.
     */
    private boolean mSupported;
    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected void initialize() {

            Log.i("gattService", "iniatialize()");
            setIndicationCallback(mTemperatureCharacteristic).with(dataReceivedCallback);
            enableIndications(mTemperatureCharacteristic).enqueue();

            //readCharacteristic(mLedCharacteristic).with(mLedCallback).enqueue();
            // readCharacteristic(mButtonCharacteristic).with(mButtonCallback).enqueue();
            //   enableNotifications(mButtonCharacteristic).enqueue();
        }


        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            Log.i("gattService", "isSupported");
            final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER));
            if (service != null) {
                Log.i("gattService", "Não nulo");
                mTemperatureCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));
            }

            boolean writeRequest = false;
            if (mTemperatureCharacteristic != null) {
                Log.i("gattService", "Não nulo");

                final int rxProperties = mTemperatureCharacteristic.getProperties();
                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
            }

            mSupported = mTemperatureCharacteristic != null && writeRequest;
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {
            mTemperatureCharacteristic = null;
        }
    };

//    /**
//     * Sends a request to the device to turn the LED on or off.
//     *
//     * @param on true to turn the LED on, false to turn it off.
//     */
//    public void send(final boolean on) {
//        // Are we connected?
//        if (mLedCharacteristic == null)
//            return;
//
//
//
//        log(Log.VERBOSE, "Turning LED " + (on ? "ON" : "OFF") + "...");
//        writeCharacteristic(mLedCharacteristic, on ? BlinkyLED.turnOn() : BlinkyLED.turnOff())
//                .with(mLedCallback).enqueue();
//    }

}
