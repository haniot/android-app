package br.edu.uepb.nutes.haniot.model.elderly;

import java.util.Objects;

import io.objectbox.annotation.BaseEntity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@BaseEntity
public class Item {
    @Id
    private long id;

    @Index
    public int index;

    private String name;

    public Item() {
    }

    public Item(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return index == item.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", index=" + index +
                ", name='" + name + '\'' +
                '}';
    }
}
