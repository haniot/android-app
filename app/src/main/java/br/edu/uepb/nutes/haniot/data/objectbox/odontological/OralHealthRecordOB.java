package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;

@Entity
public class OralHealthRecordOB extends ActivityHabitsRecordOB {

    private String teethBrushingFreq;

    private ToMany<ToothLesionOB> toothLesions;

    public OralHealthRecordOB() {}

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
                super.toString() +
                "teethBrushingFreq='" + teethBrushingFreq + '\'' +
                ", toothLesions=" + toothLesions +
                '}';
    }
}
