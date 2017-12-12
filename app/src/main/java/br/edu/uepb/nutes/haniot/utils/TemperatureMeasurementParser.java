package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.MeasurementThermometer;

public class TemperatureMeasurementParser {
    private static final byte TEMPERATURE_UNIT_FLAG = 0x01; // 1 bit
    private static final byte TIMESTAMP_FLAG = 0x02; // 1 bit
    private static final byte TEMPERATURE_TYPE_FLAG = 0x04; // 1 bit

    private static Context context;

    public static MeasurementThermometer parse(final BluetoothGattCharacteristic characteristic, Context c) {
        context = c;

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        /**
         * false 	Temperature is in Celsius degrees
         * true 	Temperature is in Fahrenheit degrees
         */
        final boolean fahrenheit = (flags & TEMPERATURE_UNIT_FLAG) > 0;

        /**
         * false 	No Timestamp in the packet
         * true 	There is a timestamp information
         */
        final boolean timestampIncluded = (flags & TIMESTAMP_FLAG) > 0;

        /**
         * false 	Temperature type is not included
         * true 	Temperature type included in the packet
         */
        final boolean temperatureTypeIncluded = (flags & TEMPERATURE_TYPE_FLAG) > 0;

        final float tempValue = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_FLOAT, offset);
        offset += 4;

        Calendar timestamp = null;
        if (timestampIncluded) {
            timestamp = DateTimeParser.parse(characteristic, offset);
            offset += 7;
        }

        int type = -1;
        if (temperatureTypeIncluded) {
            type = characteristic.getValue()[offset];
        }

        String unit = fahrenheit ? "°F" : "°C";

        return new MeasurementThermometer(tempValue, unit);
    }

    private static String getType(int type) {
        switch (type) {
            case 1:
                return context.getString(R.string.temperatre_type_armpit);
            case 2:
                return context.getString(R.string.temperatre_type_body);
            case 3:
                return context.getString(R.string.temperatre_type_ear);
            case 4:
                return context.getString(R.string.temperatre_type_finger);
            case 5:
                return context.getString(R.string.temperatre_type_gastro);
            case 6:
                return context.getString(R.string.temperatre_type_mouth);
            case 7:
                return context.getString(R.string.temperatre_type_rectum);
            case 8:
                return context.getString(R.string.temperatre_type_toe);
            case 9:
                return context.getString(R.string.temperatre_type_typanum);
            default:
                return context.getString(R.string.unknown);
        }
    }
}
