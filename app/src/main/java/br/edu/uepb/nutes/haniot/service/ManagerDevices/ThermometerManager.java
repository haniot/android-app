package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.TemperatureDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class ThermometerManager extends BluetoothManager {

    private TemperatureDataCallback temperatureDataCallback;

    public ThermometerManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
    }


    public void setSimpleCallback(TemperatureDataCallback temperatureDataCallback) {
        this.temperatureDataCallback = temperatureDataCallback;
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_HEALTH_THERMOMETER));
        if (service != null) {
            Log.i(TAG, "Não nulo");
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "iniatialize()");
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
    }

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            //Parse

            if (data.size() < 5) {
                return;
            }

            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset);
            final boolean timestampPresent = (flags & 0x02) != 0;
            final boolean temperatureTypePresent = (flags & 0x04) != 0;
            offset += 1;


            final float temperature = data.getFloatValue(Data.FORMAT_FLOAT, 1);
            offset += 4;

            Integer type = null;
            if (temperatureTypePresent) {
                type = data.getIntValue(Data.FORMAT_UINT8, offset);
                // offset += 1;
            }

            Measurement measurement = new Measurement();
            measurement.setValue(temperature);
            measurement.setRegistrationDate(DateUtils.getCurrentDatetime());
            measurement.setUnit(getContext().getResources().getString(R.string.unit_celsius));
            temperatureDataCallback.onMeasurementReceived(measurement);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            temperatureDataCallback.onConnected();
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            temperatureDataCallback.onDisconnected();
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
