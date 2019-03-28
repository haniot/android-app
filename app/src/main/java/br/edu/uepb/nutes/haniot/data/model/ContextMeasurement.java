package br.edu.uepb.nutes.haniot.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a ContextMeasurement.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class ContextMeasurement {
    @Id
    private long id;

    /**
     * {@link ContextMeasurementValueType()}
     */
    private int valueId;

    /**
     * {@link ContextMeasurementType()}
     */
    private int typeId;

    /**
     * RELATIONS
     */
    private ToOne<Measurement> measurement;

    public ContextMeasurement() {
    }

    public ContextMeasurement(int value, int type) {
        this.valueId = value;
        this.typeId = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValueId() {
        return valueId;
    }

    public void setValueId(int valueId) {
        this.valueId = valueId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public ToOne<Measurement> getMeasurement() {
        return measurement;
    }

    public void setMeasurement(ToOne<Measurement> measurement) {
        this.measurement = measurement;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + valueId;
        result = 31 * result + typeId;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ContextMeasurement))
            return false;

        ContextMeasurement other = (ContextMeasurement) o;

        return this.typeId == other.typeId && this.valueId == other.valueId;
    }

    @Override
    public String toString() {
        return "ContextMeasurement{" +
                "id=" + id +
                ", valueId=" + valueId +
                ", typeId=" + typeId +
                ", measurement=" + measurement +
                '}';
    }
}
