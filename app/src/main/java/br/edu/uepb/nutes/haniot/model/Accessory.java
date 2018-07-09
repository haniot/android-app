package br.edu.uepb.nutes.haniot.model;

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
public class Accessory {
    @Id
    private long id;
    private String name;
    private String description;
    public ToOne<Elderly> elderly;
    public int index;

    public Accessory() {
    }

    public Accessory(String name) {
        this.name = name;
    }

    public Accessory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Accessory(int index, String name) {
        this.index = index;
        this.name = name;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "Accessory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", elderly=" + elderly +
                ", index=" + index +
                '}';
    }
}
