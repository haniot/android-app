package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.objectbox.annotation.Id;

public class OdontologicalQuestionnaire {

    @Id
    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    @SerializedName("created_at")
    @Expose()
    String createdAt;

    @SerializedName("sociodemographic_recod")
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
    }

    public SociodemographicRecord getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public FamilyCohesionRecord getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecord familyCohesionRecord) {
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public OralHealthRecord getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecord oralHealthRecord) {
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
        return "OdontologicalQuestionnaire{" +
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
}
