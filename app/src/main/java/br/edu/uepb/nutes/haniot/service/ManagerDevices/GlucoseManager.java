package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.parse.JsonToContextParser;
import br.edu.uepb.nutes.haniot.parse.JsonToMeasurementParser;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.GlucoseDataCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class GlucoseManager extends BluetoothManager {

    private static final int UNIT_kgpl = 0;
    private static final byte TIMESTAMP_FLAG = 0x01; // 1 bit
    private static final int UNIT_molpl = 1;
    private static final int UNIT_kg = 0;
    private static final int UNIT_l = 1;
    GlucoseDataCallback glucoseDataCallback;
    BluetoothGattCharacteristic characteristicRecordAccess;
    BluetoothGattCharacteristic characteristicContext;
    BluetoothGattCharacteristic characteristicWrite;
    byte[] data;
    Data dataContext;

    public GlucoseManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
        data = new byte[2];
    }

    public void setSimpleCallback(GlucoseDataCallback simpleCallback) {
        this.glucoseDataCallback = simpleCallback;
    }

    @Override
    protected void setCharacteristicWrite(BluetoothGatt gatt) {
        final BluetoothGattService service = gatt
                .getService(UUID.fromString(GattAttributes.SERVICE_GLUCOSE));
        if (service != null) {

            characteristicWrite = service
                    .getCharacteristic(UUID.fromString(GattAttributes
                            .CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL));
            byte[] data = new byte[2];
            data[0] = 0x01; // Report Stored records
            data[1] = 0x06; // last record


            if (characteristicRecordAccess == null) {
                characteristicRecordAccess = service
                        .getCharacteristic(UUID.fromString(GattAttributes
                                        .CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL)); // read
            }
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes
                    .CHARACTERISTIC_GLUSOSE_MEASUREMENT));
            characteristicContext = service.getCharacteristic(UUID.fromString(GattAttributes
                    .CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "iniatialize()");
        writeCharacteristic(characteristicWrite, data);
        readCharacteristic(characteristicRecordAccess).with(dataReceivedCallback);
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        setNotificationCallback(characteristicContext).with(contextDataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
        enableNotifications(characteristicContext).enqueue();
    }

    DataReceivedCallback contextDataReceivedCallback = new DataReceivedCallback() {
        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            Log.i(TAG, "onDataReceived()");
            Log.i(TAG, "Received context");
            dataContext = data;
        }
    };

    ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            //Parse
            new Handler().postDelayed(() -> {
                JSONObject result = new JSONObject();
                try {
                    float glucoseConcentration = 0;
                    Calendar calendar = null;
                    String unit = "";

                    int offset = 0;
                    final int flags = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
                    final StringBuilder builder = new StringBuilder();

                    /**
                     * false 	No Timestamp in the packet
                     * true 	There is a timestamp information
                     */
                    final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

                    final boolean timeOffsetPresent = (flags & 0x01) > 0;
                    final boolean typeAndLocationPresent = (flags & 0x02) > 0;
                    final int concentrationUnit = (flags & 0x04) > 0 ? UNIT_molpl : UNIT_kgpl;
                    final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
                    final boolean contextInfoFollows = (flags & 0x10) > 0;

                    int sequenceNumber = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
                    offset += 2;

                    if (timestampIncluded) {
                        calendar = dateParse(data, offset);
                        offset += 7;
                    }

                    if (timeOffsetPresent) {
                        // time offset is ignored in the current release
                        final int timeOffset = data.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
                        offset += 2;
                    }

                    if (typeAndLocationPresent) {
                        glucoseConcentration = data.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
                        final int typeAndLocation = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
                        int type = (typeAndLocation & 0x0F);
                        int sampleLocation = (typeAndLocation & 0xF0) >> 4;
                        unit = concentrationUnit == UNIT_kgpl ? "kg/L" : "mol/L";
                        offset += 3;

                        /**
                         * Id of Type and Sample Location
                         */
                        result.put("glucoseTypeId", type); // Type Id
                        result.put("glucoseLocationId", type); // Sample Location Id
                    }

                    if (sensorStatusAnnunciationPresent) {
                        int status = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);

                        /**
                         * Id of Sensor Status Annunciation
                         */
                        result.put("sensorStatusAnnunciationId", status);
                    }

                    result.put("glucose", glucoseConcentration);

                    result.put("glucoseUnit", unit);
                    result.put("sequenceNumber", sequenceNumber);
                    result.put("timestamp", calendar != null ?
                            calendar.getTimeInMillis() :
                            DateUtils.getCurrentTimestamp());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Measurement glucose;
                try {
                    glucose = JsonToMeasurementParser.bloodGlucose(result.toString());

                    List<ContextMeasurement> contextMeasurement = new ArrayList<>();
                    if (dataContext != null) {

                        Log.i(TAG, "Received context " + contextMeasurement.toString());
                        contextMeasurement = JsonToContextParser.parse(result.toString(),
                                contextParse(dataContext).toString());
                        if (contextMeasurement != null) {
                            glucose.addContext(contextMeasurement);
                            Log.i(TAG, "Received context " + contextMeasurement.toString());
                            dataContext = null;
                        }
                    }


                    glucoseDataCallback.onMeasurementReceived(glucose);

                    if (!glucose.getContextMeasurements().isEmpty()) {
                        StringBuilder contextString = new StringBuilder();
                        /**
                         * Recover text from context value meal.
                         *
                         * @param contextMeasurements
                         * @return String
                         */
                        for (ContextMeasurement c : glucose.getContextMeasurements()) {
                            if (c.getTypeId() == ContextMeasurementType.GLUCOSE_MEAL)
                                contextString.append(" - ").append(ContextMeasurementValueType
                                        .getString(getContext(), c.getValueId()));
                        }
                        Log.i(TAG, new StringBuilder().append("Received measurent from ")
                                .append(device.getName()).append(": ")
                                .append(glucose.getValue()).append(" - ")
                                .append(contextString).toString());

                    } else
                        Log.i(TAG, new StringBuilder().append("Received measurent from ")
                                .append(device.getName()).append(": ")
                                .append(glucose.getValue()).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, 5000);

        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
            Intent intent = new Intent("Connecting");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            Intent intent = new Intent("Connected");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            Intent intent = new Intent("Disconnected");
            intent.putExtra("device", MeasurementType.BLOOD_GLUCOSE);
            EventBus.getDefault().post(intent);
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

    public static Calendar dateParse(final Data data, final int offset) {
        final int year = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        final int month = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2) - 1;
        final int day = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
        final int hours = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
        final int minutes = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
        final int seconds = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);

        return calendar;
    }

    /**
     * Convert value glucose for String.
     *
     * @param measurement
     * @return String
     */
    public static String parse(Measurement measurement) {
        String value_formated = "";
        double value = measurement.getValue();

        if (measurement.getUnit().equals("kg/L")) {
            measurement.setUnit("mg/dL");
            value = value * 100000;
        }

        if (value > 600) {
            value_formated = "HI"; // The blood glucose value may be above the reading range of the system.
        } else if (value < 0) {
            value_formated = "LO"; // The blood glucose value may be below the reading range of the system.
        } else {
            value_formated = String.format("%02d", (int) value);
        }

        return value_formated;
    }

    /**
     * Parse for the ACCU-CHEK device, according to GATT.
     * Supported Models: Accu-Chek Performa Connect.
     * <p>
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.glucose_measurement_context.xml>}
     *
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject contextParse(final Data data) throws JSONException {
        Log.i("ManagerDevices", "contextParse()");
        JSONObject result = new JSONObject();

        int offset = 0;
        final int flags = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        final boolean carbohydratePresent = (flags & 0x01) > 0;
        final boolean mealPresent = (flags & 0x02) > 0;
        final boolean testerHealthPresent = (flags & 0x04) > 0;
        final boolean exercisePresent = (flags & 0x08) > 0;
        final boolean medicationPresent = (flags & 0x10) > 0;
        final int medicationUnit = (flags & 0x20) > 0 ? UNIT_l : UNIT_kg;
        final boolean hbA1cPresent = (flags & 0x40) > 0;
        final boolean moreFlagsPresent = (flags & 0x80) > 0;

        int sequenceNumber = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        offset += 2;

        if (moreFlagsPresent) // not supported yet
            offset += 1;

        if (carbohydratePresent) {
            int carbohydrateId = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float carbohydrateUnits = data.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            offset += 3;

            /**
             * Carbohydrate ID and
             * Carbohydrate - units of kilograms
             */
            result.put("glucoseCarbohydrateId", carbohydrateId); // Carbohydrate Id
            result.put("glucoseCarbohydrateUnits", carbohydrateUnits); // Carbohydrate Units
        }

        if (mealPresent) {
            int meal = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            offset += 1;

            /**
             * Meal ID
             */
            result.put("glucoseMealId", meal);
        }

        if (testerHealthPresent) {
            int testerHealth = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            int tester = (testerHealth & 0xF0) >> 4;
            int health = (testerHealth & 0x0F);
            // TODO handle data when you need it

            offset += 1;
        }

        if (exercisePresent) {
            int exerciseDuration = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            int exerciseIntensity = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            // TODO handle data when you need it

            offset += 3;
        }

        if (medicationPresent) {
            int medicationId = data.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float medicationQuantity = data.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            // TODO handle data when you need it

            offset += 3;
        }

        if (hbA1cPresent) {
            float HbA1c = data.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            // TODO handle data when you need it
        }

        result.put("sequenceNumber", sequenceNumber);

        Log.i("ManagerDevices", "Context to String: " + result.toString());
        return result;
    }
}
