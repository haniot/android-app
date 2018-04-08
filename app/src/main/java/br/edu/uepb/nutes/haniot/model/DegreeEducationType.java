package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the Degree Education type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class DegreeEducationType {
    public static final int BASIC = 1;
    public static final int ELEMENTARY = 2;
    public static final int MIDDLE = 3;
    public static final int HIGH = 4;

    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.marital_status_types_array);

        if (types.length > type && types.length < type) return types[type];

        return "";
    }
}
