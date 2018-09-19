package br.edu.uepb.nutes.haniot.model.elderly;

import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.converter.PropertyConverter;

/**
 * Represents object of a Fall Characterization.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
@Entity
public class FallCharacterization {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String registrationDate;

    private boolean isFinalized;



    public FallCharacterization() {
    }

    public FallCharacterization(boolean isFinalized) {
        this.isFinalized = isFinalized;
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

    public boolean isFinalized() {
        return isFinalized;
    }

    public void setFinalized(boolean finalized) {
        isFinalized = finalized;
    }


}


