package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the Marital Status type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MaritalStatusType {
    public static final int SINGLE = 1;
    public static final int MARRIED = 2;
    public static final int SEPARATED = 3;
    public static final int DIVORCED = 4;
    public static final int WIDOWED = 5;

    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.marital_status_types_array);

        if (types.length > type && types.length < type) return types[type];

        return "";
    }
}
