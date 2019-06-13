package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;
import android.content.res.Resources;

/**
 * Contains constants that represent the measurement type.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class MeasurementType {
    public static final String BODY_TEMPERATURE = "body_temperature";
    public static final String BODY_MASS = "weight";
    public static final String BODY_FAT = "fat";
    public static final String BLOOD_PRESSURE = "blood_pressure";
    public static final String BLOOD_GLUCOSE = "blood_glucose";
    public static final String HEART_RATE = "heart_rate";
    public static final String HEIGHT = "height";
    public static final String WAIST_CIRC = "waist_circumference";

    public static String getString(Context context, String id) {
        if (id == null || id.isEmpty()) return "";
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(id, "string", context.getPackageName()));
    }
}