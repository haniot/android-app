package br.edu.uepb.nutes.haniot.data.model;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class FeedingHabitsRecord {

    @Id
    private long idBd;

    private String id;
    private String patientId;
    private String createdAt;
    private ToMany<WeeklyFoodRecord> weeklyFeedingHabits;
    private String dailyWaterGlasses;
    private String sixMonthBreastFeeding;
    private String foodAllergyintolerance;
    private String breakfastDailyFrequency;

    public long getIdBd() {
        return idBd;
    }

    public void setIdBd(long idBd) {
        this.idBd = idBd;
    }

    public String getFoodAllergyintolerance() {
        return foodAllergyintolerance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public ToMany<WeeklyFoodRecord> getWeeklyFeedingHabits() {
        return weeklyFeedingHabits;
    }

    public void setWeeklyFeedingHabits(List<WeeklyFoodRecord> weeklyFeedingHabits) {
        this.weeklyFeedingHabits.addAll(weeklyFeedingHabits);
    }

    public String getDailyWaterGlasses() {
        return dailyWaterGlasses;
    }

    public void setDailyWaterGlasses(String dailyWaterGlasses) {
        this.dailyWaterGlasses = dailyWaterGlasses;
    }

    public String getSixMonthBreastFeeding() {
        return sixMonthBreastFeeding;
    }

    public void setSixMonthBreastFeeding(String sixMonthBreastFeeding) {
        this.sixMonthBreastFeeding = sixMonthBreastFeeding;
    }

    public String getFoodAllergyStringolerance() {
        return foodAllergyintolerance;
    }

    public void setFoodAllergyintolerance(String foodAllergyStringolerance) {
        this.foodAllergyintolerance = foodAllergyStringolerance;
    }

    public String getBreakfastDailyFrequency() {
        return breakfastDailyFrequency;
    }

    public void setBreakfastDailyFrequency(String breakfastDailyFrequency) {
        this.breakfastDailyFrequency = breakfastDailyFrequency;
    }

    @Override
    public String toString() {
        return "FeedingHabitsRecord{" +
                "idBd=" + idBd +
                ", id='" + id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", weeklyFeedingHabits=" + weeklyFeedingHabits +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyintolerance='" + foodAllergyintolerance + '\'' +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
