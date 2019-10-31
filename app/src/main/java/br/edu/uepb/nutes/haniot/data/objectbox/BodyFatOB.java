package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.BodyFat;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

/**
 * Represents Object of a BodyFatOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class BodyFatOB {
    @Id
    private long id;

    private double value;

    private String unit;

    private String timestamp;

    private String type;

    public BodyFatOB() {}

    public BodyFatOB(BodyFat b) {
        this.type = MeasurementType.BODY_FAT;
        this.setId(b.getId());
        this.setValue(b.getValue());
        this.setUnit(b.getUnit());
        this.setTimestamp(b.getTimestamp());
    }
//
//    public BodyFatOB(double value, String unit) {
//        this.type = MeasurementType.BODY_FAT;
//        this.value = value;
//        this.unit = unit;
//    }
//
//    protected BodyFatOB(Parcel in) {
//        id = in.readLong();
//        value = in.readDouble();
//        unit = in.readString();
//        timestamp = in.readString();
//        type = in.readString();
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeLong(id);
//        dest.writeDouble(value);
//        dest.writeString(unit);
//        dest.writeString(timestamp);
//        dest.writeString(type);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<BodyFatOB> CREATOR = new Creator<BodyFatOB>() {
//        @Override
//        public BodyFatOB createFromParcel(Parcel in) {
//            return new BodyFatOB(in);
//        }
//
//        @Override
//        public BodyFatOB[] newArray(int size) {
//            return new BodyFatOB[size];
//        }
//    };

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
        if (!(o instanceof BodyFatOB)) return false;
        BodyFatOB fat = (BodyFatOB) o;
        return Objects.equals(timestamp, fat.timestamp);
    }

    @NonNull
    @Override
    public String toString() {
        return "BodyFatOB{" +
                "id=" + id +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
