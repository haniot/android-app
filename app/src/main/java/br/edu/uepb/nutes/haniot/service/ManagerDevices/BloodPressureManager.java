package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodPressureDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class BloodPressureManager extends BluetoothManager {

    private BloodPressureDataCallback bloodPressureDataCallback;

    public BloodPressureManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
    }


    public void setSimpleCallback(BloodPressureDataCallback simpleCallback) {
        this.bloodPressureDataCallback = simpleCallback;
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(
                UUID.fromString(GattAttributes.SERVICE_BLOOD_PRESSURE));
        if (service != null) {
            Log.i(TAG, "Service Check");
            mCharacteristic = service.getCharacteristic(
                    UUID.fromString(GattAttributes.CHARACTERISTIC_BLOOD_PRESSURE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "iniatialize()");
        setIndicationCallback(mCharacteristic).with(dataReceivedCallback);
        enableIndications(mCharacteristic).enqueue();
    }

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            if (data.size() < 7) return;

            // First byte: flags
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            final int unitType = flags & 0x01;
            final boolean timestampPresent = (flags & 0x02) > 0;
            final boolean pulseRatePresent = (flags & 0x04) > 0;
            final boolean userIdPresent = (flags & 0x08) > 0;
            final boolean statusPresent = (flags & 0x10) > 0;

            // following bytes - systolic, diastolic and mean arterial pressure
            final int systolic = Math.round(data.getFloatValue(Data.FORMAT_SFLOAT, offset));
            final int diastolic = Math.round(data.getFloatValue(Data.FORMAT_SFLOAT, offset + 2));
            final float meanArterialPressure = data.getFloatValue(Data.FORMAT_SFLOAT, offset + 4);
            final String unit = unitType == 0 ? " mmHg" : " kPa";
            offset += 6;

            // parse timestamp if present
            String timestamp = null;
            if (timestampPresent) {
                final int year = data.getIntValue(Data.FORMAT_UINT16, offset);
                final int month = data.getIntValue(Data.FORMAT_UINT8, offset + 2) - 1;
                final int day = data.getIntValue(Data.FORMAT_UINT8, offset + 3);
                final int hours = data.getIntValue(Data.FORMAT_UINT8, offset + 4);
                final int minutes = data.getIntValue(Data.FORMAT_UINT8, offset + 5);
                final int seconds = data.getIntValue(Data.FORMAT_UINT8, offset + 6);

                final Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hours, minutes, seconds);
                timestamp = DateUtils.convertDateTimeToUTC(calendar.getTime());
                offset += 7;
            } else {
                timestamp = DateUtils.getCurrentDateTimeUTC();
            }

            // parse pulse rate if present
            int pulseRate = 0;
            if (pulseRatePresent) {
                pulseRate = Math.round(data.getFloatValue(Data.FORMAT_SFLOAT, offset));
                offset += 2;
            }

            if (userIdPresent) {
                final int userId = data.getIntValue(Data.FORMAT_UINT8, offset);
                offset += 1;
            }

            bloodPressureDataCallback.onMeasurementReceived(device, systolic, diastolic,
                    pulseRate, unit, timestamp);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            bloodPressureDataCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            bloodPressureDataCallback.onDisconnected(device);
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
            Log.i(TAG, "on bonding requerid in " + device.getName());

        }

        @Override
        public void onBonded(@NonNull BluetoothDevice device) {
            Log.i(TAG, "on bonded in " + device.getName());

        }

        @Override
        public void onBondingFailed(@NonNull BluetoothDevice device) {
            Log.i(TAG, "on bonding failed in " + device.getName());

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
