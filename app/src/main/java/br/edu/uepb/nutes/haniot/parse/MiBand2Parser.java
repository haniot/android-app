package br.edu.uepb.nutes.haniot.parse;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parse for body composition.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MiBand2Parser {

    /**
     * Parse for YUNMAI device.
     * Supported Models: smart scale (M1301, M1302, M1303, M1501).
     *
     * @param data
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(@NonNull final byte[] data) throws JSONException {
        JSONObject result = new JSONObject();

        return result;
    }
}