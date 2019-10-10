package br.edu.uepb.nutes.haniot.data.model.objectbox;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@BaseEntity
public abstract class ActivityHabitsRecordOB {
    @Id
    private long id;

    @Index
    private String _id;

    private String createdAt;

    private String patientId;

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

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        return gson.toJson(this);
//    }
//
//    /**
//     * Convert json to Object.
//     *
//     * @param json String
//     * @return PatientOB
//     */
//    public static ActivityHabitsRecordOB jsonDeserialize(String json) {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        Type typeActivityHabitsRecord = new TypeToken<ActivityHabitsRecordOB>() {
//        }.getType();
//        return gson.fromJson(json, typeActivityHabitsRecord);
//    }

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
