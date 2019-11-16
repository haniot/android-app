package br.edu.uepb.nutes.haniot.data.model.odontological;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OralHealthRecordOB;

public class OralHealthRecord {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

//    @Expose(serialize = false, deserialize = false)
//    private String patientId;
//
//    @SerializedName("created_at")
//    @Expose(serialize = false)
//    private String createdAt;

    @SerializedName("teeth_brushing_freq")
    @Expose()
    private String teethBrushingFreq;

    @SerializedName("teeth_lesions")
    @Expose()
    private List<ToothLesion> toothLesions;

    public OralHealthRecord() { }

    public OralHealthRecord(OralHealthRecordOB o) {
        this.setId(o.getId());
        this.set_id(o.get_id());
//        this.setPatientId(o.getPatientId());
//        this.setCreatedAt(o.getCreatedAt());
        this.setTeethBrushingFreq(o.getTeethBrushingFreq());
        this.setToothLesions(Convert.convertListToothLesionToModel(o.getToothLesions()));
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
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }

    public String getTeethBrushingFreq() {
        return teethBrushingFreq;
    }

    public void setTeethBrushingFreq(String teethBrushingFreq) {
        this.teethBrushingFreq = teethBrushingFreq;
    }

    public List<ToothLesion> getToothLesions() {
        return toothLesions;
    }

    public void setToothLesions(List<ToothLesion> toothLesions) {
        this.toothLesions = toothLesions;
    }

//    public String getPatientId() {
//        return patientId;
//    }
//
//    public void setPatientId(String patientId) {
//        this.patientId = patientId;
//    }

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
        return "OralHealthRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
//                ", patientId='" + patientId + '\'' +
//                ", createdAt='" + createdAt + '\'' +
                ", teethBrushingFreq='" + teethBrushingFreq + '\'' +
                ", toothLesions=" + toothLesions +
//                ", toothLesionsDB=" + toothLesionsDB +
                '}';
    }
}
