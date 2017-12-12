package br.edu.uepb.nutes.haniot.model;

import java.io.Serializable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents measurement of the heart rate.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class MeasurementHeartRate implements Serializable {
    @Id
    private long id;

    @Index
    private String userId; // _id provided by remote server

    private long durationTime; // Duration of data collection in milliseconds
    private int fcMaximum;
    private int fcMinimum;
    private int fcAverage;
    private String bpms; // bpm;time,bpm;time,bpm;time...
    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

    public MeasurementHeartRate() {
    }

    public MeasurementHeartRate(int fcMaximum, int fcMinimum, int fcAverage, long durationTime, String bpms) {
        this.fcMaximum = fcMaximum;
        this.fcMinimum = fcMinimum;
        this.fcAverage = fcAverage;
        this.durationTime = durationTime;
        this.bpms = bpms;
    }

    public MeasurementHeartRate(long id, long durationTime, int fcMaximum, int fcMinimum, int fcAverage, String bpms, long registrationTime, String deviceAddress, String userId) {
        this.id = id;
        this.durationTime = durationTime;
        this.fcMaximum = fcMaximum;
        this.fcMinimum = fcMinimum;
        this.fcAverage = fcAverage;
        this.bpms = bpms;
        this.registrationTime = registrationTime;
        this.deviceAddress = deviceAddress;
        this.userId = userId;
        this.hasSent = 0;
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

    public long getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(long durationTime) {
        this.durationTime = durationTime;
    }

    public int getFcMaximum() {
        return fcMaximum;
    }

    public void setFcMaximum(int fcMaximum) {
        this.fcMaximum = fcMaximum;
    }

    public int getFcMinimum() {
        return fcMinimum;
    }

    public void setFcMinimum(int fcMinimum) {
        this.fcMinimum = fcMinimum;
    }

    public int getFcAverage() {
        return fcAverage;
    }

    public void setFcAverage(int fcAverage) {
        this.fcAverage = fcAverage;
    }

    public String getBpms() {
        return bpms;
    }

    public void setBpms(String bpms) {
        this.bpms = bpms;
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
        int result = super.hashCode();
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (durationTime ^ (durationTime >>> 32));
        result = 31 * result + (int) (registrationTime ^ (registrationTime >>> 32));
        result = 31 * result + (deviceAddress != null ? deviceAddress.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MeasurementHeartRate other = (MeasurementHeartRate) o;

        if (durationTime != other.durationTime || registrationTime != other.registrationTime)
            return false;

        return bpms.equals(other.bpms);
    }

    @Override
    public String toString() {
        return "MeasurementHeartRate{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", durationTime=" + durationTime +
                ", fcMaximum=" + fcMaximum +
                ", fcMinimum=" + fcMinimum +
                ", fcAverage=" + fcAverage +
                ", bpms='" + bpms + '\'' +
                ", registrationTime=" + registrationTime +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", hasSent=" + hasSent +
                '}';
    }
}
