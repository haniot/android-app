package br.edu.uepb.nutes.haniot.parse;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Parse for body composition.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class IEEE11073BCParser {
    /**
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(String data) throws JSONException {
        JSONObject result = new JSONObject();

        /**
         * Timestamp current
         */
        result.put("timestamp", DateUtils.getCurrentTimestamp());

        return null;
    }
}
