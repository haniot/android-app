package br.edu.uepb.nutes.haniot.parse;

import org.json.JSONException;
import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;

/**
 * Parse JSON to Measurement Object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class JsonToMeasurement {

    public static Measurement parse(JSONObject json, int measurementType) throws JSONException {
        switch (measurementType) {
            case MeasurementType.TEMPERATURE:
                return jsonToMeasurementTemperature(json);
            case MeasurementType.BLOOD_GLUCOSE:
            default:
                break;
        }

        return null;
    }

    /**
     * Convert json to Temperature Measurement.
     *
     * @param json
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement jsonToMeasurementTemperature(JSONObject json) throws JSONException {
        Measurement measurement = new Measurement(
                json.getString("temperature"),
                json.getString("temperatureUnit"),
                json.getLong("timestamp"),
                MeasurementType.TEMPERATURE);

        return measurement;
    }

    /**
     * Convert json to Body Mass Measurement.
     *
     * @param json
     * @return Measurement
     */
    public Measurement jsonToMeasurementBodyMass(JSONObject json) throws JSONException {
        Measurement measurement = new Measurement(
                json.getString("bodyMass"),
                json.getString("bodyMassUnit"),
                json.getLong("timestamp"),
                MeasurementType.BODY_MASS);

        return measurement;
    }

    /**
     * Convert json to Body Fat Measurement.
     *
     * @param json
     * @return Measurement
     * @throws JSONException
     */
    public Measurement jsonToMeasurementBodyFat(JSONObject json) throws JSONException {
        Measurement measurement = measurement = new Measurement(
                json.getString("bodyFat"),
                json.getString("bodyFatUnit"),
                json.getLong("timestamp"),
                MeasurementType.BODY_FAT);

        return measurement;
    }

    /**
     * Convert json to HeartRate Measurement.
     *
     * @param json
     * @return Measurement
     * @throws JSONException
     */
    public Measurement jsonToMeasurementHeartRate(JSONObject json) throws JSONException {
        Measurement measurement = new Measurement(
                json.getString("heartRate"),
                json.getString("heartRateUnit"),
                json.getLong("timestamp"),
                MeasurementType.HEART_RATE);

        return measurement;
    }
}
