package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.MeasurementGlucose;

public class GlucoseMeasurementContextParser {
    private static final int UNIT_kg = 0;
    private static final int UNIT_l = 1;
    private static Context context;

    public static MeasurementGlucose parse(final BluetoothGattCharacteristic characteristic, final Context c) {
        context = c;
        MeasurementGlucose measurement = new MeasurementGlucose();

        int offset = 0;
        final int flags = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset++);

        final boolean carbohydratePresent = (flags & 0x01) > 0;
        final boolean mealPresent = (flags & 0x02) > 0;
        final boolean testerHealthPresent = (flags & 0x04) > 0;
        final boolean exercisePresent = (flags & 0x08) > 0;
        final boolean medicationPresent = (flags & 0x10) > 0;
        final int medicationUnit = (flags & 0x20) > 0 ? UNIT_l : UNIT_kg;
        final boolean hbA1cPresent = (flags & 0x40) > 0;
        final boolean moreFlagsPresent = (flags & 0x80) > 0;

        int sequenceNumber = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
        measurement.setSequenceNumber(sequenceNumber);
        offset += 2;

        if (moreFlagsPresent) // not supported yet
            offset += 1;

        if (carbohydratePresent) {
            int carbohydrateId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float carbohydrateUnits = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            // TODO handle data when you need it

            offset += 3;
        }

        if (mealPresent) {
            int meal = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            measurement.setMeal(getMeal(meal));

            offset += 1;
        }

        if (testerHealthPresent) {
            int testerHealth = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            int tester = (testerHealth & 0xF0) >> 4;
            int health = (testerHealth & 0x0F);
            // TODO handle data when you need it

            offset += 1;
        }

        if (exercisePresent) {
            int exerciseDuration = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
            int exerciseIntensity = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset + 2);
            // TODO handle data when you need it

            offset += 3;
        }

        if (medicationPresent) {
            int medicationId = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, offset);
            float medicationQuantity = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset + 1);
            // TODO handle data when you need it

            offset += 3;
        }

        if (hbA1cPresent) {
            float HbA1c = characteristic.getFloatValue(BluetoothGattCharacteristic.FORMAT_SFLOAT, offset);
            // TODO handle data when you need it
        }

        return measurement;
    }

    private static String getCarbohydrate(final int id) {
        switch (id) {
            case 1:
                return context.getString(R.string.glucose_carbohydrate_breakfast);
            case 2:
                return context.getString(R.string.glucose_carbohydrate_lunch);
            case 3:
                return context.getString(R.string.glucose_carbohydrate_dinner);
            case 4:
                return context.getString(R.string.glucose_carbohydrate_snakck);
            case 5:
                return context.getString(R.string.glucose_carbohydrate_drink);
            case 6:
                return context.getString(R.string.glucose_carbohydrate_supper);
            case 7:
                return context.getString(R.string.glucose_carbohydrate_brunch);
            default:
                return context.getString(R.string.unknown);
        }
    }

    private static String getMeal(final int id) {
        switch (id) {
            case 1:
                return context.getString(R.string.glucose_meal_preprandial);
            case 2:
                return context.getString(R.string.glucose_meal_postprandial);
            case 3:
                return context.getString(R.string.glucose_meal_fasting);
            case 4:
                return context.getString(R.string.glucose_meal_casual);
            case 5:
                return context.getString(R.string.glucose_meal_bedtime);
            default:
                return context.getString(R.string.unknown);
        }
    }
}
