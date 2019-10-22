package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public abstract class ActivityHabitsRecord extends Sync {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    @SerializedName("created_at")
    @Expose()
    private String createdAt;

    @Expose(serialize = false, deserialize = false)
    private String patientId;

    public ActivityHabitsRecord() { }

    public ActivityHabitsRecord(long id, String _id, String createdAt, String patientId) {
        this.id = id;
        this._id = _id;
        this.createdAt = createdAt;
        this.patientId = patientId;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PatientOB
     */
    public static ActivityHabitsRecord jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeActivityHabitsRecord = new TypeToken<ActivityHabitsRecord>() {
        }.getType();
        return gson.fromJson(json, typeActivityHabitsRecord);
    }

    @Override
    public String toString() {
        return "ActivityHabitsRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", patientId='" + patientId + '\'' +
                '}';
    }
}
