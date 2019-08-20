package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class Patient extends User{

    @SerializedName("pilotstudy_id")
    @Expose()
    private String pilotId;

    @SerializedName("gender")
    @Expose()
    private String gender;

    @Expose(serialize = false, deserialize = false)
    private String healthProfessionalId;

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
     * @return Patient
     */
    public static Patient jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typePatient = new TypeToken<Patient>() {
        }.getType();
        return gson.fromJson(json, typePatient);
    }

    @Override
    public String toString() {
        return "Patient{" +
                ", pilotId='" + pilotId + '\'' +
                ", gender='" + gender + '\'' +
                ", healthProfessionalId='" + healthProfessionalId + '\'' +
                '}';
    }
}
