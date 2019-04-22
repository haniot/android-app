package br.edu.uepb.nutes.haniot.data.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("created_at")
    @Expose()
    private String createdAt;

    @SerializedName("teeth_brushing_freq")
    @Expose()
    private String teethBrushingFreq;

    @SerializedName("toothLesions")
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

    public void setToothLesionsDB(List<ToothLesion> toothLesionsDB) {
        this.toothLesionsDB.addAll(toothLesionsDB);
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        Log.i("AAAAAAAAAA", a);
        return a;
    }
}
