package br.edu.uepb.nutes.haniot.model.elderly;

import android.content.Context;

import java.util.Arrays;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the Degree Education type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class DegreeEducationType {
    public static final int BASIC = 0;
    public static final int ELEMENTARY = 1;
    public static final int MIDDLE = 2;
    public static final int HIGH = 3;

    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.degree_education_types_array);

        if (type < 0 || type >= types.length) return "";

        return types[type];
    }

    public static int getId(Context context, String value) {
        String types[] = context.getResources().getStringArray(R.array.degree_education_types_array);
        return Arrays.asList(types).indexOf(value);
    }
}
