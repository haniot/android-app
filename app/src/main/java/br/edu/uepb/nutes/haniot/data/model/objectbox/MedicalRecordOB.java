package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;

@Entity
public class MedicalRecordOB extends ActivityHabitsRecordOB {

    @Backlink(to = "medicalRecord")
    private ToMany<ChronicDiseaseOB> chronicDiseases;

    public MedicalRecordOB(MedicalRecord m) {
        super(m.getId(), m.get_id(), m.getCreatedAt(), m.getPatientId());
        this.setChronicDiseases(Convert.listChronicsToObjectBox(m.getChronicDiseases()));
    }

    public List<ChronicDiseaseOB> getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(List<ChronicDiseaseOB> chronicDiseases) {
        this.chronicDiseases.clear();
        this.chronicDiseases.addAll(chronicDiseases);
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
