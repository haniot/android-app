package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents Object of a BodyFat.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class BodyFat implements Parcelable {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("value")
    @Expose()
    private double value;

    @SerializedName("unit")
    @Expose()
    private String unit;

    @SerializedName("timestamp")
    @Expose()
    private String timestamp;

    @SerializedName("type")
    @Expose()
    private String type;

    public BodyFat() {
        this.type = MeasurementType.BODY_FAT;
    }

    public BodyFat(double value, String unit) {
        this.type = MeasurementType.BODY_FAT;
        this.value = value;
        this.unit = unit;
    }

    protected BodyFat(Parcel in) {
        id = in.readLong();
        value = in.readDouble();
        unit = in.readString();
        timestamp = in.readString();
        type = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeDouble(value);
        dest.writeString(unit);
        dest.writeString(timestamp);
        dest.writeString(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BodyFat> CREATOR = new Creator<BodyFat>() {
        @Override
        public BodyFat createFromParcel(Parcel in) {
            return new BodyFat(in);
        }

        @Override
        public BodyFat[] newArray(int size) {
            return new BodyFat[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BodyFat)) return false;
        BodyFat fat = (BodyFat) o;
        return Objects.equals(timestamp, fat.timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "BodyFat{" +
                "id=" + id +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
