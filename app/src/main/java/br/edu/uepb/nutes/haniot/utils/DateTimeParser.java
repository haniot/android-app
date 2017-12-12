package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Calendar;

public class DateTimeParser {
    /**
     * Parses the date and time info.
     *
     * @param characteristic
     * @return calendar
     */
    public static Calendar parse(final BluetoothGattCharacteristic characteristic) {
        return parse(characteristic, 0);
    }

    /**
     * Parses the date and time info. This data has 7 bytes
     *
     * @param characteristic
     * @param offset offset to start reading the time
     * @return time in Calendar
     */
    static Calendar parse(final BluetoothGattCharacteristic characteristic, final int offset) {
        final int year = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        final int month = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2) - 1;
        final int day = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 3);
        final int hours = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 4);
        final int minutes = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 5);
        final int seconds = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 6);

        final Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, hours, minutes, seconds);

        return calendar;
    }
}
