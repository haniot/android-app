package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class Patient extends User {

    @SerializedName("pilotstudy_id")
    @Expose()
    private String pilotId;

    @SerializedName("gender")
    @Expose()
    private String gender;

    @Expose(serialize = false, deserialize = false)
    private String healthProfessionalId;

    @SerializedName("created_at")
    @Expose()
    private String createdAt;

    public Patient() {
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHealthProfessionalId() {
        return healthProfessionalId;
    }

    public void setHealthProfessionalId(String healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        return a;
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PatientOB
     */
    public static Patient jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typePatient = new TypeToken<Patient>() {
        }.getType();
        return gson.fromJson(json, typePatient);
    }

    @Override
    public String toString() {
        return "Patient{" + super.toString() +
                ", pilotId='" + pilotId + '\'' +
                ", gender='" + gender + '\'' +
//                ", healthProfessionalId='" + healthProfessionalId + '\'' +
//                ", createdAt='" + createdAt + "\'" +
                '}';
    }
}
