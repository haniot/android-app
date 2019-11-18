package br.edu.uepb.nutes.haniot.data.model.odontological;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ToothLesion {

    private long id;

    @SerializedName("tooth_type")
    @Expose()
    private String toothType;

    @SerializedName("lesion_type")
    @Expose()
    private String lesionType;

    public ToothLesion() {
    }

    public ToothLesion(String toothType, String lesionType) {
        this.toothType = toothType;
        this.lesionType = lesionType;
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

    @Override
    public String toString() {
        return "ToothLesionOB{" +
                "toothType='" + toothType + '\'' +
                ", lesionType='" + lesionType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToothLesion that = (ToothLesion) o;
        return toothType.equals(that.toothType) &&
                lesionType.equals(that.lesionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toothType, lesionType);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
