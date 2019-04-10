package br.edu.uepb.nutes.haniot.data.model;

import android.util.Log;

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
public class Patient {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @Index
    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @SerializedName("pilotstudy_id")
    @Expose()
    private String pilotId;

    @SerializedName("first_name")
    @Expose()
    private String firstName;

    @SerializedName("last_name")
    @Expose()
    private String lastName;

    @SerializedName("gender")
    @Expose()
    private String gender;

    @SerializedName("birth_date")
    @Expose()
    private String birthDate;

    @Expose(serialize = false, deserialize = false)
    private String userId;

    public Patient() {

    }

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

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", pilotId='" + pilotId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", gender='" + gender + '\'' +
                ", birthDate='" + birthDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(_id, patient._id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id);
    }
}
