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

    @Index
    private String _id; // _id in server remote (UUID)

    private double value;
    private String unit;
    private long registrationDate;

    /**
     * RELATIONS
     */
    private ToOne<User> user;
    private ToOne<Patient> children;
    private ToOne<Device> device;
    private ToOne<Training> training;
    @Backlink(to = "measurement")
    private ToMany<ContextMeasurement> contextMeasurements;
    private ToMany<Measurement> measurements;

    /**
     * {@link MeasurementType()}
     */
    private int typeId;

    private int hasSent; // Measurement sent?

    public Measurement() {
    }

    public Measurement(long id) {
        this.id = id;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
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

    public ToOne<Patient> getChildren() {
        return children;
    }

    public void setChildren(ToOne<Patient> children) {
        this.children = children;
    }

    public User getUser() {
        return user.getTarget();
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

    public void setTraining(Training training) {
        this.training.setTarget(training);
    }

    public boolean addContext(ContextMeasurement contextMeasurement) {
        return this.contextMeasurements.add(contextMeasurement);
    }

    public void addContext(ContextMeasurement... contextMeasurements) {
        for (ContextMeasurement c : contextMeasurements)
            this.contextMeasurements.add(c);
    }

    public boolean addContext(List<ContextMeasurement> contextsMeasurements) {
        return this.contextMeasurements.addAll(contextsMeasurements);
    }

    public boolean addMeasurement(Measurement measurement) {
        return this.getMeasurements().add(measurement);
    }

    public void addMeasurement(Measurement... measurements) {
        for (Measurement m : measurements)
            this.getMeasurements().add(m);
    }

    public boolean addMeasurement(List<Measurement> measurement) {
        return this.getMeasurements().addAll(measurement);
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
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
