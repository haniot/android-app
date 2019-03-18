package br.edu.uepb.nutes.haniot.model;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class FeedingHabitsRecord {

    @Id
    private long idBd;

    private String id;
    private String patient_id;
    private String createdAt;
    private List<WeeklyFoodRecord> weeklyFeedingHabits;
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

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public List<WeeklyFoodRecord> getWeeklyFeedingHabits() {
        return weeklyFeedingHabits;
    }

    public void setWeeklyFeedingHabits(List<WeeklyFoodRecord> weeklyFeedingHabits) {
        this.weeklyFeedingHabits = weeklyFeedingHabits;
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
}
