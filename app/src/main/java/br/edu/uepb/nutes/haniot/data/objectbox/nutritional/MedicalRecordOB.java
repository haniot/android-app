package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;

@Entity
public class MedicalRecordOB extends ActivityHabitsRecordOB {

//    @Backlink(to = "medicalRecord")
    private ToMany<ChronicDiseaseOB> chronicDiseases;

    public MedicalRecordOB() {
        super();
    }

    public List<ChronicDiseaseOB> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(List<ChronicDiseaseOB> chronicDiseases) {
        this.chronicDiseases.clear();
        this.chronicDiseases.addAll(chronicDiseases);
    }

    @Override
    public String toString() {
        return super.toString() +
                " MedicalRecordOB{" +
                ", chronicDiseases=" + chronicDiseases +
                '}';
    }
}
