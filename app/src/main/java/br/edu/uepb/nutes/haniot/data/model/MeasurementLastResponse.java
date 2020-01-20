package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class MeasurementLastResponse {

    @SerializedName("weight")
    @Expose()
    Measurement weight;

    @SerializedName("hearte_rate")
    @Expose()
    Measurement heartRate;

    @SerializedName("height")
    @Expose()
    Measurement height;

    @SerializedName("blood_glucose")
    @Expose()
    Measurement bloodGlucose;

    @SerializedName("blood_pressure")
    @Expose()
    Measurement bloodPressure;

    @SerializedName("waist_circumference")
    @Expose()
    Measurement waistCircumference;

    public Measurement getWeight() {
        return weight;
    }

    public void setWeight(Measurement weight) {
        this.weight = weight;
    }

    public Measurement getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Measurement heartRate) {
        this.heartRate = heartRate;
    }

    public Measurement getHeight() {
        return height;
    }

    public void setHeight(Measurement height) {
        this.height = height;
    }

    public Measurement getBloodGlucose() {
        return bloodGlucose;
    }

    public void setBloodGlucose(Measurement bloodGlucose) {
        this.bloodGlucose = bloodGlucose;
    }

    public Measurement getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(Measurement bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Measurement getWaistCircumference() {
        return waistCircumference;
    }

    public void setWaistCircumference(Measurement waistCircumference) {
        this.waistCircumference = waistCircumference;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        return a;
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return Patient
     */
    public static Patient jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeLastMeasurement = new TypeToken<MeasurementLastResponse>() {
        }.getType();
        return gson.fromJson(json, typeLastMeasurement);
    }

    @Override
    public String toString() {
        return "MeasurementLastResponse{" +
                ", weight=" + weight +
                ", heartRate=" + heartRate +
                ", height=" + height +
                ", bloodGlucose=" + bloodGlucose +
                ", bloodPressure=" + bloodPressure +
                ", waistCircumference=" + waistCircumference +
                '}';
    }
}
