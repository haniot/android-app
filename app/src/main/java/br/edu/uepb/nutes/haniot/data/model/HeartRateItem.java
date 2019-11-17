package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.objectbox.HeartRateItemOB;

/**
 * Represents Object of a HeartRateItemOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class HeartRateItem implements Parcelable {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("value")
    @Expose()
    private int value;

    @SerializedName("timestamp")
    @Expose()
    private String timestamp;

//    @Expose(serialize = false, deserialize = false)
//    private Measurement heartRate;

    public HeartRateItem() {
    }

    public HeartRateItem(int value, String timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public HeartRateItem(HeartRateItemOB h) {
        this.setId(h.getId());
        this.setValue(h.getValue());
        this.setTimestamp(h.getTimestamp());

//        if (h.getHeartRate() != null && h.getHeartRate().getTarget() != null)
//            this.setHeartRate(Convert.convertMeasurement(h.getHeartRate().getTarget()));
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

//    public Measurement getHeartRate() {
//        return heartRate;
//    }
//
//    public void setHeartRate(Measurement heartRate) {
//        this.heartRate = heartRate;
//    }

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
                "id=" + id +
                ", value=" + value +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
