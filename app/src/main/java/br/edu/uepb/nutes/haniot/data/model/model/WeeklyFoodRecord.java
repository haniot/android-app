package br.edu.uepb.nutes.haniot.data.model.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class WeeklyFoodRecord {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("food")
    @Expose
    private String food;

    @SerializedName("seven_days_freq")
    @Expose
    private String sevenDaysFreq;

    @Expose(serialize = false, deserialize = false)
    private FeedingHabitsRecord feedingHabitsRecord;

    public WeeklyFoodRecord() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getSevenDaysFreq() {
        return sevenDaysFreq;
    }

    public void setSevenDaysFreq(String sevenDaysFreq) {
        this.sevenDaysFreq = sevenDaysFreq;
    }

    public FeedingHabitsRecord getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        Log.i("AAAAAAAAAA", a);
        return a;
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PatientOB
     */
    public static WeeklyFoodRecord jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeWeeklyFoodRecord = new TypeToken<WeeklyFoodRecord>() {
        }.getType();
        return gson.fromJson(json, typeWeeklyFoodRecord);
    }


    @Override
    public String toString() {
        return "WeeklyFoodRecordOB{" +
                "id=" + id +
                ", food='" + food + '\'' +
                ", sevenDaysFreq='" + sevenDaysFreq + '\'' +
                '}';
    }
}
