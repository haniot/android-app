package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChronicDiseaseOB {
    @Id
    private long id;

    private String type;

    private String diseaseHistory;

    public ChronicDiseaseOB() {}

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

    @Override
    public String toString() {
        return "ChronicDiseaseOB{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", diseaseHistory='" + diseaseHistory + '\'' +
                '}';
    }
}
