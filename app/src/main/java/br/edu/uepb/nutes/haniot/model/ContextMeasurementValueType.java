package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants representing the values of the measurement context types.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ContextMeasurementValueType {
    public static final int OTHER = 1;

    /**
     * GLUCOSE CARBOHYDRATE
     */
    public static final int GLUCOSE_CARBOHYDRATE_BREAKFAST = 2;
    public static final int GLUCOSE_CARBOHYDRATE_LUNCH = 3;
    public static final int GLUCOSE_CARBOHYDRATE_DINNER = 4;
    public static final int GLUCOSE_CARBOHYDRATE_SNACK = 5;
    public static final int GLUCOSE_CARBOHYDRATE_DRINK = 6;
    public static final int GLUCOSE_CARBOHYDRATE_SUPPER = 7;
    public static final int GLUCOSE_CARBOHYDRATE_BRUNCH = 8;

    /**
     * GLUCOSE MEAL
     */
    public static final int GLUCOSE_MEAL_PREPRANDIAL = 9;
    public static final int GLUCOSE_MEAL_POSTPRANDIAL = 10;
    public static final int GLUCOSE_MEAL_FASTING = 11;
    public static final int GLUCOSE_MEAL_CASUAL = 12;
    public static final int GLUCOSE_MEAL_BEDTIME = 13;

    /**
     * GLUCOSE LOCATION
     */
    public static final int GLUCOSE_LOCATION_FINGER = 14;
    public static final int GLUCOSE_LOCATION_ALTERNATE_SITE_TEST = 15;
    public static final int GLUCOSE_LOCATION_EARLOBE = 16;
    public static final int GLUCOSE_LOCATION_CONTROL_SOLUTION = 17;
    public static final int GLUCOSE_LOCATION_SAMPLE_LOCATION_NOT_AVAILABLE = 18;

    /**
     * GLUCOSE TYPE
     */
    public static final int GLUCOSE_TYPE_CAPILLARY_WHOLE_BLOOD = 19;
    public static final int GLUCOSE_TYPE_CAPILLARY_PLASMA = 20;
    public static final int GLUCOSE_TYPE_VENOUS_WHOLE_BLOOD = 21;
    public static final int GLUCOSE_TYPE_VENOUS_PLASMA = 22;
    public static final int GLUCOSE_TYPE_ARTERIAL_WHOLE_BLOOD = 23;
    public static final int GLUCOSE_TYPE_ARTERIAL_PLASMA = 24;
    public static final int GLUCOSE_TYPE_UNDERTERMINED_WHOLE_BLOOD = 25;
    public static final int GLUCOSE_TYPE_UNDERTERMINED_PLASMA = 26;
    public static final int GLUCOSE_TYPE_INTERSTITIAL_FLUID = 27;
    public static final int GLUCOSE_TYPE_CONTROL_SOLUTION = 28;

    /**
     * TEMPERATURE TYPE
     */
    public static final int TEMPERATURE_TYPE_ARMPIT = 29;
    public static final int TEMPERATURE_TYPE_BODY = 30;
    public static final int TEMPERATURE_TYPE_EAR = 31;
    public static final int TEMPERATURE_TYPE_FINGER = 32;
    public static final int TEMPERATURE_TYPE_GASTRO_INTESTINAL_TRACT = 33;
    public static final int TEMPERATURE_TYPE_MOUTH = 34;
    public static final int TEMPERATURE_TYPE_RECTUM = 35;
    public static final int TEMPERATURE_TYPE_TOE = 36;
    public static final int TEMPERATURE_TYPE_TYPANUM = 37;

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param valueId    int
     * @return String
     */
    public static String getString(Context context, int valueId) {
        String values[] = context.getResources().getStringArray(R.array.measurement_values_context_types_array);

        if (values.length >= valueId) return values[valueId - 1];

        return "";
    }
}
