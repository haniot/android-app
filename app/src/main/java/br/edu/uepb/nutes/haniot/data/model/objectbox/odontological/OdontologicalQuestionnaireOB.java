package br.edu.uepb.nutes.haniot.data.model.objectbox.odontological;

import java.util.Objects;

import io.objectbox.annotation.Id;

public class OdontologicalQuestionnaireOB {

    @Id
    private long id;

    private String _id;

    String createdAt;

    SociodemographicRecordOB sociodemographicRecord;

    FamilyCohesionRecordOB familyCohesionRecord;

    OralHealthRecordOB oralHealthRecord;

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
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecordOB sociodemographicRecord) {
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public FamilyCohesionRecordOB getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecordOB familyCohesionRecord) {
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public OralHealthRecordOB getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecordOB oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
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
}
