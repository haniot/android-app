package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class WeeklyFoodRecord {
    @Id
    private long idBd;

    private String food;
    private String seveDaysFreq;

    public long getIdBd() {
        return idBd;
    }

    public void setIdBd(long idBd) {
        this.idBd = idBd;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getSeveDaysFreq() {
        return seveDaysFreq;
    }

    public void setSeveDaysFreq(String seveDaysFreq) {
        this.seveDaysFreq = seveDaysFreq;
    }
}
