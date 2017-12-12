package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose;

public class GlucoseMeasurementParser {
    private static final int UNIT_kgpl = 0;
    private static final int UNIT_molpl = 1;

    private static final int STATUS_DEVICE_BATTERY_LOW = 0x0001;
    private static final int STATUS_SENSOR_MALFUNCTION = 0x0002;
    private static final int STATUS_SAMPLE_SIZE_FOR_BLOOD_OR_CONTROL_SOLUTION_INSUFFICIENT = 0x0004;
    private static final int STATUS_STRIP_INSERTION_ERROR = 0x0008;
    private static final int STATUS_STRIP_TYPE_INCORRECT_FOR_DEVICE = 0x0010;
    private static final int STATUS_SENSOR_RESULT_TOO_HIGH = 0x0020;
    private static final int STATUS_SENSOR_RESULT_TOO_LOW = 0x0040;
    private static final int STATUS_SENSOR_TEMPERATURE_TOO_HIGH = 0x0080;
    private static final int STATUS_SENSOR_TEMPERATURE_TOO_LOW = 0x0100;
    private static final int STATUS_SENSOR_READ_INTERRUPTED = 0x0200;
    private static final int STATUS_GENERAL_DEVICE_FAULT = 0x0400;
    private static final int STATUS_TIME_FAULT = 0x0800;

    private static final byte TIMESTAMP_FLAG = 0x01; // 1 bit

    private static Context context;

    public static MeasurementGlucose parse(final BluetoothGattCharacteristic characteristic, final Context c) {
        context = c;
        MeasurementGlucose measurement = new MeasurementGlucose();

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);
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

        // create and fill the new record
        int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        measurement.setSequenceNumber(sequenceNumber);
        offset += 2;

        if (timestampIncluded) {
            Calendar calendar = DateTimeParser.parse(characteristic, offset);
            measurement.setRegistrationTime(calendar.getTimeInMillis());
            offset += 7;
        }

        if (timeOffsetPresent) {
            // time offset is ignored in the current release
            final int timeOffset = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, offset);
            offset += 2;
        }

        if (typeAndLocationPresent) {
            float glucoseConcentration = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            int typeAndLocation = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            int type = (typeAndLocation & 0xF0) >> 4;
            int location = (typeAndLocation & 0x0F);

            String unit = concentrationUnit == UNIT_kgpl ? "kg/L" : "mol/L";
            offset += 3;

            measurement.setValue(glucoseConcentration);
            measurement.setUnit(unit);
            measurement.setType(getType(type));
        }

        if (sensorStatusAnnunciationPresent) {
            int status = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            builder.append("Status:\n").append(getStatusAnnunciation(status));
            measurement.setStatusAnnunciation(getStatusAnnunciation(status));
        }

        return measurement;
    }

    private static String getType(final int type) {
        switch (type) {
            case 1:
                return context.getString(R.string.glucose_type_capillary_whole);
            case 2:
                return context.getString(R.string.glucose_type_capillary_plasma);
            case 3:
                return context.getString(R.string.glucose_type_venous_whole);
            case 4:
                return context.getString(R.string.glucose_type_venous_plasma);
            case 5:
                return context.getString(R.string.glucose_type_arterial_whole);
            case 6:
                return context.getString(R.string.glucose_type_arterial_plama);
            case 7:
                return context.getString(R.string.glucose_type_undertermined_whole);
            case 8:
                return context.getString(R.string.glucose_type_undertermined_plasma);
            case 9:
                return context.getString(R.string.glucose_type_interstitial);
            case 10:
                return context.getString(R.string.glucose_type_control_solution);
            default:
                return context.getString(R.string.unknown);
        }
    }

    private static String getLocation(final int location) {
        switch (location) {
            case 1:
                return context.getString(R.string.glucose_location_finger);
            case 2:
                return context.getString(R.string.glucose_location_alternate);
            case 3:
                return context.getString(R.string.glucose_location_earlobe);
            case 4:
                return context.getString(R.string.glucose_location_control_solution);
            case 15:
                return context.getString(R.string.glucose_location_sample_not_available);
            default:
                return context.getString(R.string.unknown);
        }
    }

    private static String getStatusAnnunciation(final int status) {
        if ((status & STATUS_DEVICE_BATTERY_LOW) > 0)
            return context.getString(R.string.glucose_status_annunciation_battery_low);
        if ((status & STATUS_SENSOR_MALFUNCTION) > 0)
            return context.getString(R.string.glucose_status_annunciation_sensor_malfunction);
        if ((status & STATUS_SAMPLE_SIZE_FOR_BLOOD_OR_CONTROL_SOLUTION_INSUFFICIENT) > 0)
            return context.getString(R.string.glucose_status_annunciation_sample_size_insufficient);
        if ((status & STATUS_STRIP_INSERTION_ERROR) > 0)
            return context.getString(R.string.glucose_status_annunciation_strip_insertion_error);
        if ((status & STATUS_STRIP_TYPE_INCORRECT_FOR_DEVICE) > 0)
            return context.getString(R.string.glucose_status_annunciation_strip_incorrect);
        if ((status & STATUS_SENSOR_RESULT_TOO_HIGH) > 0)
            return context.getString(R.string.glucose_status_annunciation_result_high);
        if ((status & STATUS_SENSOR_RESULT_TOO_LOW) > 0)
            return context.getString(R.string.glucose_status_annunciation_result_lower);
        if ((status & STATUS_SENSOR_TEMPERATURE_TOO_HIGH) > 0)
            return context.getString(R.string.glucose_status_annunciation_sensor_temperature_high);
        if ((status & STATUS_SENSOR_TEMPERATURE_TOO_LOW) > 0)
            return context.getString(R.string.glucose_status_annunciation_sensor_temperature_lower);
        if ((status & STATUS_SENSOR_READ_INTERRUPTED) > 0)
            return context.getString(R.string.glucose_status_annunciation_sensor_read_interrupted);
        if ((status & STATUS_GENERAL_DEVICE_FAULT) > 0)
            return context.getString(R.string.glucose_status_annunciation_general_device_fault);
        if ((status & STATUS_TIME_FAULT) > 0)
            return context.getString(R.string.glucose_status_annunciation_sensor_time_fault);

        return context.getString(R.string.unknown);
    }
}
