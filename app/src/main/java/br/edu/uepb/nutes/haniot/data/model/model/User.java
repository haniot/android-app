package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.edu.uepb.nutes.haniot.data.model.type.UserType;

/**
 * Represents UserOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class User {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @SerializedName("email")
    @Expose()
    private String email;

    @SerializedName("name")
    @Expose()
    private String name;

    @SerializedName("birth_date")
    @Expose()
    private String birthDate;

    @SerializedName("health_area")
    @Expose()
    private String healthArea;

    @Expose(deserialize = false)
    private String password;

    @SerializedName("old_password")
    @Expose(deserialize = false)
    private String oldPassword;

    @SerializedName("new_password")
    @Expose(deserialize = false)
    private String newPassword;

    @SerializedName("phone_number")
    @Expose()
    private String phoneNumber; // provide by the server

    @SerializedName("last_login")
    @Expose()
    private String lastLogin;

    @SerializedName("last_sync")
    @Expose()
    private String lastSync;

    @SerializedName("language")
    @Expose()
    private String language;

    @SerializedName("selected_pilot_study")
    @Expose()
    private String pilotStudyIDSelected;
    /**
     * {@link UserType ()}
     */
    @Expose()
    private String userType; // 1 admin, 2 health_profissional

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
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

    public String getPilotStudyIDSelected() {
        return pilotStudyIDSelected;
    }

    public void setPilotStudyIDSelected(String pilotStudyIDSelected) {
        this.pilotStudyIDSelected = pilotStudyIDSelected;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLastSync() {
        return lastSync;
    }

    public void setLastSync(String lastSync) {
        this.lastSync = lastSync;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHealthArea() {
        return healthArea;
    }

    public void setHealthArea(String healthArea) {
        this.healthArea = healthArea;
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return UserOB
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

        return other.get_id().equals(this.get_id())
                && (other.email == null
                || this.email == null
                || other.getEmail().equals(this.getEmail()));
    }

    @Override
    public String toString() {
        return "UserOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", healthArea='" + healthArea + '\'' +
                ", password='" + password + '\'' +
                ", oldPassword='" + oldPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", lastLogin='" + lastLogin + '\'' +
                ", lastSync='" + lastSync + '\'' +
                ", language='" + language + '\'' +
                ", pilotStudyIDSelected='" + pilotStudyIDSelected + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
