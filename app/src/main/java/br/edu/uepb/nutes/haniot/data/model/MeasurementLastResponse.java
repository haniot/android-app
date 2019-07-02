package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MeasurementLastResponse {

    @SerializedName("body_temperature")
    @Expose()
    Measurement temperature;

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

    public Measurement getTemperature() {
        return temperature;
    }

    public void setTemperature(Measurement temperature) {
        this.temperature = temperature;
    }

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

    @Override
    public String toString() {
        return "MeasurementLastResponse{" +
                "temperature=" + temperature +
                ", weight=" + weight +
                ", heartRate=" + heartRate +
                ", height=" + height +
                ", bloodGlucose=" + bloodGlucose +
                ", bloodPressure=" + bloodPressure +
                ", waistCircumference=" + waistCircumference +
                '}';
    }
}
