package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.NameInDb;
import io.objectbox.annotation.Uid;

/**
 * Represents User object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class User {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String name;
    private String email;
    private String password;
    private int gender;
    private int height; // in cm
    private long dateOfBirth;
    private String token;
    private int group; // 1 super, 2 comum

    public User() {
    }

    public User(String name, String email, String password, int gender, int height, long dateOfBirth) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.height = height;
        this.dateOfBirth = dateOfBirth;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (_id != null ? _id.hashCode() : 0);
        result = 31 * result + email.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;

        User other = (User) o;

        if (id != other.id) return false;
        if (_id != null ? !_id.equals(other._id) : other._id != null) return false;
        return email.equals(other.email);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", height=" + height +
                ", dateOfBirth=" + dateOfBirth +
                ", token='" + token + '\'' +
                ", group=" + group +
                '}';
    }
}
