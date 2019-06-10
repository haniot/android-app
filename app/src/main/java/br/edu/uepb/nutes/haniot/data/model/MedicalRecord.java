package br.edu.uepb.nutes.haniot.data.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

@Entity
public class MedicalRecord extends ActivityHabitsRecord {
    @SerializedName("chronic_diseases")
    @Expose()
    @Transient // not persisted
    private List<ChronicDisease> chronicDiseases;

    @Expose(serialize = false, deserialize = false)
    @Backlink(to = "medicalRecord")
    private ToMany<ChronicDisease> chronicDiseasesDB;

    public MedicalRecord() {
    }

    public List<ChronicDisease> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(List<ChronicDisease> chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public void addChronicDiseases(ChronicDisease... chronicDiseases) {
        if (this.chronicDiseases == null) this.chronicDiseases = new ArrayList<>();
        for (ChronicDisease choChronicDisease : chronicDiseases) {
            this.chronicDiseases.add(choChronicDisease);
        }
    }

    public ToMany<ChronicDisease> getChronicDiseasesDB() {
        return chronicDiseasesDB;
    }

    public void setChronicDiseasesDB(List<ChronicDisease> chronicDiseasesDB) {
        this.getChronicDiseasesDB().clear();
        this.getChronicDiseasesDB().addAll(chronicDiseasesDB);
    }

    public void addChronicDiseasesDB(ChronicDisease... chronicDiseases) {
        for (ChronicDisease chronicDisease : chronicDiseases) {
            this.getChronicDiseasesDB().add(chronicDisease);
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
     * @return Patient
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
                "chronicDiseaseDB=" + chronicDiseasesDB +
                ", chronicDiseases=" + chronicDiseases +
                '}';
    }
}
