package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class FeedingHabitsRecord {

    @Id
    private long idBd;

    @SerializedName("id")
    private String _id;

    @SerializedName("patient_id")
    private String patientId;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("weekly_feeding_habits")
    private List<WeeklyFoodRecord> weeklyFoodRecords;

    @Expose(serialize = false, deserialize = false)
    private ToMany<WeeklyFoodRecord> weeklyFoodRecordsDB;

    @SerializedName("daily_water_glasses")
    private String dailyWaterGlasses;

    @SerializedName("six_month_breast_feeding")
    private String sixMonthBreastFeeding;

    @SerializedName("food_allergy_intolerance")
    private String foodAllergyintolerance;

    @SerializedName("breakfast_daily_frequency")
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

    public String get_id() {
        return _id;
    }

    public void set_id(String id) {
        this._id = id;
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


    public String getDailyWaterGlasses() {
        return dailyWaterGlasses;
    }

    public void setDailyWaterGlasses(String dailyWaterGlasses) {
        this.dailyWaterGlasses = dailyWaterGlasses;
    }

    public List<WeeklyFoodRecord> getWeeklyFoodRecords() {
        return weeklyFoodRecords;
    }

    public void setWeeklyFoodRecords(List<WeeklyFoodRecord> weeklyFoodRecords) {
        this.weeklyFoodRecords = weeklyFoodRecords;
    }

    public ToMany<WeeklyFoodRecord> getWeeklyFoodRecordsDB() {
        return weeklyFoodRecordsDB;
    }

    public void setWeeklyFoodRecordsDB(List<WeeklyFoodRecord> weeklyFoodRecordsDB) {
        this.weeklyFoodRecordsDB.addAll(weeklyFoodRecordsDB);
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
                ", id='" + _id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyintolerance='" + foodAllergyintolerance + '\'' +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
