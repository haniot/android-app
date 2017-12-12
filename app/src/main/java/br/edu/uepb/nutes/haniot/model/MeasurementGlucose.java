package br.edu.uepb.nutes.haniot.model;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents measurement of the Glucose device.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class MeasurementGlucose implements Serializable {
    @Id
    private long id;

    @Index
    private String userId; // _id provided by remote server

    private float value;
    private String unit;
    private int sequenceNumber;
    private String meal; // Preprandial (before meal), 	Postprandial (after meal), Fasting, Casual (snacks, drinks, etc.), Bedtime or Other
    private String type; // Capillary Whole blood, Capillary Plasma, Venous Whole blood, Venous Plasma, Arterial Whole blood...
    private String statusAnnunciation;
    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

    public MeasurementGlucose() {
    }

    public MeasurementGlucose(String userId, float value, String unit, long registrationTime, String deviceAddress) {
        this.userId = userId;
        this.value = value;
        this.unit = unit;
        this.registrationTime = registrationTime;
        this.deviceAddress = deviceAddress;
    }

    public MeasurementGlucose(float value, String unit, int sequenceNumber, String meal, String type, String statusAnnunciation) {
        this.value = value;
        this.unit = unit;
        this.sequenceNumber = sequenceNumber;
        this.meal = meal;
        this.type = type;
        this.statusAnnunciation = statusAnnunciation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatusAnnunciation() {
        return statusAnnunciation;
    }

    public void setStatusAnnunciation(String statusAnnunciation) {
        this.statusAnnunciation = statusAnnunciation;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(long registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getHasSent() {
        return hasSent;
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (int) (registrationTime ^ (registrationTime >>> 32));
        result = 31 * result + (deviceAddress != null ? deviceAddress.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeasurementBodyCompositionMonitor))
            return false;

        MeasurementGlucose other = (MeasurementGlucose) o;

        if (Float.compare(other.value, value) != 0) return false;
        if (registrationTime != other.registrationTime) return false;
        if (unit != null ? !unit.equals(other.unit) : other.unit != null) return false;
        if (meal != null ? !meal.equals(other.meal) : other.meal != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "MeasurementGlucose{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", sequenceNumber=" + sequenceNumber +
                ", meal='" + meal + '\'' +
                ", type='" + type + '\'' +
                ", statusAnnunciation='" + statusAnnunciation + '\'' +
                ", registrationTime=" + registrationTime +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", hasSent=" + hasSent +
                '}';
    }
}
