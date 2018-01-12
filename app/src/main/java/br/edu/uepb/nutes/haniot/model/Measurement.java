package br.edu.uepb.nutes.haniot.model;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a Measurement.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Measurement {
    @Id
    private long id;

    private double value;
    private String unit;
    private long registrationDate;

    /**
     * RELATIONS
     */
    private ToOne<User> user;
    private ToOne<Device> device;
    private ToOne<Training> training;
    @Backlink(to = "measurement")
    private ToMany<ContextMeasurement> contextMeasurements;
    public ToMany<Measurement> measurements;

    /**
     * {@link MeasurementType()}
     */
    private int typeId;

    private int hasSent; // Measurement sent?

    public Measurement() {
    }

    public Measurement(double value, String unit, int typeId) {
        this.value = value;
        this.unit = unit;
        this.typeId = typeId;
    }

    public Measurement(double value, String unit, long registrationDate, int typeId) {
        this.value = value;
        this.unit = unit;
        this.registrationDate = registrationDate;
        this.typeId = typeId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getValue() {
        return value;
    }

    public Measurement setValue(double value) {
        this.value = value;
        return this;
    }

    public String getUnit() {
        return unit;
    }

    public Measurement setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public Measurement setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public ToOne<Device> getDevice() {
        return device;
    }

    public ToOne<Training> getTraining() {
        return training;
    }

    public void setUser(User user) {
        this.user.setTarget(user);
    }

    public void setDevice(Device device) {
        this.device.setTarget(device);
    }

    public Measurement setTraining(Training training) {
        this.training.setTarget(training);
        return this;
    }

    public Measurement addContext(ContextMeasurement contextMeasurement) {
        this.getContextMeasurements().add(contextMeasurement);
        return this;
    }

    public Measurement addContext(List<ContextMeasurement> contextsMeasurements) {
        this.getContextMeasurements().addAll(contextsMeasurements);
        return this;
    }

    public boolean addMeasurement(Measurement measurement) {
        return this.getMeasurements().add(measurement);
    }

    public void addMeasurement(Measurement... measurement) {
        for (Measurement m : measurement)
            this.getMeasurements().add(m);
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
    }

    public int getTypeId() {
        return typeId;
    }

    public Measurement setTypeId(int typeId) {
        this.typeId = typeId;
        return this;
    }

    public ToMany<ContextMeasurement> getContextMeasurements() {
        return contextMeasurements;
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public int getHasSent() {
        return hasSent;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        temp = Double.doubleToLongBits(value);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (int) (registrationDate ^ (registrationDate >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + typeId;
        result = 31 * result + hasSent;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Measurement))
            return false;

        Measurement other = (Measurement) o;

        return other.value == this.value &&
                other.getRegistrationDate() == this.getRegistrationDate() &&
                other.user.equals(this.user) &&
                other.device.equals(this.device);
    }

    @Override
    public String toString() {
        return "Measurement{" +
                "id=" + id +
                ", value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                ", registrationDate=" + registrationDate +
                ", user=" + user +
                ", device=" + device +
                ", training=" + training +
                ", contextMeasurements=" + contextMeasurements +
                ", typeId=" + typeId +
                ", hasSent=" + hasSent +
                '}';
    }
}
