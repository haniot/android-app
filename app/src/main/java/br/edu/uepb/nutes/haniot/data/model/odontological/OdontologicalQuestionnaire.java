package br.edu.uepb.nutes.haniot.data.model.odontological;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.Sync;

public class OdontologicalQuestionnaire extends Sync {

    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    private String patient_id;

    private long patientId;

    @SerializedName("created_at")
    @Expose()
    String createdAt;

    @SerializedName("sociodemographic_record")
    @Expose()
    SociodemographicRecord sociodemographicRecord;

    @SerializedName("family_cohesion_record")
    @Expose()
    FamilyCohesionRecord familyCohesionRecord;

    @SerializedName("oral_health_record")
    @Expose()
    OralHealthRecord oralHealthRecord;

    public OdontologicalQuestionnaire() {
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
//        this.getSociodemographicRecord().setCreatedAt(createdAt);
//        this.getOralHealthRecord().setCreatedAt(createdAt);
//        this.getFamilyCohesionRecord().setCreatedAt(createdAt);
    }

    public SociodemographicRecord getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
//        sociodemographicRecord.setCreatedAt(this.getCreatedAt());
//        sociodemographicRecord.setPatientId(this.getPatient_id());
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public FamilyCohesionRecord getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecord familyCohesionRecord) {
//        familyCohesionRecord.setCreatedAt(this.getCreatedAt());
//        familyCohesionRecord.setPatientId(this.getPatient_id());
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public OralHealthRecord getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecord oralHealthRecord) {
//        oralHealthRecord.setCreatedAt(this.getCreatedAt());
//        oralHealthRecord.setPatientId(this.getPatient_id());
        this.oralHealthRecord = oralHealthRecord;
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
        OdontologicalQuestionnaire that = (OdontologicalQuestionnaire) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(createdAt, that.createdAt);
    }

    public String getPatient_id() {
        return this.patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
}
