package br.edu.uepb.nutes.blescanner;

import android.bluetooth.BluetoothGattCharacteristic;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Parse for heart rate.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GattHRParser {
    private static final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit

    /**
     * Parse for the POLAR device, according to GATT.
     * Supported Models: H7, H10.
     *
     * {@link <https://www.bluetooth.com/specifications/gatt/viewer?attributeXmlFile=org.bluetooth.characteristic.heart_rate_measurement.xml>}
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(final BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();
        int offset = 0;
        int heartRateValue = 0;

        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

		/*
         * false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
		 * true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
		 */
        final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

        // heart rate value is 8 or 16 bit long
        heartRateValue = characteristic.getIntValue(value16bit ? BluetoothGattCharacteristic.FORMAT_UINT16 :
                BluetoothGattCharacteristic.FORMAT_UINT8, offset++); // bits per minute

        /**
         * Populating the JSON
         */
        result.put("heartRate", heartRateValue);
        result.put("heartRateUnit", "bpm");
       // result.put("timestamp", DateUtils.getCurrentDatetime());

        return result;
    }
}
