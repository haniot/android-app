package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents User object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 2.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class User {
    @Id
    @Expose(serialize = false)
    private long idDb;

    @Index
    @SerializedName("id")
    private String _id; // _id in server remote (UUID)

    @SerializedName("name")
    private String name;

    @Index
    @SerializedName("email")
    private String email;

    @SerializedName("token")
    private String token; // provide by the server

    @Expose(serialize = false)
    private String password;

    @SerializedName("healthArea")
    private String healthArea; // provide by the server

    private ToOne<PilotStudy> pilotStudy;
    /**
     * RELATIONS
     */

    /**
     * {@link Measurement}
     */
    @Backlink(to = "user")
    private ToMany<Measurement> measurements;


    /**
     * {@link UserType ()}
     */
    private int userType; // 1 admin, 2 health_profissional

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, int userType) {
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public long getIdDb() {
        return idDb;
    }

    public void setIdDb(long id) {
        this.idDb = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
    }

    public String getHealthArea() {
        return healthArea;
    }

    public void setHealthArea(String healthArea) {
        this.healthArea = healthArea;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static User jsonDeserialize(String json) {
        Type typeUser = new TypeToken<User>() {
        }.getType();
        return new Gson().fromJson(json, typeUser);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;

        User other = (User) o;

        return other.get_id().equals(this.get_id()) && other.getEmail().equals(this.getEmail());
    }

    public ToOne<PilotStudy> getPilotStudy() {
        return pilotStudy;
    }

    public void setPilotStudy(ToOne<PilotStudy> pilotStudy) {
        this.pilotStudy = pilotStudy;
    }

    public void setPilotStudy(PilotStudy pilotStudy) {
        this.pilotStudy.setTarget(pilotStudy);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + idDb +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", measurements=" + measurements +
                ", userType=" + userType +
                '}';
    }
}
