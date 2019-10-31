package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.nutritional.ChronicDisease;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class ChronicDiseaseOB {
    @Id
    private long id;

    private String type;

    private String diseaseHistory;

    private ToOne<MedicalRecordOB> medicalRecord;

    public ChronicDiseaseOB() {}

    public ChronicDiseaseOB(ChronicDisease c) {
        this.setId(c.getId());
        this.setType(c.getType());
        this.setDiseaseHistory(c.getDiseaseHistory());
        this.medicalRecord.setTarget(
                Convert.convertMedicalRecord(c.getMedicalRecord()));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDiseaseHistory() {
        return diseaseHistory;
    }

    public void setDiseaseHistory(String diseaseHistory) {
        this.diseaseHistory = diseaseHistory;
    }

    public ToOne<MedicalRecordOB> getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(ToOne<MedicalRecordOB> medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    @Override
    public String toString() {
        return "ChronicDiseaseOB{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", diseaseHistory='" + diseaseHistory + '\'' +
                ", medicalRecord=" + medicalRecord +
                '}';
    }
}
