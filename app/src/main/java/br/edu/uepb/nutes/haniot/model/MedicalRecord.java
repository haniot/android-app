package br.edu.uepb.nutes.haniot.model;


import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class MedicalRecord {
    @Id
    private long idBd;

    private int id;
    private String patientId;
    private String createdAt;
    private List<ChronicDisease> chronicDisease;

    public long getIdBd() {
        return idBd;
    }

    public void setIdBd(long idBd) {
        this.idBd = idBd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List getChronicDisease() {
        return chronicDisease;
    }

    public void setChronicDisease(List chronicDisease) {
        this.chronicDisease = chronicDisease;
    }
}
