package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB;

/**
 * Represents Object of a MeasurementOB.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class Measurement extends Sync implements Parcelable {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @SerializedName("value")
    @Expose()
    private double value;

    @SerializedName("unit")
    @Expose()
    private String unit;

    @SerializedName("type")
    @Expose()
    private String type;

    @SerializedName("timestamp")
    @Expose()
    private String timestamp;

    @SerializedName("user_id")
    @Expose()
    private String userId;

    @SerializedName("device_id")
    @Expose()
    private String deviceId;

    @SerializedName("fat")
    @Expose()
    private BodyFat fat;

    @SerializedName("dataset")
    @Expose()
    private List<HeartRateItem> dataset;

    @SerializedName("bodyfat")
    @Expose()
    private List<BodyFat> bodyFat;

    @SerializedName("systolic")
    @Expose()
    private int systolic;

    @SerializedName("diastolic")
    @Expose()
    private int diastolic;

    @SerializedName("pulse")
    @Expose()
    private int pulse;

    @SerializedName("meal")
    @Expose()
    private String meal;

    public Measurement() {
    }

    public Measurement(MeasurementOB m) {
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

        if (m.getBodyFat() != null)
            this.setFat(Convert.convertBodyFat(m.getBodyFat().getTarget()));
        if (m.getDataset() != null)
            this.setDataset(Convert.convertListHeartRateL(m.getDataset()));
    }

    protected Measurement(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        value = in.readDouble();
        unit = in.readString();
        type = in.readString();
        timestamp = in.readString();
        userId = in.readString();
        deviceId = in.readString();
        fat = in.readParcelable(BodyFat.class.getClassLoader());
        dataset = in.createTypedArrayList(HeartRateItem.CREATOR);
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

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
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

    public BodyFat getFat() {
        return fat;
    }

    public void setFat(BodyFat fat) {
        this.fat = fat;
    }

    public List<HeartRateItem> getDataset() {
        return dataset;
    }

    public void setDataset(List<HeartRateItem> dataset) {
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

//    public List<HeartRateItem> getDataset() {
//        return datasetDB;
//    }

//    public void setDataset(List<HeartRateItem> datasetDB) {
//        this.getDataset().clear();
//        this.getDataset().addAll(datasetDB);
//    }

    public List<BodyFat> getBodyFat() {
        return bodyFat;
    }

    public void setBodyFat(List<BodyFat> bodyFat) {
        this.bodyFat = bodyFat;
    }

//    public BodyFat getBodyFat() {
//        return bodyFatDB;
//    }
//
//    public void setBodyFat(BodyFat bodyFatDB) {
//        this.bodyFatDB = bodyFatDB;
//    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, userId);
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Measurement)) return false;
        Measurement that = (Measurement) o;
        return Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(userId, that.userId);
    }

    @NonNull
    @Override
    public String toString() {
        return "Measurement{" +
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
//                ", datasetDB=" + datasetDB +
//                ", bodyFatDB=" + bodyFatDB +
                '}';
    }
}
