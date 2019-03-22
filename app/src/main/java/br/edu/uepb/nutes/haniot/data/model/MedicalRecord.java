package br.edu.uepb.nutes.haniot.data.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class MedicalRecord {
    @Id
    private long idBd;

    @SerializedName("id")
    private String _id;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("created_at")
    private String createdAt;

    @Expose(serialize = false, deserialize = false)
    private ToMany<ChronicDisease> chronicDiseaseDB;

    @SerializedName("chronic_diseases")
    private List<ChronicDisease> chronicDiseases;

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

    public ToMany<ChronicDisease> getChronicDiseaseDB() {
        return chronicDiseaseDB;
    }

    public void setChronicDiseaseDB(List<ChronicDisease> chronicDiseaseDB) {
        this.chronicDiseaseDB.addAll(chronicDiseaseDB);
    }

    public List<ChronicDisease> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(List<ChronicDisease> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalRecord that = (MedicalRecord) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(patientId, that.patientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, patientId);
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "idBd=" + idBd +
                ", id=" + _id +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", chronicDisease=" + chronicDiseases +
                '}';
    }
}
