package br.edu.uepb.nutes.haniot.data.model.objectbox;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;
//import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;

@Entity
public class FeedingHabitsRecordOB extends ActivityHabitsRecordOB {

    @Transient // not persisted
    private List<WeeklyFoodRecordOB> weeklyFeedingHabits;

    @Backlink(to = "feedingHabitsRecord")
    private ToMany<WeeklyFoodRecordOB> weeklyFeedingHabitsDB;

    private String dailyWaterGlasses;

    private String sixMonthBreastFeeding;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> foodAllergyIntolerance;

    private String breakfastDailyFrequency;

    public FeedingHabitsRecordOB() {
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

    public List<WeeklyFoodRecordOB> getWeeklyFeedingHabits() {
        return weeklyFeedingHabits;
    }

    public void setWeeklyFeedingHabits(List<WeeklyFoodRecordOB> weeklyFeedingHabits) {
        this.weeklyFeedingHabits = weeklyFeedingHabits;
    }

    public void addWeeklyFeedingHabits(WeeklyFoodRecordOB... weeklyFeedingHabits) {
        if (this.weeklyFeedingHabits == null) this.weeklyFeedingHabits = new ArrayList<>();
        for (WeeklyFoodRecordOB weeklyFoodRecord : weeklyFeedingHabits) {
            this.weeklyFeedingHabits.add(weeklyFoodRecord);
        }
    }

    public ToMany<WeeklyFoodRecordOB> getWeeklyFeedingHabitsDB() {
        return weeklyFeedingHabitsDB;
    }

    public void setWeeklyFeedingHabitsDB(List<WeeklyFoodRecordOB> weeklyFeedingHabits) {
        this.getWeeklyFeedingHabitsDB().clear();
        this.getWeeklyFeedingHabitsDB().addAll(weeklyFeedingHabits);
    }

    public void addWeeklyFeedingHabitsDB(WeeklyFoodRecordOB... weeklyFoodRecord) {
        for (WeeklyFoodRecordOB weeklyFo : weeklyFoodRecord) {
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

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        return gson.toJson(this);
//    }
//
//    /**
//     * Convert json to Object.
//     *
//     * @param json String
//     * @return PatientOB
//     */
//    public static FeedingHabitsRecordOB jsonDeserialize(String json) {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        Type typeFeedingHabits = new TypeToken<FeedingHabitsRecordOB>() {
//        }.getType();
//        return gson.fromJson(json, typeFeedingHabits);
//    }

    @Override
    public String toString() {
        return super.toString() +
                " FeedingHabitsRecordOB{" +
                "weeklyFeedingHabits=" + weeklyFeedingHabits +
                ", weeklyFeedingHabitsDB=" + weeklyFeedingHabitsDB +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyIntolerance=" + foodAllergyIntolerance +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
