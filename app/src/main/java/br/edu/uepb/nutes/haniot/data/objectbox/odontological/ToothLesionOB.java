package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.odontological.ToothLesion;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class ToothLesionOB {

    @Id
    private long id;

    private String toothType;

    private String lesionType;

    public ToothLesionOB() {}

    public ToothLesionOB(ToothLesion t) {
        this.setId(t.getId());
        this.setToothType(t.getToothType());
        this.setLesionType(t.getLesionType());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToothType() {
        return toothType;
    }

    public void setToothType(String toothType) {
        this.toothType = toothType;
    }

    public String getLesionType() {
        return lesionType;
    }

    public void setLesionType(String lesionType) {
        this.lesionType = lesionType;
    }

    @Override
    public String toString() {
        return "ToothLesionOB{" +
                "toothType='" + toothType + '\'' +
                ", lesionType='" + lesionType + '\'' +
//                ", oralHealthRecord=" + oralHealthRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToothLesionOB that = (ToothLesionOB) o;
        return toothType.equals(that.toothType) &&
                lesionType.equals(that.lesionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toothType, lesionType);
    }
}
