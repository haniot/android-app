package br.edu.uepb.nutes.haniot.data.model.objectbox;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a HeartRateItem.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class HeartRateItem implements Parcelable {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("value")
    @Expose()
    private int value;

    @SerializedName("timestamp")
    @Expose()
    private String timestamp;

    @Expose(serialize = false, deserialize = false)
    private ToOne<Measurement> heartRate;

    public HeartRateItem() {
    }

    public HeartRateItem(int value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    protected HeartRateItem(Parcel in) {
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

    public static final Creator<HeartRateItem> CREATOR = new Creator<HeartRateItem>() {
        @Override
        public HeartRateItem createFromParcel(Parcel in) {
            return new HeartRateItem(in);
        }

        @Override
        public HeartRateItem[] newArray(int size) {
            return new HeartRateItem[size];
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

    public ToOne<Measurement> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(ToOne<Measurement> heartRate) {
        this.heartRate = heartRate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HeartRateItem)) return false;
        HeartRateItem weight = (HeartRateItem) o;
        return Objects.equals(timestamp, weight.timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "HeartRateItem{" +
                super.toString() + '\'' +
                "value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
