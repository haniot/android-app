package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents Object of a MeasurementOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class MeasurementOB extends SyncOB {

    @Id
    private long id;
    @Index
    private String _id; // _id in server remote (UUID)
    private double value;
    private String unit;
    private String type;
    private String timestamp;
    private String user_id;
    private long userId;
    private String deviceId;
    private int systolic;
    private int diastolic;
    private int pulse;
    private String meal;

    private ToOne<BodyFatOB> fat;

    private ToMany<BodyFatOB> bodyFat;

    //    @Backlink(to = "heartRate")
    private ToMany<HeartRateItemOB> dataset;

    public MeasurementOB() {
        super();
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

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ToMany<BodyFatOB> getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(List<BodyFatOB> bodyFat) {
        this.bodyFat.clear();
        this.bodyFat.addAll(bodyFat);
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

    public List<HeartRateItemOB> getDataset() {
        return dataset;
    }

    public void setDataset(List<HeartRateItemOB> datasetDB) {
        this.getDataset().clear();
        this.getDataset().addAll(datasetDB);
    }

    public ToOne<BodyFatOB> getFat() {
        return fat;
    }

    public void setFat(ToOne<BodyFatOB> fat) {
        this.fat = fat;
    }

    public void setFatModel(BodyFatOB fat) {
        this.fat.setTarget(fat);
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
                ", user_id='" + user_id + '\'' +
                ", userId=" + userId +
                ", deviceId='" + deviceId + '\'' +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                ", meal='" + meal + '\'' +
                ", fat=" + fat.getTarget() +
                ", dataset=" + dataset.toString() +
                ", Sync='" + isSync() + "\'" +
                '}';
    }
}
