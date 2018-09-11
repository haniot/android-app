package br.edu.uepb.nutes.haniot.model.elderly;

import android.content.Context;

import java.util.Arrays;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the Marital Status type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MaritalStatusType {
    public static final int SINGLE = 0;
    public static final int MARRIED = 1;
    public static final int SEPARATED = 2;
    public static final int DIVORCED = 3;
    public static final int WIDOWED = 4;

    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.marital_status_types_array);

        if (type < 0 || type >= types.length) return "";

        return types[type];
    }

    public static int getId(Context context, String value) {
        String types[] = context.getResources().getStringArray(R.array.marital_status_types_array);
        return Arrays.asList(types).indexOf(value);
    }
}
