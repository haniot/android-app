package br.edu.uepb.nutes.haniot.data.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.objectbox.relation.ToOne;

public class ToothLesion {

    @SerializedName("tooth_type")
    @Expose()
    private String toothType;

    @SerializedName("lesion_type")
    @Expose()
    private String lesionType;

    @Expose(serialize = false, deserialize = false)
    private ToOne<OralHealthRecord> oralHealthRecord;

    public ToothLesion() {
    }

    public String getToothType() {
        return toothType;
    }

    public void setToothType(String toothType) {
        this.toothType = toothType;
    }

    public String getLesionType() {
        return lesionType;
    }

    public void setLesionType(String lesionType) {
        this.lesionType = lesionType;
    }

    public ToOne<OralHealthRecord> getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(ToOne<OralHealthRecord> oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
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
