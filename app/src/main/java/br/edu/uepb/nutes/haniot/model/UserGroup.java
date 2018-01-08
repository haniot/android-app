package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the user group.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class UserGroup {
    public static final int SUPER = 1;
    public static final int COMMON = 2;

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
