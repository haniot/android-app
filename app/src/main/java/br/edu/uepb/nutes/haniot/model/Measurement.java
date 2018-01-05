package br.edu.uepb.nutes.haniot.model;

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

    private String value;
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

    public Measurement(String value, String unit, int typeId) {
        this.value = value;
        this.unit = unit;
        this.typeId = typeId;
    }

    public Measurement(String value, String unit, long registrationDate, int typeId) {
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setTarget(user);
    }

    public ToOne<Device> getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device.setTarget(device);
    }

    public ToOne<Training> getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training.setTarget(training);
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public ToMany<ContextMeasurement> getContextMeasurements() {
        return contextMeasurements;
    }

    public boolean addContext(ContextMeasurement contextMeasurement) {
        return this.getContextMeasurements().add(contextMeasurement);
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public boolean addMeasurement(Measurement measurement) {
        return this.getMeasurements().add(measurement);
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
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (int) (registrationDate ^ (registrationDate >>> 32));
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (device != null ? device.hashCode() : 0);
        result = 31 * result + typeId;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Measurement))
            return false;

        Measurement other = (Measurement) o;

        return other.getRegistrationDate() == this.getRegistrationDate() &&
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
