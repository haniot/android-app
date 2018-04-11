package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

/**
 * Contains constants that represent the training type.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class TrainingType {
    public static final int WALKING = 1;
    public static final int RUNNING = 2;
    public static final int CYCLING = 3;

    /**
     * Retrieve the mapped type name in resources.
     *
     * @param context ContextMeasurement
     * @param type int
     * @return String
     */
    public static String getString(Context context, int type) {
        String types[] = context.getResources().getStringArray(R.array.training_types_array);

        if (type < 1 || type > types.length) return "";

        return types[type - 1];
    }
}
