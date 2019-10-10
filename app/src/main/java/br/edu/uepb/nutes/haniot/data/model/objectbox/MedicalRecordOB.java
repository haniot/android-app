package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;

@Entity
public class MedicalRecordOB extends ActivityHabitsRecordOB {

    @Backlink(to = "medicalRecord")
    private ToMany<ChronicDiseaseOB> chronicDiseases;

    public MedicalRecordOB() { }

    public MedicalRecordOB(MedicalRecord m) {

    }

    public List<ChronicDiseaseOB> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(ToMany<ChronicDiseaseOB> chronicDiseases) {
        this.chronicDiseases.clear();
        this.chronicDiseases.addAll(chronicDiseases);
    }

    public void addChronicDiseases(ChronicDiseaseOB... chronicDiseases) {
        for (ChronicDiseaseOB choChronicDisease : chronicDiseases) {
            this.chronicDiseases.add(choChronicDisease);
        }
    }

    public ToMany<ChronicDiseaseOB> getChronicDiseasesDB() {
        return chronicDiseases;
    }

    @Override
    public String toString() {
        return super.toString() +
                " MedicalRecordOB{" +
                ", chronicDiseases=" + chronicDiseases +
                '}';
    }
}
