package br.edu.uepb.nutes.haniot.data.type;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the user group.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class UserType {
//    public static final int ADMIN = 1;
//    public static final int HEALTH_PROFESSEIONAL = 2;
//    public static final int NUTRITION = 3;
//    public static final int DENTISTRY = 4;
//    public static final int PATIENT = 4;
    public static final String ADMIN = "admin";
    public static final String HEALTH_PROFESSIONAL = "health_professional";
    public static final String PATIENT = "patient";
    public static final String DENTISTRY = "dentistry";
    public static final String NUTRITION = "nutrition";

    /**
     * Retrieve the mapped group name in resources.
     *
     * @param context ContextMeasurement
     * @param type    int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.user_group_array);

        if (types.length > type && types.length < type) return types[type];

        return "";
    }
}
