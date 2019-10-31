package br.edu.uepb.nutes.haniot.data.objectbox;

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

    public ActivityHabitsRecordOB() {}

    public ActivityHabitsRecordOB(long id, String _id, String createdAt, String patientId) {
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
