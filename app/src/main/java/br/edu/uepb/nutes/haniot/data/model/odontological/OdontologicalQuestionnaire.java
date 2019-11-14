package br.edu.uepb.nutes.haniot.data.model.odontological;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Sync;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB;

public class OdontologicalQuestionnaire extends Sync {

    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    private String patientId;

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

    public OdontologicalQuestionnaire(OdontologicalQuestionnaireOB o) {
        this.setId(o.getId());
        this.set_id(o.get_id());
        this.setPatientId(o.getPatientId());
        this.setCreatedAt(o.getCreatedAt());

        this.setSociodemographicRecord(Convert.convertSociodemographicRecord(o.getSociodemographicRecord()));
        this.setFamilyCohesionRecord(Convert.convertFamilyCohesionRecord(o.getFamilyCohesionRecord()));
        this.setOralHealthRecord(Convert.convertOralHealthRecord(o.getOralHealthRecord()));
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
        this.getSociodemographicRecord().setCreatedAt(createdAt);
        this.getOralHealthRecord().setCreatedAt(createdAt);
        this.getFamilyCohesionRecord().setCreatedAt(createdAt);
    }

    public SociodemographicRecord getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
        sociodemographicRecord.setCreatedAt(this.getCreatedAt());
        sociodemographicRecord.setPatientId(this.getPatientId());
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public FamilyCohesionRecord getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecord familyCohesionRecord) {
        familyCohesionRecord.setCreatedAt(this.getCreatedAt());
        familyCohesionRecord.setPatientId(this.getPatientId());
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public OralHealthRecord getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecord oralHealthRecord) {
        oralHealthRecord.setCreatedAt(this.getCreatedAt());
        oralHealthRecord.setPatientId(this.getPatientId());
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

    public String getPatientId() {
        return this.patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
        this.getFamilyCohesionRecord().setPatientId(patientId);
        this.getOralHealthRecord().setPatientId(patientId);
        this.getSociodemographicRecord().setPatientId(patientId);
    }
}
