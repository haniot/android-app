package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;
import android.content.res.Resources;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the device type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class DeviceType {
    public static final String THERMOMETER = "thermometer";
    public static final String GLUCOMETER = "glucometer";
    public static final String BODY_COMPOSITION = "body_composition";
    public static final String BLOOD_PRESSURE = "blood_pressure";
    public static final String HEART_RATE = "heart_rate";
    public static final String SMARTWATCH = "smartwatch";
    public static final String SMARTBAND = "smartband";

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context {@link Context}
     * @param type Measurement type
     * @return String
     */
    public static String getString(Context context, String type) {
        if (type == null || type.isEmpty()) return "";
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(type, "string", context.getPackageName()));
    }
}
