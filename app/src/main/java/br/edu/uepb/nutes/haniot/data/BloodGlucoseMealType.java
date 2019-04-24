package br.edu.uepb.nutes.haniot.data;

import android.content.Context;
import android.content.res.Resources;

/**
 * Contains constants that represent the glucose meal type.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class BloodGlucoseMealType {
    public static final String PREPRANDIAL = "preprandial";
    public static final String POSTPRANDIAL = "postprandial";
    public static final String FASTING = "fasting";
    public static final String CASUAL = "casual";
    public static final String BEDTIME = "bedtime";

    public static String getString(Context context, String id) {
        if (id == null || id.isEmpty()) return "";
        Resources res = context.getResources();
        return res.getString(res.getIdentifier(id, "string", context.getPackageName()));
    }
}