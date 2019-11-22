package br.edu.uepb.nutes.haniot.data.model.odontological;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;

public class OralHealthRecord extends ActivityHabitsRecord {

    @SerializedName("teeth_brushing_freq")
    @Expose()
    private String teethBrushingFreq;

    @SerializedName("teeth_lesions")
    @Expose()
    private List<ToothLesion> toothLesions;

    public OralHealthRecord() { }

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
                super.toString() +
                "teethBrushingFreq='" + teethBrushingFreq + '\'' +
                ", toothLesions=" + toothLesions +
                '}';
    }
}
