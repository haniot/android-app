package br.edu.uepb.nutes.haniot.parse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementValueType;

/**
 * Parse JSON to ContextMeasurement Object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class JsonToContextParser {

    /**
     * Receives one or more JSON in string format and returns objects of type ContextMeasurement.
     *
     * @param data String
     * @return List<ContextMeasurement>
     * @throws JSONException
     */
    public static List<ContextMeasurement> parse(String... data) throws JSONException {
        List<ContextMeasurement> result = new ArrayList<>();
        ContextMeasurement item = null;
        JSONObject o = null;

        for (String json : data) {
            o = new JSONObject(json);
            if (o.has("glucoseCarbohydrateId")) {
                item = glucoseCarbohydrate(json);
                if (!result.contains(item)) result.add(item);
            }

            if (o.has("glucoseMealId")) {
                item = glucoseMeal(json);
                if (!result.contains(item)) result.add(item);
            }

            if (o.has("glucoseTypeId")) {
                item = glucoseType(json);
                if (!result.contains(item)) result.add(item);
            }

            if (o.has("glucoseLocationId")) {
                item = glucoseLocation(json);
                if (!result.contains(item)) result.add(item);
            }

            if (o.has("temperatureTypeId")) {
                item = temperatureType(json);
                if (!result.contains(item)) result.add(item);
            }
        }

        return result;
    }

    /**
     * Convert json to Glucose Carbohydrate ContextMeasurement.
     *
     * @param json String
     * @return ContextMeasurement
     * @throws JSONException
     */
    public static ContextMeasurement glucoseCarbohydrate(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        int value;

        int id = o.getInt("glucoseCarbohydrateId");

        switch (id) {
            case 1:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_BREAKFAST;
                break;
            case 2:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_LUNCH;
                break;
            case 3:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_DINNER;
                break;
            case 4:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_SNACK;
                break;
            case 5:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_DRINK;
                break;
            case 6:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_SUPPER;
                break;
            case 7:
                value = ContextMeasurementValueType.GLUCOSE_CARBOHYDRATE_BRUNCH;
                break;
            default:
                value = ContextMeasurementValueType.OTHER;
                break;
        }

        return new ContextMeasurement(value, ContextMeasurementType.GLUCOSE_CARBOHYDRATE);
    }

    /**
     * Convert json to Glucose Meal ContextMeasurement.
     *
     * @param json String
     * @return ContextMeasurement
     * @throws JSONException
     */
    public static ContextMeasurement glucoseMeal(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        int value;

        int id = o.getInt("glucoseMealId");

        switch (id) {
            case 1:
                value = ContextMeasurementValueType.GLUCOSE_MEAL_PREPRANDIAL;
                break;
            case 2:
                value = ContextMeasurementValueType.GLUCOSE_MEAL_POSTPRANDIAL;
                break;
            case 3:
                value = ContextMeasurementValueType.GLUCOSE_MEAL_FASTING;
                break;
            case 4:
                value = ContextMeasurementValueType.GLUCOSE_MEAL_CASUAL;
                break;
            case 5:
                value = ContextMeasurementValueType.GLUCOSE_MEAL_BEDTIME;
                break;
            default:
                value = ContextMeasurementValueType.OTHER;
                break;
        }

        return new ContextMeasurement(value, ContextMeasurementType.GLUCOSE_MEAL);
    }

    /**
     * Convert json to Glucose Type ContextMeasurement.
     *
     * @param json String
     * @return ContextMeasurement
     * @throws JSONException
     */
    public static ContextMeasurement glucoseType(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        int value;

        int id = o.getInt("glucoseTypeId");

        switch (id) {
            case 1:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_CAPILLARY_WHOLE_BLOOD;
                break;
            case 2:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_CAPILLARY_PLASMA;
                break;
            case 3:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_VENOUS_WHOLE_BLOOD;
                break;
            case 4:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_VENOUS_PLASMA;
                break;
            case 5:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_ARTERIAL_WHOLE_BLOOD;
                break;
            case 6:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_ARTERIAL_PLASMA;
                break;
            case 7:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_UNDERTERMINED_WHOLE_BLOOD;
                break;
            case 8:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_UNDERTERMINED_PLASMA;
                break;
            case 9:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_INTERSTITIAL_FLUID;
                break;
            case 10:
                value = ContextMeasurementValueType.GLUCOSE_TYPE_CONTROL_SOLUTION;
                break;
            default:
                value = ContextMeasurementValueType.OTHER;
                break;
        }

        return new ContextMeasurement(value, ContextMeasurementType.GLUCOSE_TYPE);
    }

    /**
     * Convert json to Glucose Sample Location ContextMeasurement.
     *
     * @param json String
     * @return ContextMeasurement
     * @throws JSONException
     */
    public static ContextMeasurement glucoseLocation(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        int value;

        int id = o.getInt("glucoseLocationId");

        switch (id) {
            case 1:
                value = ContextMeasurementValueType.GLUCOSE_LOCATION_FINGER;
                break;
            case 2:
                value = ContextMeasurementValueType.GLUCOSE_LOCATION_ALTERNATE_SITE_TEST;
                break;
            case 3:
                value = ContextMeasurementValueType.GLUCOSE_LOCATION_EARLOBE;
                break;
            case 4:
                value = ContextMeasurementValueType.GLUCOSE_LOCATION_CONTROL_SOLUTION;
                break;
            default:
                value = ContextMeasurementValueType.OTHER;
                break;
        }

        return new ContextMeasurement(value, ContextMeasurementType.GLUCOSE_LOCATION);
    }

    /**
     * Convert json to Temperature Type ContextMeasurement.
     *
     * @param json String
     * @return ContextMeasurement
     * @throws JSONException
     */
    public static ContextMeasurement temperatureType(String json) throws JSONException {
        JSONObject o = new JSONObject(json);
        int value;

        int id = o.getInt("temperatureTypeId");

        switch (id) {
            case 1:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_ARMPIT;
                break;
            case 2:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_BODY;
                break;
            case 3:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_EAR;
                break;
            case 4:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_FINGER;
                break;
            case 5:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_GASTRO_INTESTINAL_TRACT;
                break;
            case 6:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_MOUTH;
                break;
            case 7:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_RECTUM;
                break;
            case 8:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_TOE;
                break;
            case 9:
                value = ContextMeasurementValueType.TEMPERATURE_TYPE_TYPANUM;
                break;
            default:
                value = ContextMeasurementValueType.OTHER;
                break;
        }

        return new ContextMeasurement(value, ContextMeasurementType.TEMPERATURE_TYPE);
    }
}
