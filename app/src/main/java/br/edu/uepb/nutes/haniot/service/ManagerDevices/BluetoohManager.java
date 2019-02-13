package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public abstract class BluetoohManager extends BleManager<ManagerCallback> {
    protected final String TAG = "ManagerDevices";
    protected BluetoothGattCharacteristic mCharacteristic;

    public BluetoohManager(@NonNull Context context) {
        super(context);
    }


    public void connectDevice(BluetoothDevice device) {
        Log.i(TAG, "Called connect");
        super.connect(device)
                .retry(10, 100)
                .useAutoConnect(false)
                .enqueue();
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return mGattCallback;
    }

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
            Log.i(TAG, "onDataReceived()");
            mCallbacks.measurementReceiver(device, data);
        }
    };

    public void disconnectDevice() {
        disconnect();
    }

    protected abstract void initializeCharacteristic();

    protected abstract void setCharacteristicWrite(BluetoothGatt gatt);

    /**
     * BluetoothGatt callbacks object.
     */
    private boolean mSupported;

    private final BleManagerGattCallback mGattCallback = new BleManagerGattCallback() {

        @Override
        protected void initialize() {
            Log.i(TAG, "iniatialize()");
            initializeCharacteristic();

            //readCharacteristic(mLedCharacteristic).with(mLedCallback).enqueue();
            // readCharacteristic(mButtonCharacteristic).with(mButtonCallback).enqueue();
            //   enableNotifications(mButtonCharacteristic).enqueue();
        }


        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            Log.i(TAG, "isSupported");
            setCharacteristicWrite(gatt);
            boolean writeRequest = false;
            if (mCharacteristic != null) {
                Log.i(TAG, "Characteristic check");

                final int rxProperties = mCharacteristic.getProperties();
                writeRequest = (rxProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
            }

            mSupported = mCharacteristic != null && writeRequest;
            return true;
        }

        @Override
        protected void onDeviceDisconnected() {
            Log.i(TAG, "onDeviceDisconnected()");
//            connect(getBluetoothDevice());
            //mCharacteristic = null;
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
