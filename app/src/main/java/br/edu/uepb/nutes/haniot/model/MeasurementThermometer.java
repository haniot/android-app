package br.edu.uepb.nutes.haniot.model;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents measurement of the Thermometer device.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class MeasurementThermometer implements Serializable {
    @Id
    private long id;

    @Index
    private String userId; // _id provided by remote server

    private float value;
    private String unit;
    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

    public MeasurementThermometer() {
    }

    public MeasurementThermometer(float value, String unit) {
        this.value = value;
        this.unit = unit;
    }

    public MeasurementThermometer(float value, String unit, long registrationTime, String deviceAddress, String userId, int hasSent) {
        this.value = value;
        this.unit = unit;
        this.registrationTime = registrationTime;
        this.deviceAddress = deviceAddress;
        this.userId = userId;
        this.hasSent = hasSent;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        result = 31 * result + (value != +0.0f ? Float.floatToIntBits(value) : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (int) (registrationTime ^ (registrationTime >>> 32));
        result = 31 * result + (deviceAddress != null ? deviceAddress.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + hasSent;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeasurementThermometer))
            return false;

        MeasurementThermometer other = (MeasurementThermometer) o;

        if (value != other.value || registrationTime != other.registrationTime)
            return false;

        return unit.equals(other.unit) && userId.equals(other.userId);
    }

    @Override
    public String toString() {
        return "MeasurementThermometer{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", registrationTime=" + registrationTime +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", hasSent=" + hasSent +
                '}';
    }
}
