package br.edu.uepb.nutes.haniot.model;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;

/**
 * Represents measurement of the Scale device.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class MeasurementScale implements Serializable {
    @Id
    private long id;

    @Index
    private String userId; // _id provided by remote server

    private float weight; // Peso
    private String unit; // unit weight kg or lb
    private float bmi; // IMC
    private float bmr; // Taxa Métabolica Basal - Medida de energia ou calorias que você consome em repouso
    private float boneFat; // Massa Óssea
    private float bodyFat; // Gordura corporal
    private float bodyWater; // água corporal
    private float visceralFat; // Gordura visceral
    private float muscle; // Taxa muscular
    private float protein; // Proteína
    private int fitnesAge; // Idade corporal
    private int resistence;
    private int userIdDevice; // Saved and returned by scale

    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

    @Transient
    private boolean isFinalized;

    public MeasurementScale() {
    }

    public MeasurementScale(float weight, String unit) {
        this.weight = weight;
        this.unit = unit;
        this.hasSent = 0;
    }

    public MeasurementScale(String userId, float weight, String unit, String deviceAddress) {
        this.userId = userId;
        this.weight = weight;
        this.unit = unit;
        this.deviceAddress = deviceAddress;
    }

    public MeasurementScale(String userId, float weight, float bodyFat, String unit, String deviceAddress) {
        this.userId = userId;
        this.weight = weight;
        this.bodyFat = bodyFat;
        this.unit = unit;
        this.deviceAddress = deviceAddress;
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }

    public float getBmr() {
        return bmr;
    }

    public void setBmr(float bmr) {
        this.bmr = bmr;
    }

    public float getBoneFat() {
        return boneFat;
    }

    public void setBoneFat(float boneFat) {
        this.boneFat = boneFat;
    }

    public float getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(float bodyFat) {
        this.bodyFat = bodyFat;
    }

    public float getBodyWater() {
        return bodyWater;
    }

    public void setBodyWater(float bodyWater) {
        this.bodyWater = bodyWater;
    }

    public float getVisceralFat() {
        return visceralFat;
    }

    public void setVisceralFat(float visceralFat) {
        this.visceralFat = visceralFat;
    }

    public float getMuscle() {
        return muscle;
    }

    public void setMuscle(float muscle) {
        this.muscle = muscle;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public int getFitnesAge() {
        return fitnesAge;
    }

    public void setFitnesAge(int fitnesAge) {
        this.fitnesAge = fitnesAge;
    }

    public int getResistence() {
        return resistence;
    }

    public void setResistence(int resistence) {
        this.resistence = resistence;
    }

    public int getUserIdDevice() {
        return userIdDevice;
    }

    public void setUserIdDevice(int userIdDevice) {
        this.userIdDevice = userIdDevice;
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

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (weight != +0.0f ? Float.floatToIntBits(weight) : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (int) (registrationTime ^ (registrationTime >>> 32));
        result = 31 * result + (deviceAddress != null ? deviceAddress.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof MeasurementScale)) return false;

        MeasurementScale other = (MeasurementScale) o;

        if (Float.compare(other.weight, weight) != 0) return false;
        if (Float.compare(other.bodyFat, bodyFat) != 0) return false;
        if (resistence != other.resistence) return false;
        if (registrationTime != other.registrationTime) return false;
        return unit != null ? unit.equals(other.unit) : other.unit == null;
    }

    @Override
    public String toString() {
        return "MeasurementScale{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", weight=" + weight +
                ", unit='" + unit + '\'' +
                ", bmi=" + bmi +
                ", bmr=" + bmr +
                ", boneFat=" + boneFat +
                ", bodyFat=" + bodyFat +
                ", bodyWater=" + bodyWater +
                ", visceralFat=" + visceralFat +
                ", muscle=" + muscle +
                ", protein=" + protein +
                ", fitnesAge=" + fitnesAge +
                ", resistence=" + resistence +
                ", userIdDevice=" + userIdDevice +
                ", registrationTime=" + registrationTime +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", hasSent=" + hasSent +
                ", isFinalized=" + isFinalized +
                '}';
    }
}
