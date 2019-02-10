package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.UUID;

import androidx.annotation.NonNull;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class GlucoseManager extends BluetoohManager {

    BluetoothGattCharacteristic characteristicRecordAccess;
    BluetoothGattCharacteristic characteristicWrite;
    byte[] data = new byte[2];

    public GlucoseManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_GLUCOSE));
        if (service != null) {

            characteristicWrite = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
            byte[] data = new byte[2];
            data[0] = 0x01; // Report Stored records
            data[1] = 0x06; // last record


            if (characteristicRecordAccess == null) {
                characteristicRecordAccess = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL)); // read
            }

            Log.i("gattService", "Não nulo");
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT));
        }
    }

    @Override
    protected void initialize() {
        Log.i("gattService", "iniatialize()");
        //setNotificationCallback(characteristicRecordAccess).with(dataReceivedCallback);
        writeCharacteristic(characteristicWrite, data);
        readCharacteristic(characteristicRecordAccess).with(dataReceivedCallback);
        setIndicationCallback(characteristicRecordAccess).with(dataReceivedCallback);
        enableIndications(mCharacteristic).enqueue();
        //setIndicationCallback(mCharacteristic).with(dataReceivedCallback);
    }

    ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@androidx.annotation.NonNull BluetoothDevice device, @androidx.annotation.NonNull Data data) {
            //Parse

//            if (data.size() < 5) {
//                onInvalidDataReceived(device, data);
//                return;
//            }

            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset);
            //   final int unit = (flags & 0x01) == UNIT_C ? UNIT_C : UNIT_F;
            final boolean timestampPresent = (flags & 0x02) != 0;
            final boolean temperatureTypePresent = (flags & 0x04) != 0;
            offset += 1;


            final float temperature = data.getFloatValue(Data.FORMAT_FLOAT, 1);
            offset += 4;

            Calendar calendar = null;
//            if (timestampPresent) {
//                calendar = DateTimeDataCallback.readDateTime(data, offset);
//                offset += 7;
//            }

            Integer type = null;
            if (temperatureTypePresent) {
                type = data.getIntValue(Data.FORMAT_UINT8, offset);
                // offset += 1;
            }

            Log.i(TAG, "Received measurent from " + device.getName() + ": " + temperature);

            float value = 0.0f;
            Intent intent = new Intent("Measurement");
            intent.putExtra("Device", MeasurementType.BLOOD_GLUCOSE);
            intent.putExtra("Value", value);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceConnecting(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
            //showToast("Connecting to " + device.getName());
            Intent intent = new Intent("Connecting");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceConnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            //showToast("Connected to " + device.getName());
            Intent intent = new Intent("Connected");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceDisconnecting(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
            //showToast("Disconnecting from " + device.getName());

        }

        @Override
        public void onDeviceDisconnected(@androidx.annotation.NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            //showToast("Disconnected to " + device.getName());
            Intent intent = new Intent("Disconnected");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onLinkLossOccurred(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(@androidx.annotation.NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Log.i(TAG, "Services Discovered from " + device.getName());
            //showToast("Services Discovered from " + device.getName());

        }

        @Override
        public void onDeviceReady(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingRequired(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBonded(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onBondingFailed(@androidx.annotation.NonNull BluetoothDevice device) {

        }

        @Override
        public void onError(@androidx.annotation.NonNull BluetoothDevice device, @androidx.annotation.NonNull String message, int errorCode) {
            Log.i(TAG, "Error from " + device.getName() + " - " + message);
            //showToast("Error from " + device.getName() + " - " + message);

        }

        @Override
        public void onDeviceNotSupported(@androidx.annotation.NonNull BluetoothDevice device) {

        }
    };
}
