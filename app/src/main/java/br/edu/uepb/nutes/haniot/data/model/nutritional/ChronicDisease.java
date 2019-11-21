package br.edu.uepb.nutes.haniot.data.model.nutritional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class ChronicDisease {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("type")
    @Expose()
    private String type;

    @SerializedName("disease_history")
    @Expose()
    private String diseaseHistory;

    public ChronicDisease() { }

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
                '}';
    }
}
