package br.edu.uepb.nutes.haniot.parse;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Parse for Smart Band.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class MiBand2Parser {

    /**
     * Parse for smart band.
     * Supported Models: (MI BAND 2).
     *
     * @param characteristic
     * @return JSONObject
     * @throws JSONException
     */
    public static JSONObject parse(@NonNull BluetoothGattCharacteristic characteristic) throws JSONException {
        JSONObject result = new JSONObject();
        byte[] data = characteristic.getValue();

        /* Parse data */
        double stepsValue = (double) (((data[1] & 255) | ((data[2] & 255) << 8)));
        double distanceValue = (double) ((((data[5] & 255) | ((data[6] & 255) << 8)) | (data[7] & 16711680)) | ((data[8] & 255) << 24));
        double caloriesValue = (double) ((((data[9] & 255) | ((data[10] & 255) << 8)) | (data[11] & 16711680)) | ((data[12] & 255) << 24));

        /**
         * Populating the JSON
         */
        result.put("steps", stepsValue);
        result.put("stepsUnit", "");
        result.put("distance", distanceValue);
        result.put("distanceUnit", "m");
        result.put("calories", caloriesValue);
        result.put("caloriesUnit", "kcal");
        result.put("timestamp", DateUtils.getCurrentDatetime());

        return result;
    }
}