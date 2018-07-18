package br.edu.uepb.nutes.haniot.model.elderly;

import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents the object of the type medication that for example an elderly person takes.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Medication extends Item {
    public Medication() {
    }

    public Medication(int index, String name) {
        super(index, name);
    }
}
