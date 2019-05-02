package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

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
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "iniatialize()");
        setIndicationCallback(mCharacteristic).with(dataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
    }

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            final byte TEMPERATURE_UNIT_FLAG = 0x01; // 1 bit
            final byte TIMESTAMP_FLAG = 0x02; // 1 bits
            final byte TEMPERATURE_TYPE_FLAG = 0x04; // 1 bit
            Log.i(TAG, "measurementReceiver()");
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            /*
             * false 	Temperature is in Celsius degrees
             * true 	Temperature is in Fahrenheit degrees
             */
            final boolean fahrenheit = (flags & TEMPERATURE_UNIT_FLAG) > 0;
            String unit = "°C";
            if (fahrenheit) unit = "°F";

            /*
             * false 	No Timestamp in the packet
             * true 	There is a timestamp information
             */
            final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

            /*
             * false 	Temperature type is not included
             * true 	Temperature type included in the packet
             */
            final boolean temperatureTypeIncluded = (flags & TEMPERATURE_TYPE_FLAG) > 0;

            final float tempValue = data.getFloatValue(Data.FORMAT_FLOAT, offset);
            offset += 4;

            String timestamp = null;
            if (timestampIncluded) {
                // parse timestamp if present
                final int year = data.getIntValue(Data.FORMAT_UINT16, offset);
                final int month = data.getIntValue(Data.FORMAT_UINT8, offset + 2) - 1;
                final int day = data.getIntValue(Data.FORMAT_UINT8, offset + 3);
                final int hours = data.getIntValue(Data.FORMAT_UINT8, offset + 4);
                final int minutes = data.getIntValue(Data.FORMAT_UINT8, offset + 5);
                final int seconds = data.getIntValue(Data.FORMAT_UINT8, offset + 6);

                final Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hours, minutes, seconds);
                timestamp = DateUtils.calendarToString(calendar, DateUtils.DATE_FORMAT_ISO_8601);
                offset += 7;
            } else {
                timestamp = DateUtils.getCurrentDateISO8601();
            }

            if (temperatureTypeIncluded) {
                // offset++;
            }
            Log.i(TAG, "measurementReceiver() " + data.toString());
            temperatureDataCallback.onMeasurementReceived(device, tempValue, unit, timestamp);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            temperatureDataCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            temperatureDataCallback.onDisconnected(device);
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
