package br.edu.uepb.nutes.haniot.parse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;

/**
 * Parse JSON to Measurement Object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class JsonToMeasurementParser {

    /**
     * Parse for all supported measurements.
     *
     * @param data
     * @return List<Measurement>
     * @throws JSONException
     */
    public static List<Measurement> parse(String... data) throws JSONException {
        List<Measurement> result = new ArrayList<>();
        Measurement item = null;
        JSONObject o = null;

        for (String json : data) {
            o = new JSONObject(json);

            if (o.has("temperature")) {
                item = temperature(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("glucose")) {
                item = boodGlucose(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("heartRate")) {
                item = heartRate(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("systolic")) {
                item = systolic(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("diastolic")) {
                item = diastolic(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("bodyMass")) {
                item = bodyMass(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("bodyFat")) {
                item = bodyFat(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("bmi")) {
                item = bmi(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("rmr")) {
                item = rmr(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("bmr")) {
                item = bmr(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("muscleMass")) {
                item = muscleMass(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("visceralFat")) {
                item = visceralFat(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("bodyAge")) {
                item = bodyAge(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("steps")) {
                item = steps(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("distance")) {
                item = distance(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }

            if (o.has("calories")) {
                item = calories(json);
                if (!result.contains(item)) result.add(item);
                continue;
            }
        }

        return result;
    }

    /**
     * Convert json to Temperature Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement temperature(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = new Measurement(
                o.getDouble("temperature"),
                o.getString("temperatureUnit"),
                o.getLong("timestamp"),
                MeasurementType.TEMPERATURE);

        return measurement;
    }

    /**
     * Convert json to Blood Glucose Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement boodGlucose(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = new Measurement(
                o.getDouble("glucose"),
                o.getString("glucoseUnit"),
                o.getLong("timestamp"),
                MeasurementType.BLOOD_GLUCOSE);

        return measurement;
    }

    /**
     * Convert json to Heart Rate Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement heartRate(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = new Measurement(
                o.getDouble("heartRate"),
                o.getString("heartRateUnit"),
                o.getLong("timestamp"),
                MeasurementType.HEART_RATE);

        return measurement;
    }

    /**
     * Convert json to Systolic Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement systolic(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("systolic"),
                o.getString("systolicUnit"),
                o.getLong("timestamp"),
                MeasurementType.BLOOD_PRESSURE_SYSTOLIC);

        return measurement;
    }

    /**
     * Convert json to Diastolic Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement diastolic(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("diastolic"),
                o.getString("diastolicUnit"),
                o.getLong("timestamp"),
                MeasurementType.BLOOD_PRESSURE_DIASTOLIC);

        return measurement;
    }

    /**
     * Convert json to Body Mass Measurement.
     *
     * @param json String
     * @return Measurement
     */
    public static Measurement bodyMass(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = new Measurement(
                o.getDouble("bodyMass"),
                o.getString("bodyMassUnit"),
                o.getLong("timestamp"),
                MeasurementType.BODY_MASS);

        return measurement;
    }

    /**
     * Convert json to Body Fat Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement bodyFat(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("bodyFat"),
                o.getString("bodyFatUnit"),
                o.getLong("timestamp"),
                MeasurementType.BODY_FAT);

        return measurement;
    }

    /**
     * Convert json to BMI (Body Mass Index) Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement bmi(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("bmi"),
                o.getString("bmiUnit"),
                o.getLong("timestamp"),
                MeasurementType.BMI);

        return measurement;
    }

    /**
     * Convert json to RMR (Resting Metabolic Rate) Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement rmr(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("rmr"),
                o.getString("rmrUnit"),
                o.getLong("timestamp"),
                MeasurementType.RMR);

        return measurement;
    }

    /**
     * Convert json to BMR (Basal Metabolic Rate) Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement bmr(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("bmr"),
                o.getString("bmrUnit"),
                o.getLong("timestamp"),
                MeasurementType.BMR);

        return measurement;
    }

    /**
     * Convert json to Muscle Mass Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement muscleMass(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("muscleMass"),
                o.getString("muscleMassUnit"),
                o.getLong("timestamp"),
                MeasurementType.MUSCLE_MASS);

        return measurement;
    }

    /**
     * Convert json to Visceral Fat Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement visceralFat(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("visceralFat"),
                o.getString("visceralFatUnit"),
                o.getLong("timestamp"),
                MeasurementType.VISCERAL_FAT);

        return measurement;
    }

    /**
     * Convert json to Body Age Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement bodyAge(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("bodyAge"),
                o.getString("bodyAgeUnit"),
                o.getLong("timestamp"),
                MeasurementType.BODY_AGE);

        return measurement;
    }

    /**
     * Convert json to Steps Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement steps(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("steps"),
                o.getString("stepsUnit"),
                o.getLong("timestamp"),
                MeasurementType.STEPS);

        return measurement;
    }

    /**
     * Convert json to Distance Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement distance(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("distance"),
                o.getString("distanceUnit"),
                o.getLong("timestamp"),
                MeasurementType.DISTANCE);

        return measurement;
    }

    /**
     * Convert json to Calories Burned Measurement.
     *
     * @param json String
     * @return Measurement
     * @throws JSONException
     */
    public static Measurement calories(String json) throws JSONException {
        JSONObject o = new JSONObject(json);

        Measurement measurement = measurement = new Measurement(
                o.getDouble("calories"),
                o.getString("caloriesUnit"),
                o.getLong("timestamp"),
                MeasurementType.CALORIES_BURNED);

        return measurement;
    }
}
