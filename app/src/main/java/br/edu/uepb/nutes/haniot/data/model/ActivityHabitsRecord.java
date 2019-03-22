package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public abstract class ActivityHabitsRecord {

    @Id
    private long idBd;

    @SerializedName("id")
    private String _id;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("created_at")
    private String createdAt;

    public ActivityHabitsRecord() {

    }

    public long getIdBd() {
        return idBd;
    }

    public void setIdBd(long idBd) {
        this.idBd = idBd;
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
        return "ActivityHabitsRecord{" +
                "idBd=" + idBd +
                ", id='" + _id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
