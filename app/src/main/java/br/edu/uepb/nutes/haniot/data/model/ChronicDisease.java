package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ChronicDisease {

    @Id
    private long idBd;

    @SerializedName("type")
    private String type;

    @SerializedName("disease_history")
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

    @Override
    public String toString() {
        return "ChronicDisease{" +
                "idBd=" + idBd +
                ", type='" + type + '\'' +
                ", diseaseHistory='" + diseaseHistory + '\'' +
                '}';
    }
}
