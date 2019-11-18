package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.objectbox.SyncOB;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

@Entity
public class OdontologicalQuestionnaireOB extends SyncOB {

    @Id
    private long id;

    @Index
    private String _id;

    String createdAt;

    private long patientId;
    private String patient_id;

    ToOne<SociodemographicRecordOB> sociodemographicRecord;

    ToOne<FamilyCohesionRecordOB> familyCohesionRecord;

    ToOne<OralHealthRecordOB> oralHealthRecord;

    public OdontologicalQuestionnaireOB() { }

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

    public SociodemographicRecordOB getSociodemographicRecord() {
        return sociodemographicRecord.getTarget();
    }

    public void setSociodemographicRecord(SociodemographicRecordOB sociodemographicRecord) {
        this.sociodemographicRecord.setTarget(sociodemographicRecord);
    }

    public FamilyCohesionRecordOB getFamilyCohesionRecord() {
        return familyCohesionRecord.getTarget();
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecordOB familyCohesionRecord) {
        this.familyCohesionRecord.setTarget(familyCohesionRecord);
    }

    public OralHealthRecordOB getOralHealthRecord() {
        return oralHealthRecord.getTarget();
    }

    public void setOralHealthRecord(OralHealthRecordOB oralHealthRecord) {
        this.oralHealthRecord.setTarget(oralHealthRecord);
    }

    @Override
    public String toString() {
        return "OdontologicalQuestionnaireOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", sociodemographicRecord=" + sociodemographicRecord +
                ", familyCohesionRecord=" + familyCohesionRecord +
                ", oralHealthRecord=" + oralHealthRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OdontologicalQuestionnaireOB that = (OdontologicalQuestionnaireOB) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(createdAt, that.createdAt);
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }
}
