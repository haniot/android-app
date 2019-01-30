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
    public static final int BLOOD_GLUCOSE = 2;
    public static final int TEMPERATURE = 1;
    public static final int BODY_MASS = 3;
    public static final int BODY_FAT = 4;
    public static final int BLOOD_PRESSURE_SYSTOLIC = 5;
    public static final int BLOOD_PRESSURE_DIASTOLIC = 6;
    public static final int HEART_RATE = 7;
    public static final int BMI = 8; // Body Mass Index - IMC
    public static final int RMR = 9; // Resting Metabolic Rate
    public static final int BMR = 10; // Basal Metabolic Rate
    public static final int MUSCLE_MASS = 11;
    public static final int VISCERAL_FAT = 12;
    public static final int BODY_AGE = 13;
    public static final int STEPS = 14;
    public static final int DISTANCE = 15;
    public static final int CALORIES_BURNED = 16;
    public static final int HEIGHT = 17;
    public static final int CIRCUMFERENCE = 18;

    public static int[] SUPPORTED_TYPES = {
            TEMPERATURE,
            BLOOD_GLUCOSE,
            BODY_MASS,
            BODY_FAT,
            BLOOD_PRESSURE_SYSTOLIC,
            BLOOD_PRESSURE_DIASTOLIC,
            HEART_RATE,
            BMI,
            RMR,
            BMR,
            MUSCLE_MASS,
            VISCERAL_FAT,
            BODY_AGE,
            HEIGHT,
            CIRCUMFERENCE
    };

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param type    int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.measurement_types_array);

        if (type < 1 || type > types.length) return "";

        return types[type - 1];
    }

    /**
     * Checks whether a type is supported.
     *
     * @param type int
     * @return boolean
     */
    public static boolean isSupportedType(int type) {
        for (int x : SUPPORTED_TYPES)
            if (x == type) return true;

        return false;
    }
}