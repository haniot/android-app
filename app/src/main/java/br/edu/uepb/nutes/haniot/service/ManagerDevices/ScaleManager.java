package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class ScaleManager extends BluetoothManager {
    private ScaleDataCallback scaleDataCallback;
    private boolean isFinishMeasurement = false;

    public ScaleManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
    }

    public void setSimpleCallback(ScaleDataCallback simpleCallback) {
        this.scaleDataCallback = simpleCallback;
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt.getService(UUID.fromString(GattAttributes.SERVICE_SCALE));
        if (service != null) {
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "initializeCharacteristic()");
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
    }

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data dataBle) {
            final byte[] data = dataBle.getValue();
            double bodyMass;
            double bodyFat = 0f;
            String bodyMassUnit = "";
            String timestamp;

            if (data != null && data.length > 0) {
                timestamp = DateUtils.getCurrentDateTimeUTC();

                // 03: response type
                //    01 - unfinished weighing
                //    02 - finished weighing
                boolean isFinalized = String.format("%02X", data[3]).equals("02");

                // unfinished weighing
                // 08-09: weight - BE uint16 times 0.01
                bodyMass = Integer.valueOf(String.format("%02X", data[8]) +
                        String.format("%02X", data[9]), 16) * 0.01f;

                // Finalized
                if (isFinalized && !isFinishMeasurement) {
                    isFinishMeasurement = true;

                    // finished weighing
                    // 13-14: weight - BE uint16 times 0.01
                    bodyMass = Integer.valueOf(String.format("%02X", data[13]) +
                            String.format("%02X", data[14]), 16) * 0.01f;

                    // 15-16: resistance - BE uint 16
                    final double resistance = Integer.valueOf(String.format("%02X", data[15]) +
                            String.format("%02X", data[16]), 16);

                    // Body Fat in percentage
                    //  17-18: - BE uint16 times 0.01
                    bodyFat = Integer.valueOf(String.format("%02X", data[17]) +
                            String.format("%02X", data[18]), 16) * 0.01f;

                    if (scaleDataCallback != null) {
                        scaleDataCallback.onMeasurementReceived(
                                device,
                                bodyMass,
                                bodyMassUnit,
                                bodyFat,
                                timestamp
                        );
                    }
                } else {
                    if (scaleDataCallback != null && !isFinishMeasurement) {
                        scaleDataCallback.onMeasurementReceiving(bodyMass, bodyMassUnit);
                    }
                    isFinishMeasurement = false;
                }
            }
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to Scale" + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to Scale" + device.getName());
            if (scaleDataCallback != null) scaleDataCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            if (scaleDataCallback != null) scaleDataCallback.onDisconnected(device);
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

    /**
     * Return value of BMI.
     * formula: bodyMass(kg)/height(m)^2
     *
     * @param bodyMass double
     * @return double
     */
    private double calcBMI(double bodyMass) {
        //double height = (session.getUserLogged().getHeight()) / 100D;
        double height = 1.0;
        return bodyMass / (Math.pow(height, 2));
    }

}
