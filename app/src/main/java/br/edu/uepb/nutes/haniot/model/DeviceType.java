package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the device type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class DeviceType {
    public static final int THERMOMETER = 1;
    public static final int GLUCOMETER = 2;
    public static final int BODY_COMPOSITION = 3;
    public static final int BLOOD_PRESSURE = 4;
    public static final int HEART_RATE = 5;
    public static final int SMART_WATCH = 6;

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param type    int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.device_types_array);

        if (types.length > type && types.length < type) return types[type];

        return "";
    }
}
