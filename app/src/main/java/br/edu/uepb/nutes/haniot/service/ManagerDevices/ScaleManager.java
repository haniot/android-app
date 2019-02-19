package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ScaleDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.data.Data;

public class ScaleManager extends BluetoohManager {

    private ScaleDataCallback scaleDataCallback;
    private DecimalFormat decimalFormat;

    public ScaleManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
        decimalFormat = new DecimalFormat(context.getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));

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
            try {
                final byte[] data = dataa.getValue();
                JSONObject result = new JSONObject();
                double bodyMass = 0f;

                if (data.length > 0) {
                    /**
                     * Timestamp current
                     */
                    result.put("timestamp", DateUtils.getCurrentDatetime());


                    /**
                     * 03: response type
                     *     01 - unfinished weighing
                     *     02 - finished weighing
                     */
                    boolean isFinalized = String.format("%02X", data[3]).equals("02");
                    result.put("isFinalized", isFinalized);

                    /**
                     * unfinished weighing
                     * 08-09: weight - BE uint16 times 0.01
                     */
                    bodyMass = Integer.valueOf(String.format("%02X", data[8]) + String.format("%02X", data[9]), 16) * 0.01f;
                    result.put("bodyMass", bodyMass);

                    // Body Mass Unit default
                    result.put("bodyMassUnit", "kg");

                    // Finalized
                    if (isFinalized) {
                        /**
                         * finished weighing
                         * 13-14: weight - BE uint16 times 0.01
                         */
                        bodyMass = Integer.valueOf(String.format("%02X", data[13]) + String.format("%02X", data[14]), 16) * 0.01f;
                        result.put("bodyMass", bodyMass);

                        /**
                         * 15-16: resistance - BE uint 16
                         */
                        final double resistance = Integer.valueOf(String.format("%02X", data[15]) + String.format("%02X", data[16]), 16);
                        result.put("resistance", resistance);

                        /**
                         * Body Fat in percentage
                         *
                         * 17-18: - BE uint16 times 0.01
                         */
                        final double bodyFat = Integer.valueOf(String.format("%02X", data[17]) + String.format("%02X", data[18]), 16) * 0.01f;
                        result.put("bodyFat", bodyFat);
                        result.put("bodyFatUnit", "%"); // value fixed

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
                    Log.i(TAG, "Received measurent from Scale" + device.getName() + ": " + result.get("bodyMass"));

                    //final String bodyMassMeasurement = formatNumber(result.getDouble("bodyMass"));
                    //inal String bodyMassUnit = result.getString("bodyMassUnit");

                    //bodyMassTextView.setText(bodyMassMeasurement);
                    //.setText(bodyMassUnit);

                    //if (isFinalized) {
                    // showAnimation = false;
                    Session session = new Session(getContext());
                    User user = session.getUserLogged();

                    Measurement bodyMassMeasurement = JsonToMeasurementParser.bodyMass(result.toString());
                    bodyMassMeasurement.setUser(user);
                    //bodyMass.setDevice(mDevice);

                    Measurement bmi = new Measurement(calcBMI(bodyMassMeasurement.getValue()),
                            "kg/m2", bodyMassMeasurement.getRegistrationDate(), MeasurementType.BMI);
                    bmi.setUser(user);
                    //  bmi.setDevice(mDevice);

                    Measurement bodyFatMeasurement = JsonToMeasurementParser.bodyFat(result.toString());
                    bodyFatMeasurement.setUser(user);
                    //  bodyFat.setDevice(mDevice);

                    /**
                     * Add relationships
                     */
                    bodyMassMeasurement.addMeasurement(bmi, bodyFatMeasurement);


                    scaleDataCallback.onMeasurementReceiver(bodyMassMeasurement);
                    //  }
//            Intent intent = new Intent("Measurement");
//            intent.putExtra("Device", MeasurementType.BODY_FAT);
//            intent.putExtra("Value", temperature);
//            EventBus.getDefault().post(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    /**
     * Format value for XX.X
     *
     * @param value double
     * @return String
     */
    private String formatNumber(double value) {
        String result = decimalFormat.format(value);
        return result.equals(".0") ? "00.0" : result;
    }

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
