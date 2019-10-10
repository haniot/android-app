package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FeedingHabitsRecord extends ActivityHabitsRecord {
    @SerializedName("weekly_feeding_habits")
    @Expose()
    private List<WeeklyFoodRecord> weeklyFeedingHabits;

    @SerializedName("daily_water_glasses")
    @Expose()
    private String dailyWaterGlasses;

    @SerializedName("six_month_breast_feeding")
    @Expose()
    private String sixMonthBreastFeeding;

    @SerializedName("food_allergy_intolerance")
    @Expose()
    private List<String> foodAllergyIntolerance;

    @SerializedName("breakfast_daily_frequency")
    @Expose()
    private String breakfastDailyFrequency;

    public FeedingHabitsRecord() {
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

    public List<WeeklyFoodRecord> getWeeklyFeedingHabits() {
        return weeklyFeedingHabits;
    }

    public void setWeeklyFeedingHabits(List<WeeklyFoodRecord> weeklyFeedingHabits) {
        this.weeklyFeedingHabits = weeklyFeedingHabits;
    }

    public void addWeeklyFeedingHabits(WeeklyFoodRecord... weeklyFeedingHabits) {
        if (this.weeklyFeedingHabits == null) this.weeklyFeedingHabits = new ArrayList<>();
        for (WeeklyFoodRecord weeklyFoodRecord : weeklyFeedingHabits) {
            this.weeklyFeedingHabits.add(weeklyFoodRecord);
        }
    }

    public void addWeeklyFeedingHabitsDB(WeeklyFoodRecord... weeklyFoodRecord) {
        for (WeeklyFoodRecord weeklyFo : weeklyFoodRecord) {
            this.getWeeklyFeedingHabits().add(weeklyFo);
        }
    }

    public List<String> getFoodAllergyIntolerance() {
        return foodAllergyIntolerance;
    }

    public void setFoodAllergyIntolerance(List<String> foodAllergyIntolerance) {
        this.foodAllergyIntolerance = foodAllergyIntolerance;
    }

    public String getBreakfastDailyFrequency() {
        return breakfastDailyFrequency;
    }

    public void setBreakfastDailyFrequency(String breakfastDailyFrequency) {
        this.breakfastDailyFrequency = breakfastDailyFrequency;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PatientOB
     */
    public static FeedingHabitsRecord jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeFeedingHabits = new TypeToken<FeedingHabitsRecord>() {
        }.getType();
        return gson.fromJson(json, typeFeedingHabits);
    }

    @Override
    public String toString() {
        return super.toString() +
                " FeedingHabitsRecordOB{" +
                "weeklyFeedingHabits=" + weeklyFeedingHabits +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyIntolerance=" + foodAllergyIntolerance +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
