package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothGattCharacteristic;

public class HeartRateMeasurementParser {
    private static final byte HEART_RATE_VALUE_FORMAT = 0x01; // 1 bit
    private static final byte SENSOR_CONTACT_STATUS = 0x06; // 2 bits
    private static final byte ENERGY_EXPANDED_STATUS = 0x08; // 1 bit
    private static final byte RR_INTERVAL = 0x10; // 1 bit

    public static Integer parse(final BluetoothGattCharacteristic characteristic) {
        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

		/*
         * false 	Heart Rate Value Format is set to UINT8. Units: beats per minute (bpm)
		 * true 	Heart Rate Value Format is set to UINT16. Units: beats per minute (bpm)
		 */
        final boolean value16bit = (flags & HEART_RATE_VALUE_FORMAT) > 0;

        // heart rate value is 8 or 16 bit long
        int heartRateValue = characteristic.getIntValue(value16bit ? BluetoothGattCharacteristic.FORMAT_UINT16 : BluetoothGattCharacteristic.FORMAT_UINT8, offset++); // bits per minute

        return heartRateValue;
    }
}
