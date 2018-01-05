package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the measurement type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MeasurementType {
    public static final int TEMPERATURE = 1;
    public static final int BLOOD_GLUCOSE = 2;
    public static final int BODY_MASS = 3;
    public static final int BODY_FAT = 4;
    public static final int BLOOD_PRESSURE_SYSTOLIC = 5;
    public static final int BLOOD_PRESSURE_DIASTOLIC = 6;
    public static final int HEART_RATE = 7;

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param type int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.measurement_types_array);

        if (types.length > type && types.length < type) return types[type];

        return "";
    }
}