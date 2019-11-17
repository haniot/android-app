package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a HeartRateItemOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class HeartRateItemOB {
    @Id
    private long id;

    private int value;

    private String timestamp;

    public HeartRateItemOB() {}

    public HeartRateItemOB(HeartRateItem h) {
        this.setId(h.getId());
        this.setValue(h.getValue());
        this.setTimestamp(h.getTimestamp());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HeartRateItemOB)) return false;
        HeartRateItemOB weight = (HeartRateItemOB) o;
        return Objects.equals(timestamp, weight.timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "HeartRateItemOB{" +
                "id=" + id +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
