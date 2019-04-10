package br.edu.uepb.nutes.haniot.data.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

@Entity
public class FeedingHabitsRecord extends ActivityHabitsRecord {
    @SerializedName("weekly_feeding_habits")
    @Expose()
    @Transient // not persisted
    private List<WeeklyFoodRecord> weeklyFeedingHabits;

    @Expose(serialize = false, deserialize = false)
    @Backlink(to = "feedingHabitsRecord")
    private ToMany<WeeklyFoodRecord> weeklyFeedingHabitsDB;

    @SerializedName("daily_water_glasses")
    @Expose()
    private String dailyWaterGlasses;

    @SerializedName("six_month_breast_feeding")
    @Expose()
    private String sixMonthBreastFeeding;

    @SerializedName("food_allergy_intolerance")
    @Expose()
    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
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

    public ToMany<WeeklyFoodRecord> getWeeklyFeedingHabitsDB() {
        return weeklyFeedingHabitsDB;
    }

    public void setWeeklyFeedingHabitsDB(List<WeeklyFoodRecord> weeklyFeedingHabits) {
        this.getWeeklyFeedingHabitsDB().clear();
        this.getWeeklyFeedingHabitsDB().addAll(weeklyFeedingHabits);
    }

    public void addWeeklyFeedingHabitsDB(WeeklyFoodRecord... weeklyFoodRecord) {
        for (WeeklyFoodRecord weeklyFo : weeklyFoodRecord) {
            this.getWeeklyFeedingHabitsDB().add(weeklyFo);
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
     * @return Patient
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
                " FeedingHabitsRecord{" +
                "weeklyFeedingHabits=" + weeklyFeedingHabits +
                ", weeklyFeedingHabitsDB=" + weeklyFeedingHabitsDB +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyIntolerance=" + foodAllergyIntolerance +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
