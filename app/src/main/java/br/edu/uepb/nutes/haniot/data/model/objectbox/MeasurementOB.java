package br.edu.uepb.nutes.haniot.data.model.objectbox;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a MeasurementOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class MeasurementOB implements Parcelable {

    @Id
    private long id;
    @Index
    private String _id; // _id in server remote (UUID)
    private double value;
    private String unit;
    private String type;
    private String timestamp;
    private String userId;
    private String deviceId;
    private int systolic;
    private int diastolic;
    private int pulse;
    private String meal;

    @Transient()
    private BodyFatOB fat; // not persisted in ObjectBox

    @Transient()
    private List<HeartRateItemOB> dataset; // not persisted in ObjectBox

    @Transient()
    private List<BodyFatOB> bodyFat; // not persisted in ObjectBox

    // RELATIONS ObjectBox
    @Backlink(to = "heartRate")
    private ToMany<HeartRateItemOB> datasetDB;

    private ToOne<BodyFatOB> bodyFatDB;

    public MeasurementOB(Measurement m) {
        this.set_id(m.get_id());
        this.setId(m.getId());
        this.setValue(m.getValue());
        this.setUnit(m.getUnit());
        this.setType(m.getType());
        this.setTimestamp(m.getTimestamp());
        this.setUserId(m.getUserId());
        this.setDeviceId(m.getDeviceId());
        this.setSystolic(m.getSystolic());
        this.setDiastolic(m.getDiastolic());
        this.setPulse(m.getPulse());
        this.setMeal(m.getMeal());


        this.setBodyFat(m.getBodyFat());


    }

    protected MeasurementOB(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        value = in.readDouble();
        unit = in.readString();
        type = in.readString();
        timestamp = in.readString();
        userId = in.readString();
        deviceId = in.readString();
        fat = in.readParcelable(BodyFatOB.class.getClassLoader());
        dataset = in.createTypedArrayList(HeartRateItemOB.CREATOR);
        systolic = in.readInt();
        diastolic = in.readInt();
        pulse = in.readInt();
        meal = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(_id);
        dest.writeDouble(value);
        dest.writeString(unit);
        dest.writeString(type);
        dest.writeString(timestamp);
        dest.writeString(userId);
        dest.writeString(deviceId);
        dest.writeParcelable(fat, flags);
        dest.writeTypedList(dataset);
        dest.writeInt(systolic);
        dest.writeInt(diastolic);
        dest.writeInt(pulse);
        dest.writeString(meal);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MeasurementOB> CREATOR = new Creator<MeasurementOB>() {
        @Override
        public MeasurementOB createFromParcel(Parcel in) {
            return new MeasurementOB(in);
        }

        @Override
        public MeasurementOB[] newArray(int size) {
            return new MeasurementOB[size];
        }
    };

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public BodyFatOB getFat() {
        return fat;
    }

    public void setFat(BodyFatOB fat) {
        this.fat = fat;
    }

    public List<HeartRateItemOB> getDataset() {
        return dataset;
    }

    public void setDataset(List<HeartRateItemOB> dataset) {
        this.dataset = dataset;
    }

    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDiastolic() {
        return diastolic;
    }

    public void setDiastolic(int diastolic) {
        this.diastolic = diastolic;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }

    public String getMeal() {
        return meal;
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public ToMany<HeartRateItemOB> getDatasetDB() {
        return datasetDB;
    }

    public void setDatasetDB(List<HeartRateItemOB> datasetDB) {
        this.getDatasetDB().clear();
        this.getDatasetDB().addAll(datasetDB);
    }

    public List<BodyFatOB> getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(List<BodyFatOB> bodyFat) {
        this.bodyFat = bodyFat;
    }

    public ToOne<BodyFatOB> getBodyFatDB() {
        return bodyFatDB;
    }

    public void setBodyFatDB(ToOne<BodyFatOB> bodyFatDB) {
        this.bodyFatDB = bodyFatDB;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, userId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MeasurementOB)) return false;
        MeasurementOB that = (MeasurementOB) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(userId, that.userId);
    }

    @NonNull
    @Override
    public String toString() {
        return "MeasurementOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", type='" + type + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", fat=" + fat +
                ", dataset=" + dataset +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                ", meal=" + meal +
                ", datasetDB=" + datasetDB +
                ", bodyFatDB=" + bodyFatDB +
                '}';
    }
}
