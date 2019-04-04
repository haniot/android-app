package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;

public abstract class BluetoothManager extends BleManager<ManagerCallback> {
    protected final String TAG = "ManagerDevices";
    protected BluetoothGattCharacteristic mCharacteristic;

    public BluetoothManager(@NonNull Context context) {
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

    DataReceivedCallback dataReceivedCallback = (device, data) -> {
        Log.i(TAG, "onDataReceived()");
        mCallbacks.measurementReceiver(device, data);
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
        }
    };
}
