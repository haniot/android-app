package br.edu.uepb.nutes.haniot.model;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Created by lucas on 06/10/17.
 */
@Entity
public class MeasurementBloodPressure implements Serializable {
    @Id
    private long id;

    @Index
    private String userId; // _id provided by remote server

    private int systolic;
    private String unitSystolic;
    private int diastolic;
    private String unitDiastolic;

    // PAM = ((2PAD) + PAS)/3 - Pressão Arterial Média
    private int frequency;
    private String unitFrequency;
    private int heartFate;
    private String unitHeartFate;

    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

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

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public String getUnitSystolic() {
        return unitSystolic;
    }

    public void setUnitSystolic(String unitSystolic) {
        this.unitSystolic = unitSystolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public String getUnitDiastolic() {
        return unitDiastolic;
    }

    public void setUnitDiastolic(String unitDiastolic) {
        this.unitDiastolic = unitDiastolic;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getUnitFrequency() {
        return unitFrequency;
    }

    public void setUnitFrequency(String unitFrequency) {
        this.unitFrequency = unitFrequency;
    }

    public int getHeartFate() {
        return heartFate;
    }

    public void setHeartFate(int heartFate) {
        this.heartFate = heartFate;
    }

    public String getUnitHeartFate() {
        return unitHeartFate;
    }

    public void setUnitHeartFate(String unitHeartFate) {
        this.unitHeartFate = unitHeartFate;
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
        result = 31 * result + systolic;
        result = 31 * result + diastolic;
        result = 31 * result + frequency;
        result = 31 * result + (int) (registrationTime ^ (registrationTime >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeasurementBloodPressure))
            return false;

        MeasurementBloodPressure other = (MeasurementBloodPressure) o;

        if (systolic != other.systolic) return false;
        if (diastolic != other.diastolic) return false;
        if (frequency != other.frequency) return false;
        if (heartFate != other.heartFate) return false;
        if (registrationTime != other.registrationTime) return false;

        return true;
    }

}
