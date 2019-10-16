package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

public class OralHealthRecordOB {

    @Id
    private long id;

    private String _id;

    private String patientId;

    private String createdAt;

    private String teethBrushingFreq;

    @Transient // not persisted
    private List<ToothLesionOB> toothLesions;

    @Backlink(to = "oralHealthRecord")
    private ToMany<ToothLesionOB> toothLesionsDB;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTeethBrushingFreq() {
        return teethBrushingFreq;
    }

    public void setTeethBrushingFreq(String teethBrushingFreq) {
        this.teethBrushingFreq = teethBrushingFreq;
    }

    public List<ToothLesionOB> getToothLesions() {
        return toothLesions;
    }

    public void setToothLesions(List<ToothLesionOB> toothLesions) {
        this.toothLesions = toothLesions;
    }

    public ToMany<ToothLesionOB> getToothLesionsDB() {
        return toothLesionsDB;
    }

    public void setToothLesionsDB(ToMany<ToothLesionOB> toothLesionsDB) {
        this.toothLesionsDB = toothLesionsDB;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public String toString() {
        return "OralHealthRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", teethBrushingFreq='" + teethBrushingFreq + '\'' +
                ", toothLesions=" + toothLesions +
                ", toothLesionsDB=" + toothLesionsDB +
                '}';
    }
}
