package br.edu.uepb.nutes.haniot.data.model.objectbox;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class ChronicDisease {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("type")
    @Expose()
    private String type;

    @SerializedName("disease_history")
    @Expose()
    private String diseaseHistory;

    @Expose(serialize = false, deserialize = false)
    private ToOne<MedicalRecord> medicalRecord;

    public ChronicDisease() {
    }

    public ChronicDisease(String type, String diseaseHistory) {
        this.type = type;
        this.diseaseHistory = diseaseHistory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiseaseHistory() {
        return diseaseHistory;
    }

    public void setDiseaseHistory(String diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
    }

    public ToOne<MedicalRecord> getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(ToOne<MedicalRecord> medicalRecord) {
        this.medicalRecord = medicalRecord;
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
    public static ChronicDisease jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeChronicDisease = new TypeToken<ChronicDisease>() {
        }.getType();
        return gson.fromJson(json, typeChronicDisease);
    }

    @Override
    public String toString() {
        return "ChronicDisease{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", diseaseHistory='" + diseaseHistory + '\'' +
                ", medicalRecord=" + medicalRecord +
                '}';
    }
}
