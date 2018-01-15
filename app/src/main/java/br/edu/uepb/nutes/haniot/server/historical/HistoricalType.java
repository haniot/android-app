package br.edu.uepb.nutes.haniot.server.historical;

/**
 * Historical Types.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HistoricalType {
    public static int MEASUREMENTS_USER = 1;
    public static int MEASUREMENTS_DEVICE_USER = 2;
    public static int MEASUREMENTS_TYPE_USER = 3;

    public static int[] SUPPORTED_TYPES = {
            MEASUREMENTS_USER,
            MEASUREMENTS_DEVICE_USER,
            MEASUREMENTS_TYPE_USER
    };

    /**
     * Checks whether a type is supported.
     *
     * @param type int
     * @return boolean
     */
    public static boolean isSupportedType(int type) {
        for (int x : SUPPORTED_TYPES)
            if (x == type) return true;

        return false;
    }
}
