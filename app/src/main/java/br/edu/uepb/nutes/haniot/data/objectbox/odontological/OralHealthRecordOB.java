package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.odontological.OralHealthRecord;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;

@Entity
public class OralHealthRecordOB {

    @Id
    private long id;

    @Index
    private String _id;

    private String patientId;

    private String createdAt;

    private String teethBrushingFreq;

    private ToMany<ToothLesionOB> toothLesions;

    public OralHealthRecordOB() {}

    public OralHealthRecordOB(OralHealthRecord o) {
        this.setId(o.getId());
        this.set_id(o.get_id());
        this.setPatientId(o.getPatientId());
        this.setCreatedAt(o.getCreatedAt());
        this.setTeethBrushingFreq(o.getTeethBrushingFreq());
        this.setToothLesions(Convert.listToothLesionsToObjectBox(o.getToothLesions()));
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
        this.toothLesions.clear();
        this.toothLesions.addAll(toothLesions);
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
//                ", toothLesions=" + toothLesions +
                ", toothLesions=" + toothLesions +
                '}';
    }
}
