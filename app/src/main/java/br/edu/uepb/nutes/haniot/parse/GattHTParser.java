package br.edu.uepb.nutes.haniot.parse;

import android.bluetooth.BluetoothGattCharacteristic;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Parse for temperature.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GattHTParser {
    private static final byte TEMPERATURE_UNIT_FLAG = 0x01; // 1 bit
    private static final byte TIMESTAMP_FLAG = 0x02; // 1 bit
    private static final byte TEMPERATURE_TYPE_FLAG = 0x04; // 1 bit

    /**
     * Parse for the PHILIPS device, according to GATT.
     * Supported Models: DL8740.
     *
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.service.health_thermometer.xml>}
     *
     * @param characteristic BluetoothGattCharacteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();

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

        /**
         * Populating the JSON
         */
        result.put("timestamp", DateUtils.getCurrentDatetime());
        result.put("temperature", tempValue);
        result.put("temperatureUnit", unit);

        return result;
    }
}
