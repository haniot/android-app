package br.edu.uepb.nutes.haniot.model;

import java.util.List;

import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;

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
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String name;

    @Index
    private String email;

    private String password;
    private int gender;
    private long dateOfBirth;
    private int height; // in cm

    private String token; // provide by the server

    /**
     * RELATIONS
     */

    /**
     * {@link Measurement}
     */
    @Backlink(to = "user")
    private ToMany<Measurement> measurements;

    @Backlink(to = "user")
    public ToMany<Elderly> elderlies;

    /**
     * {@link UserGroup()}
     */
    private int groupId; // 1 super, 2 comum

    public User() {
    }

    public User(String name, String email, int gender, long dateOfBirth, int height, int groupId) {
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.groupId = groupId;
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

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public ToMany<Elderly> getElderlies() {
        return elderlies;
    }

    public boolean addElderlies(List<Elderly> elderlies) {
        return getElderlies().addAll(elderlies);
    }

    public boolean addElderly(Elderly elderly) {
        return getElderlies().add(elderly);
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
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
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", gender=" + gender +
                ", dateOfBirth=" + dateOfBirth +
                ", height=" + height +
                ", token='" + token + '\'' +
                ", measurements=" + measurements +
                ", elderlies=" + elderlies +
                ", groupId=" + groupId +
                '}';
    }
}
