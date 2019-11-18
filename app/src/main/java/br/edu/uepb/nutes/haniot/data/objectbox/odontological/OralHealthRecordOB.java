package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;

@Entity
public class OralHealthRecordOB {

    @Id
    private long id;

    @Index
    private String _id;

    private String teethBrushingFreq;

    private ToMany<ToothLesionOB> toothLesions;

    public OralHealthRecordOB() {}

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

    public String getTeethBrushingFreq() {
        return teethBrushingFreq;
    }

    public void setTeethBrushingFreq(String teethBrushingFreq) {
        this.teethBrushingFreq = teethBrushingFreq;
    }

    public List<ToothLesionOB> getToothLesions() {
        return toothLesions;
    }

    public void setToothLesions(List<ToothLesionOB> toothLesions) {
        this.toothLesions.clear();
        this.toothLesions.addAll(toothLesions);
    }

    @Override
    public String toString() {
        return "OralHealthRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", teethBrushingFreq='" + teethBrushingFreq + '\'' +
//                ", toothLesions=" + toothLesions +
                ", toothLesions=" + toothLesions +
                '}';
    }
}
