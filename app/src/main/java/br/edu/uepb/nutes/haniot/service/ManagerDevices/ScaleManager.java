package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class ScaleManager extends BluetoohManager {

    private ScaleDataCallback scaleDataCallback;

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
            Log.i(TAG, "Não nulo");
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "initializeCharacteristic()");
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
    }

    ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data dataa) {
            byte[] data = dataa.getValue();
            if (data.length > 0) {
                Measurement measurement = new Measurement();
                /**
                 * Timestamp current
                 */
                measurement.setRegistrationDate(DateUtils.getCurrentDatetime());
//                result.put("timestamp", DateUtils.getCurrentDatetime());

                /**
                 * 03: response type
                 *     01 - unfinished weighing
                 *     02 - finished weighing
                 */
                boolean isFinalized = String.format("%02X", data[3]).equals("02");
//                result.put("isFinalized", isFinalized);

                /**
                 * unfinished weighing
                 * 08-09: weight - BE uint16 times 0.01
                 */
                double bodyMass = Integer.valueOf(String.format("%02X", data[8]) + String.format("%02X", data[9]), 16) * 0.01f;
                measurement.setValue(bodyMass);
//                result.put("bodyMass", bodyMass);
                // Body Mass Unit default
                measurement.setUnit(getContext().getString(R.string.unit_kg));
                // result.put("bodyMassUnit", "kg");

                // Finalized
                if (isFinalized) {
                    Measurement measurementFat = new Measurement();
                    /**
                     * finished weighing
                     * 13-14: weight - BE uint16 times 0.01
                     */
                    bodyMass = Integer.valueOf(String.format("%02X", data[13]) + String.format("%02X", data[14]), 16) * 0.01f;
                    measurementFat.setValue(bodyMass);
                    // result.put("bodyMass", bodyMass);

                    /**
                     * 15-16: resistance - BE uint 16
                     */
                    final double resistance = Integer.valueOf(String.format("%02X", data[15]) + String.format("%02X", data[16]), 16);
//                    result.put("resistance", resistance);

                    /**
                     * Body Fat in percentage
                     *
                     * 17-18: - BE uint16 times 0.01
                     */
                    final double bodyFat = Integer.valueOf(String.format("%02X", data[17]) + String.format("%02X", data[18]), 16) * 0.01f;
//                    result.put("bodyFat", bodyFat);
//                    result.put("bodyFatUnit", "%"); // value fixed

//                    measurement.addMeasurement();
                    /**
                     * USER ID
                     * 09-12: recognized userID - BE uint32
                     */
//                Integer userID = Integer.valueOf(String.format("%02X", data[9]) +
//                        String.format("%02X", data[10]) +
//                        String.format("%02X", data[11]) +
//                        String.format("%02X", data[12]), 32
//                );
//
//                result.put("userID", userID);
                }

                Log.i(TAG, "Received measurent from Scale" + device.getName() + ": " + measurement);

                scaleDataCallback.onMeasurementReceiver(measurement);
            }
//            Intent intent = new Intent("Measurement");
//            intent.putExtra("Device", MeasurementType.BODY_FAT);
//            intent.putExtra("Value", temperature);
//            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to Scale" + device.getName());
            //showToast("Connecting to " + device.getName());
//            Intent intent = new Intent("Connecting");
//            intent.putExtra("device", MeasurementType.BODY_FAT);
//            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to Scale" + device.getName());
            //showToast("Connected to " + device.getName());
//            Intent intent = new Intent("Connected");
//            intent.putExtra("device", MeasurementType.BODY_FAT);
//            EventBus.getDefault().post(intent);
            scaleDataCallback.onConnected();

        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
            //showToast("Disconnecting from " + device.getName());

        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            //showToast("Disconnected to " + device.getName());
//            Intent intent = new Intent("Disconnected");
//            intent.putExtra("device", MeasurementType.BODY_FAT);
//            EventBus.getDefault().post(intent);
            scaleDataCallback.onDisconnected();

        }

        @Override
        public void onLinkLossOccurred(@NonNull BluetoothDevice device) {

        }

        @Override
        public void onServicesDiscovered(@NonNull BluetoothDevice device, boolean optionalServicesFound) {
            Log.i(TAG, "Services Discovered from " + device.getName());
            //showToast("Services Discovered from " + device.getName());

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
            //showToast("Error from " + device.getName() + " - " + message);

        }

        @Override
        public void onDeviceNotSupported(@NonNull BluetoothDevice device) {

        }
    };
}
