package br.edu.uepb.nutes.haniot.model.elderly;

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

    private String registrationDate;

    /**
     * {@link FallCharacterization}
     */
    private ToOne<FallCharacterization> characterization;

    /**
     * {@link Elderly}
     */
    private ToOne<Elderly> elderly;

    public Fall() {
    }

    public Fall(String registrationDate, FallCharacterization characterization) {
        this.registrationDate = registrationDate;
        this.setCharacterization(characterization);
    }

    public Fall(String _id, String registrationDate, FallCharacterization characterization, Elderly elderly) {
        this._id = _id;
        this.registrationDate = registrationDate;
        this.setCharacterization(characterization);
        this.setElderly(elderly);
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

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public ToOne<FallCharacterization> getCharacterization() {
        return characterization;
    }

    public void setCharacterization(FallCharacterization characterization) {
        this.characterization.setTarget(characterization);
    }

    public ToOne<Elderly> getElderly() {
        return elderly;
    }

    public void setElderly(Elderly elderly) {
        this.elderly.setTarget(elderly);
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
                ", registrationDate='" + registrationDate + '\'' +
                ", characterization=" + characterization +
                ", elderly=" + elderly +
                '}';
    }
}


