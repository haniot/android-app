package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents User object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class User {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @Index
    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @Index
    @SerializedName("email")
    @Expose()
    private String email;

    @SerializedName("name")
    @Expose()
    private String name;

    @Expose(deserialize = false)
    private String password;

    @SerializedName("old_password")
    @Expose(deserialize = false)
    private String oldPassword;

    @SerializedName("new_password")
    @Expose(deserialize = false)
    private String newPassword;

    @SerializedName("health_area")
    @Expose()
    private String healthArea; // provide by the server

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

    public User(String _id, String oldPassword, String newPassword) {
        this._id = _id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
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

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return User
     */
    public static User jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeUser = new TypeToken<User>() {
        }.getType();
        return gson.fromJson(json, typeUser);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;

        User other = (User) o;

        return other.get_id().equals(this.get_id()) && other.getEmail().equals(this.getEmail());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", healthArea='" + healthArea + '\'' +
                ", userType=" + userType +
                '}';
    }
}
