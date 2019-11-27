package br.edu.uepb.nutes.haniot.data.type;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the type of measurement context.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ContextMeasurementType {
    public static final int GLUCOSE_CARBOHYDRATE = 1;
    public static final int GLUCOSE_MEAL = 2;
    public static final int GLUCOSE_LOCATION = 3;
    public static final int GLUCOSE_TYPE = 4;
    public static final int TEMPERATURE_TYPE = 5;

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param type    int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.measurement_context_types_array);

        if (type < 1 || type > types.length) return "";

        return types[type - 1];
    }
}
