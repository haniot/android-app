package br.edu.uepb.nutes.haniot.model;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

/**
 * Represents object of a Fall.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
@Entity
public class Fall {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private long registrationDate;

    private ToOne<FallProfile> profile;

    public ToOne<Elderly> elderly;

    public Fall() {
    }

    public Fall(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Fall(long registrationDate, FallProfile profile) {
        this.registrationDate = registrationDate;
        this.setProfile(profile);
    }

    public Fall(String _id, long registrationDate, FallProfile profile) {
        this._id = _id;
        this.registrationDate = registrationDate;
        this.setProfile(profile);
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

    public long getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(long registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ToOne<FallProfile> getProfile() {
        return profile;
    }

    public void setProfile(FallProfile profile) {
        this.profile.setTarget(profile);
    }

    public ToOne<Elderly> getElderly() {
        return elderly;
    }

    public void setElderly(ToOne<Elderly> elderly) {
        this.elderly = elderly;
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationDate);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Fall)) return false;

        Fall fall = (Fall) o;
        return registrationDate == fall.registrationDate;
    }

    @Override
    public String toString() {
        return "Fall{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", registrationDate=" + registrationDate +
                ", profile=" + profile +
                ", elderly=" + elderly +
                '}';
    }
}


