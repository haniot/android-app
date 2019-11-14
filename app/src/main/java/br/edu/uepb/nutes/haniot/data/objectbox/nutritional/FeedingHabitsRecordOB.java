package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.relation.ToMany;

@Entity
public class FeedingHabitsRecordOB extends ActivityHabitsRecordOB {

    private String dailyWaterGlasses;

    private String sixMonthBreastFeeding;

    private String breakfastDailyFrequency;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> foodAllergyIntolerance;

//    @Backlink(to = "feedingHabitsRecord")
    private ToMany<WeeklyFoodRecordOB> weeklyFeedingHabits;

    public FeedingHabitsRecordOB() {
        super();
    }

    public FeedingHabitsRecordOB(FeedingHabitsRecord f) {
        super(f.getId(), f.get_id(), f.getCreatedAt(), f.getPatientId());
        this.setDailyWaterGlasses(f.getDailyWaterGlasses());
        this.setSixMonthBreastFeeding(f.getSixMonthBreastFeeding());
        this.setFoodAllergyIntolerance(f.getFoodAllergyIntolerance());
        this.setBreakfastDailyFrequency(f.getBreakfastDailyFrequency());
        this.setWeeklyFeedingHabits((
                br.edu.uepb.nutes.haniot.data.Convert.listWeeklyFoodRecordToObjectBox(f.getWeeklyFeedingHabits())));
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

    public ToMany<WeeklyFoodRecordOB> getWeeklyFeedingHabits() {
        return weeklyFeedingHabits;
    }

    public void setWeeklyFeedingHabits(List<WeeklyFoodRecordOB> weeklyFeedingHabits) {
        this.weeklyFeedingHabits.clear();
        this.weeklyFeedingHabits.addAll(weeklyFeedingHabits);
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

    @Override
    public String toString() {
        return super.toString() +
                " FeedingHabitsRecordOB{" +
                ", weeklyFeedingHabits=" + weeklyFeedingHabits +
                ", dailyWaterGlasses='" + dailyWaterGlasses + '\'' +
                ", sixMonthBreastFeeding='" + sixMonthBreastFeeding + '\'' +
                ", foodAllergyIntolerance=" + foodAllergyIntolerance +
                ", breakfastDailyFrequency='" + breakfastDailyFrequency + '\'' +
                '}';
    }
}
