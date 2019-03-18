package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChronicDisease {

    @Id
    private long idBd;

    private String type;
    private String diseaseHistory;

    public long getIdBd() {
        return idBd;
    }

    public void setIdBd(long idBd) {
        this.idBd = idBd;
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
}
