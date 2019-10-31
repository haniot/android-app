package br.edu.uepb.nutes.haniot.data.objectbox;

import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.type.UserType;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents UserOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class UserOB extends SyncOB {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    @Index
    private String email;

    private String name;

    private String birthDate;

    private String healthArea;

    private String password;

    private String oldPassword;

    private String newPassword;

    private String phoneNumber; // provide by the server

    private String lastLogin;

    private String lastSync;

    private String language;

    private String pilotStudyIDSelected;
    /**
     * {@link UserType ()}
     */
    private String userType; // 1 admin, 2 health_profissional

    public UserOB(long id, String _id, String email, String name, String birthDate, String healthArea,
                  String password, String oldPassword, String newPassword, String phoneNumber,
                  String lastLogin, String lastSync, String language, String pilotStudyIDSelected, String userType, boolean sync) {
        super(sync);
        this.id = id;
        this._id = _id;
        this.email = email;
        this.name = name;
        this.birthDate = birthDate;
        this.healthArea = healthArea;
        this.password = password;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.phoneNumber = phoneNumber;
        this.lastLogin = lastLogin;
        this.lastSync = lastSync;
        this.language = language;
        this.pilotStudyIDSelected = pilotStudyIDSelected;
        this.userType = userType;
    }

    public UserOB(User p) {
        this(p.getId(), p.get_id(), p.getEmail(), p.getName(), p.getBirthDate(), p.getHealthArea(), p.getPassword(), p.getOldPassword(), p.getNewPassword(),
                p.getPhoneNumber(), p.getLastLogin(), p.getLastSync(), p.getLanguage(), p.getPilotStudyIDSelected(), p.getUserType(), p.isSync());
    }

    public UserOB() {
        super();
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof UserOB))
            return false;

        UserOB other = (UserOB) o;

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
