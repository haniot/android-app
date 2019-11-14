package br.edu.uepb.nutes.haniot.data.model.nutritional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.MedicalRecordOB;

public class MedicalRecord extends ActivityHabitsRecord {

    @SerializedName("chronic_diseases")
    @Expose()
    private List<ChronicDisease> chronicDiseases;

    public MedicalRecord() { }

    public MedicalRecord(MedicalRecordOB m) {
        this.setChronicDiseases(Convert.listChronicsToModel(m.getChronicDiseases()));
    }

    public List<ChronicDisease> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(List<ChronicDisease> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public void addChronicDiseases(ChronicDisease... chronicDiseases) {
        if (this.chronicDiseases == null)
            this.chronicDiseases = new ArrayList<>();
        for (ChronicDisease choChronicDisease : chronicDiseases) {
            this.chronicDiseases.add(choChronicDisease);
        }
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

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PatientOB
     */
    public static MedicalRecord jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeMedicalRecords = new TypeToken<MedicalRecord>() {
        }.getType();
        return gson.fromJson(json, typeMedicalRecords);
    }

    @Override
    public String toString() {
        return super.toString() +
                " MedicalRecord{" +
                ", chronicDiseases=" + chronicDiseases +
                '}';
    }
}
