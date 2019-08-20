package br.edu.uepb.nutes.haniot.service.ManagerDevices;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.Calendar;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.data.model.BloodGlucoseMealType;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.BloodGlucoseDataCallback;
import br.edu.uepb.nutes.haniot.service.ManagerDevices.callback.ManagerCallback;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.data.Data;

public class GlucoseManager extends BluetoothManager {
    private BloodGlucoseDataCallback glucoseDataCallback;
    private BluetoothGattCharacteristic characteristicRecordAccess;
    private BluetoothGattCharacteristic characteristicContext;
    private BluetoothGattCharacteristic characteristicWrite;
    private byte[] data;
    private SparseArray<GlucoseRecord> mRecords;

    public GlucoseManager(@NonNull Context context) {
        super(context);
        setGattCallbacks(bleManagerCallbacks);
        data = new byte[2];
        mRecords = new SparseArray<>();
    }

    public void setSimpleCallback(BloodGlucoseDataCallback simpleCallback) {
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
            data[0] = 0x01; // Report Stored records
            data[1] = 0x06; // last record
//            data[1] = 0x01; // all records
//            data[1] = 0x05; // first record

//            if (characteristicRecordAccess == null) {
//                characteristicRecordAccess = service
//                        .getCharacteristic(UUID.fromString(GattAttributes
//                                        .CHARACTERISTIC_GLUSOSE_RECORD_ACCESS_CONTROL)); // read
//            }
            mCharacteristic = service.getCharacteristic(UUID.fromString(GattAttributes
                    .CHARACTERISTIC_GLUSOSE_MEASUREMENT));
            characteristicContext = service.getCharacteristic(UUID.fromString(GattAttributes
                    .CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT));
        }
    }

    @Override
    protected void initializeCharacteristic() {
        Log.i(TAG, "iniatialize()");
//        writeCharacteristic(characteristicWrite, data);
//        readCharacteristic(characteristicRecordAccess).with(dataReceivedCallback);
        setNotificationCallback(mCharacteristic).with(dataReceivedCallback);
        setNotificationCallback(characteristicContext).with(contextDataReceivedCallback);
        enableNotifications(mCharacteristic).enqueue();
        enableNotifications(characteristicContext).enqueue();
    }

    private DataReceivedCallback contextDataReceivedCallback = (device, data) -> processContext(device, data);

    private ManagerCallback bleManagerCallbacks = new ManagerCallback() {
        @Override
        public void measurementReceiver(@NonNull BluetoothDevice device, @NonNull Data data) {
            if (glucoseDataCallback == null) return;

            Log.w(TAG, "DATA: " + data.toString());
            Log.w(TAG, "VALUE: " + Arrays.toString(data.getValue()));
            int offset = 0;
            final int flags = data.getIntValue(Data.FORMAT_UINT8, offset);
            offset += 1;

            final boolean timeOffsetPresent = (flags & 0x01) > 0;
            final boolean typeAndLocationPresent = (flags & 0x02) > 0;
            final int concentrationUnit = (flags & 0x04) > 0 ? GlucoseRecord.UNIT_molpl : GlucoseRecord.UNIT_kgpl;
            final boolean sensorStatusAnnunciationPresent = (flags & 0x08) > 0;
            final boolean contextInfoFollows = (flags & 0x10) > 0;

            // create and fill the new record
            final int sequenceNumber = data.getIntValue(Data.FORMAT_UINT16, offset);
            offset += 2;

            // parse timestamp if present
            final int year = data.getIntValue(Data.FORMAT_UINT16, offset);
            final int month = data.getIntValue(Data.FORMAT_UINT8, offset + 2) - 1;
            final int day = data.getIntValue(Data.FORMAT_UINT8, offset + 3);
            final int hours = data.getIntValue(Data.FORMAT_UINT8, offset + 4);
            final int minutes = data.getIntValue(Data.FORMAT_UINT8, offset + 5);
            final int seconds = data.getIntValue(Data.FORMAT_UINT8, offset + 6);

            final Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hours, minutes, seconds);
            final String timestamp = DateUtils.convertDateTimeToUTC(calendar.getTime());
            offset += 7;

            if (timeOffsetPresent) {
                // time offset is ignored in the current release
                final int timeOffset = data.getIntValue(Data.FORMAT_SINT16, offset);
                offset += 2;
            }

            Float glucose = null;
            int type = 0;
            int sampleLocation = 0;
            if (typeAndLocationPresent) {
                glucose = data.getFloatValue(Data.FORMAT_SFLOAT, offset);
                final int typeAndLocation = data.getIntValue(Data.FORMAT_UINT8, offset + 2);
                type = (typeAndLocation & 0xF0) >> 4;
                sampleLocation = (typeAndLocation & 0x0F);
                offset += 3;
            }
            int status = 0;
            if (sensorStatusAnnunciationPresent) {
                status = data.getIntValue(Data.FORMAT_UINT16, offset);
            }

            final GlucoseRecord record = new GlucoseRecord();
            record.sequenceNumber = sequenceNumber;
            record.timestamp = timestamp;
            record.glucoseConcentration = glucose != null ? Math.round(glucose) : 0;
            record.unit = concentrationUnit == GlucoseRecord.UNIT_kgpl ? "kg/L" : "mol/L";
            record.type = type;
            record.sampleLocation = sampleLocation;
            record.status = status;

            // insert the new record to storage
            mRecords.put(record.sequenceNumber, record);
            new Handler().postDelayed(() -> {
                // if there is no context information following the measurement data,
                // notify callback about the new record
                if (!contextInfoFollows) {
                    Log.w(TAG, "PARSE: " + record.glucoseConcentration);
                    glucoseDataCallback.onMeasurementReceived(
                            device,
                            record.glucoseConcentration,
                            "",
                            timestamp
                    );
                }
            }, 300);
        }

        @Override
        public void onDeviceConnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connecting to " + device.getName());
        }

        @Override
        public void onDeviceConnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Connected to " + device.getName());
            glucoseDataCallback.onConnected(device);
        }

        @Override
        public void onDeviceDisconnecting(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnecting from " + device.getName());
        }

        @Override
        public void onDeviceDisconnected(@NonNull BluetoothDevice device) {
            Log.i(TAG, "Disconnected from " + device.getName());
            glucoseDataCallback.onDisconnected(device);
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

//    /**
//     * Convert value glucose for String.
//     *
//     * @param value
//     * @return String
//     */
//    private String validateGlucoseValue(int value) {
//        String value_formated = "";
//        double value = measurement.getValue();
//
//        if (measurement.getUnit().equals("kg/L")) {
//            measurement.setUnit("mg/dL");
//            value = value * 100000;
//        }
//
//        if (value > 600) {
//            value_formated = "HI"; // The blood glucose value may be above the reading range of the system.
//        } else if (value < 0) {
//            value_formated = "LO"; // The blood glucose value may be below the reading range of the system.
//        } else {
//            value_formated = String.format("%02d", (int) value);
//        }
//
//        return value_formated;
//    }

    /**
     * Parse for the ACCU-CHEK device, according to GATT.
     * Supported Models: Accu-Chek Performa Connect.
     * <p>
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.glucose_measurement_context.xml>}
     */
    private void processContext(final BluetoothDevice device, final Data data) {
        Log.w(TAG, "CONTEXT DATA: " + data.toString());
        Log.w(TAG, "CONTEXT VALUE: " + Arrays.toString(data.getValue()));
        final int UNIT_kg = 0;
        final int UNIT_l = 1;

        int offset = 0;
        final int flags = data.getIntValue(Data.FORMAT_UINT8, offset);
        offset += 1;

        final boolean carbohydratePresent = (flags & 0x01) > 0;
        final boolean mealPresent = (flags & 0x02) > 0;
        final boolean testerHealthPresent = (flags & 0x04) > 0;
        final boolean exercisePresent = (flags & 0x08) > 0;
        final boolean medicationPresent = (flags & 0x10) > 0;
        final int medicationUnit = (flags & 0x20) > 0 ? UNIT_l : UNIT_kg;
        final boolean hbA1cPresent = (flags & 0x40) > 0;
        final boolean moreFlagsPresent = (flags & 0x80) > 0;

        final int sequenceNumber = data.getIntValue(Data.FORMAT_UINT16, offset);
        offset += 2;

        if (moreFlagsPresent) // not supported yet
            offset += 1;

        if (carbohydratePresent) {
            final int carbohydrateId = data.getIntValue(Data.FORMAT_UINT8, offset);
            final float carbohydrateUnits = data.getFloatValue(Data.FORMAT_SFLOAT, offset + 1);
            offset += 3;
        }

        String mealStr = "";
        if (mealPresent) {
            final int meal = data.getIntValue(Data.FORMAT_UINT8, offset);
            mealStr = getMeal(meal);
            offset += 1;
        }

        if (testerHealthPresent) {
            final int testerHealth = data.getIntValue(Data.FORMAT_UINT8, offset);
            final int tester = (testerHealth & 0xF0) >> 4;
            final int health = (testerHealth & 0x0F);
            offset += 1;
        }

        if (exercisePresent) {
            final int exerciseDuration = data.getIntValue(Data.FORMAT_UINT16, offset);
            final int exerciseIntensity = data.getIntValue(Data.FORMAT_UINT8, offset + 2);
            offset += 3;
        }

        if (medicationPresent) {
            final int medicationId = data.getIntValue(Data.FORMAT_UINT8, offset);
            final float medicationQuantity = data.getFloatValue(Data.FORMAT_SFLOAT, offset + 1);
            offset += 3;
        }

        if (hbA1cPresent) {
            final float HbA1c = data.getFloatValue(Data.FORMAT_SFLOAT, offset);
        }

        final GlucoseRecord record = mRecords.get(sequenceNumber);
        if (record == null) {
            Log.w(TAG, "Context information with unknown sequence number: " + sequenceNumber);
            return;
        }

        glucoseDataCallback.onMeasurementReceived(
                device,
                record.glucoseConcentration,
                mealStr,
                record.timestamp
        );
    }

    /**
     * Get Meal value.
     *
     * @param id int
     * @return String
     */
    private String getMeal(final int id) {
        switch (id) {
            case 1:
                return BloodGlucoseMealType.PREPRANDIAL;
            case 2:
                return BloodGlucoseMealType.POSTPRANDIAL;
            case 3:
                return BloodGlucoseMealType.FASTING;
            case 4:
                return BloodGlucoseMealType.CASUAL;
            case 5:
                return BloodGlucoseMealType.BEDTIME;
            default:
                return "";
        }
    }

    /*
     * Copyright (c) 2015, Nordic Semiconductor
     * All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
     *
     * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
     *
     * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
     * documentation and/or other materials provided with the distribution.
     *
     * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
     * software without specific prior written permission.
     *
     * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
     * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
     * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
     * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
     * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
     * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    public class GlucoseRecord {
        public static final int UNIT_kgpl = 0;
        public static final int UNIT_molpl = 1;

        /**
         * Record sequence number
         */
        protected int sequenceNumber;
        /**
         * The base time of the measurement
         */
        protected String timestamp;
        /**
         * Time offset of the record
         */
        protected int timeOffset;
        /**
         * The glucose concentration. 0 if not present
         */
        protected int glucoseConcentration;
        /**
         * Concentration unit. One of the following: {@link GlucoseRecord#UNIT_kgpl}, {@link GlucoseRecord#UNIT_molpl}
         */
        protected String unit;
        /**
         * The type of the record. 0 if not present
         */
        protected int type;
        /**
         * The sample location. 0 if unknown
         */
        protected int sampleLocation;
        /**
         * Sensor status annunciation flags. 0 if not present
         */
        protected int status;

        protected MeasurementContext context;

        public class MeasurementContext {
            public static final int UNIT_kg = 0;
            public static final int UNIT_l = 1;

            /**
             * One of the following:<br/>
             * 0 Not present<br/>
             * 1 Breakfast<br/>
             * 2 Lunch<br/>
             * 3 Dinner<br/>
             * 4 Snack<br/>
             * 5 Drink<br/>
             * 6 Supper<br/>
             * 7 Brunch
             */
            protected int carbohydrateId;
            /**
             * Number of kilograms of carbohydrate
             */
            protected float carbohydrateUnits;
            /**
             * One of the following:<br/>
             * 0 Not present<br/>
             * 1 Preprandial (before meal)<br/>
             * 2 Postprandial (after meal)<br/>
             * 3 Fasting<br/>
             * 4 Casual (snacks, drinks, etc.)<br/>
             * 5 Bedtime
             */
            protected int meal;
            /**
             * One of the following:<br/>
             * 0 Not present<br/>
             * 1 Self<br/>
             * 2 Health Care Professional<br/>
             * 3 Lab test<br/>
             * 15 Tester value not available
             */
            protected int tester;
            /**
             * One of the following:<br/>
             * 0 Not present<br/>
             * 1 Minor health issues<br/>
             * 2 Major health issues<br/>
             * 3 During menses<br/>
             * 4 Under stress<br/>
             * 5 No health issues<br/>
             * 15 Tester value not available
             */
            protected int health;
            /**
             * Exercise duration in seconds. 0 if not present
             */
            protected int exerciseDuration;
            /**
             * Exercise intensity in percent. 0 if not present
             */
            protected int exerciseIntensity;
            /**
             * One of the following:<br/>
             * 0 Not present<br/>
             * 1 Rapid acting insulin<br/>
             * 2 Short acting insulin<br/>
             * 3 Intermediate acting insulin<br/>
             * 4 Long acting insulin<br/>
             * 5 Pre-mixed insulin
             */
            protected int medicationId;
            /**
             * Quantity of medication. See {@link #medicationUnit} for the unit.
             */
            protected float medicationQuantity;
            /**
             * One of the following: {@link GlucoseRecord.MeasurementContext#UNIT_kg}, {@link GlucoseRecord.MeasurementContext#UNIT_l}.
             */
            protected int medicationUnit;
            /**
             * HbA1c value. 0 if not present
             */
            protected float HbA1c;
        }
    }
}
