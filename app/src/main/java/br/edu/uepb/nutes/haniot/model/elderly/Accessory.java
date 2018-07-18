package br.edu.uepb.nutes.haniot.model.elderly;

import br.edu.uepb.nutes.haniot.model.elderly.Elderly;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

/**
 * Represents the accessory type object of an elderly person.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Accessory extends Item {
    public Accessory() {
    }

    public Accessory(int index, String name) {
        super(index, name);
    }
}
