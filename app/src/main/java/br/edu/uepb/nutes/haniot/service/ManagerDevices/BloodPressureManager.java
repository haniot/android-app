package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Calendar;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodPressureDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
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
        final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_BLOOD_PRESSURE));
        if (service != null) {
            Log.i(TAG, "Service Check");
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_BLOOD_PRESSURE_MEASUREMENT));
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

            if (data.size() < 7) {
                return;
            }
            // First byte: flags
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset++);

            // See UNIT_* for unit options
            final boolean timestampPresent         = (flags & 0x02) != 0;
            final boolean pulseRatePresent         = (flags & 0x04) != 0;
            final boolean userIdPresent            = (flags & 0x08) != 0;
            final boolean measurementStatusPresent = (flags & 0x10) != 0;

            if (data.size() < 7
                    + (timestampPresent ? 7 : 0) + (pulseRatePresent ? 2 : 0)
                    + (userIdPresent ? 1 : 0) + (measurementStatusPresent ? 2 : 0)) {
                return;
            }

            // Following bytes - systolic, diastolic and mean arterial pressure
            final float systolic = data.getFloatValue(Data.FORMAT_SFLOAT, offset);
            final float diastolic = data.getFloatValue(Data.FORMAT_SFLOAT, offset + 2);
            final float meanArterialPressure = data.getFloatValue(Data.FORMAT_SFLOAT, offset + 4);
            offset += 6;

            // Parse timestamp if present
            if (timestampPresent) {
                offset += 7;
            }

            // Parse pulse rate if present
            Float pulseRate = null;
            if (pulseRatePresent) {
                pulseRate = data.getFloatValue(Data.FORMAT_SFLOAT, offset);
                offset += 2;
            }

            // Read user id if present
            Integer userId = null;
            if (userIdPresent) {
                userId = data.getIntValue(Data.FORMAT_UINT8, offset);
                offset += 1;
            }

            // Read measurement status if present
            if (measurementStatusPresent) {
                final int measurementStatus = data.getIntValue(Data.FORMAT_UINT16, offset);
                // offset += 2;
            }
            Calendar calendar = Calendar.getInstance();

            Measurement systolicMeasurement = new Measurement();
            systolicMeasurement.setValue(systolic);
            systolicMeasurement.setRegistrationDate(calendar.getTimeInMillis());
            //systolicMeasurement.setUser(user);
            //systolicMeasurement.setDevice(mDevice);

            Measurement diastolicMeasurement = new Measurement();
            diastolicMeasurement.setValue(diastolic);
            diastolicMeasurement.setRegistrationDate(calendar.getTimeInMillis());

            //diastolicMeasurement.setUser(user);
            //diastolicMeasurement.setDevice(mDevice);

            Measurement heartRateMeasurement = new Measurement();
            heartRateMeasurement.setValue(meanArterialPressure);
            heartRateMeasurement.setRegistrationDate(calendar.getTimeInMillis());
            //heartRateMeasurement.setUser(user);
            //heartRateMeasurement.setDevice(mDevice);

            /**
             * Add relationships
             */
            systolicMeasurement.addMeasurement(diastolicMeasurement, heartRateMeasurement);
            bloodPressureDataCallback.onMeasurementReceived(systolicMeasurement);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            bloodPressureDataCallback.onConnected();
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            bloodPressureDataCallback.onDisconnected();
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
