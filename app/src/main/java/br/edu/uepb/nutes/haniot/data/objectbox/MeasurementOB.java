package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import io.objectbox.annotation.Backlink;
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
    private String userId;
    private String deviceId;
    private int systolic;
    private int diastolic;
    private int pulse;
    private String meal;

//    @Transient()
//    private BodyFatOB fat; // not persisted in ObjectBox

//    @Transient()
//    private List<HeartRateItemOB> dataset; // not persisted in ObjectBox

//    @Transient()
//    private List<BodyFatOB> bodyFat; // not persisted in ObjectBox

    private ToOne<BodyFatOB> bodyFatDB;

    @Backlink(to = "heartRate")
    private ToMany<HeartRateItemOB> datasetDB;

    public MeasurementOB(Measurement m) {
        super(m.isSync());
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

        this.bodyFatDB.setTarget(Convert.convertBodyFat(m.getFat()));
        this.setDatasetDB(Convert.convertListHeartRate(m.getDataset()));
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

//    public BodyFatOB getFat() {
//        return fat;
//    }
//
//    public void setFat(BodyFatOB fat) {
//        this.fat = fat;
//    }
//
//    public List<HeartRateItemOB> getDataset() {
//        return dataset;
//    }
//
//    public void setDataset(List<HeartRateItemOB> dataset) {
//        this.dataset = dataset;
//    }

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

    public List<HeartRateItemOB> getDatasetDB() {
        return datasetDB;
    }

    public void setDatasetDB(List<HeartRateItemOB> datasetDB) {
        this.getDatasetDB().clear();
        this.getDatasetDB().addAll(datasetDB);
    }

//    public List<BodyFatOB> getBodyFat() {
//        return bodyFat;
//    }
//
//    public void setBodyFat(List<BodyFatOB> bodyFat) {
//        this.bodyFat = bodyFat;
//    }

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
//                ", fat=" + fat +
//                ", dataset=" + dataset +
                ", systolic=" + systolic +
                ", diastolic=" + diastolic +
                ", pulse=" + pulse +
                ", meal=" + meal +
                ", datasetDB=" + datasetDB +
                ", bodyFatDB=" + bodyFatDB +
                '}';
    }
}
