package br.edu.uepb.nutes.haniot.model;

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
public class Medication {
    @Id
    private long id;
    private String name;
    private String description;
    public ToOne<Elderly> elderly;

    public Medication() {
    }

    public Medication(String name) {
        this.name = name;
    }

    public Medication(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ToOne<Elderly> getElderly() {
        return elderly;
    }

    public void setElderly(ToOne<Elderly> elderly) {
        this.elderly = elderly;
    }

    @Override
    public String toString() {
        return "Medication{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", elderly=" + elderly +
                '}';
    }
}
