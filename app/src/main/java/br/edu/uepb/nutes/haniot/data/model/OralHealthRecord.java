package br.edu.uepb.nutes.haniot.data.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

public class OralHealthRecord {

    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;


    @Expose(serialize = false, deserialize = false)
    private String patientId;

    @SerializedName("created_at")
    @Expose(serialize = false)
    private String createdAt;

    @SerializedName("teeth_brushing_freq")
    @Expose()
    private String teethBrushingFreq;

    @SerializedName("teeth_lesions")
    @Expose()
    @Transient // not persisted
    private List<ToothLesion> toothLesions;

    @Expose(serialize = false, deserialize = false)
    @Backlink(to = "oralHealthRecord")
    private ToMany<ToothLesion> toothLesionsDB;

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

    public List<ToothLesion> getToothLesions() {
        return toothLesions;
    }

    public void setToothLesions(List<ToothLesion> toothLesions) {
        this.toothLesions = toothLesions;
    }

    public ToMany<ToothLesion> getToothLesionsDB() {
        return toothLesionsDB;
    }

    public void setToothLesionsDB(ToMany<ToothLesion> toothLesionsDB) {
        this.toothLesionsDB = toothLesionsDB;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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
}
