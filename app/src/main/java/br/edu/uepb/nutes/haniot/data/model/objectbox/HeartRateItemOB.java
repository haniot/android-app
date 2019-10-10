package br.edu.uepb.nutes.haniot.data.model.objectbox;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a HeartRateItemOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class HeartRateItemOB implements Parcelable {
    @Id
    private long id;

    private int value;

    private String timestamp;

    private ToOne<MeasurementOB> heartRate;

    public HeartRateItemOB() {
    }

    public HeartRateItemOB(int value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    protected HeartRateItemOB(Parcel in) {
        value = in.readInt();
        timestamp = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(value);
        dest.writeString(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HeartRateItemOB> CREATOR = new Creator<HeartRateItemOB>() {
        @Override
        public HeartRateItemOB createFromParcel(Parcel in) {
            return new HeartRateItemOB(in);
        }

        @Override
        public HeartRateItemOB[] newArray(int size) {
            return new HeartRateItemOB[size];
        }
    };

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

    public ToOne<MeasurementOB> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(ToOne<MeasurementOB> heartRate) {
        this.heartRate = heartRate;
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
                super.toString() + '\'' +
                "value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
