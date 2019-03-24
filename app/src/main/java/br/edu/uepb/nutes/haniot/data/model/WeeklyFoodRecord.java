package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class WeeklyFoodRecord {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("food")
    private String food;

    @SerializedName("seven_days_freq")
    private String seveDaysFreq;

    @Expose(serialize = false, deserialize = false)
    private ToOne<FeedingHabitsRecord> feedingHabitsRecord;

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

    public String getSeveDaysFreq() {
        return seveDaysFreq;
    }

    public void setSeveDaysFreq(String seveDaysFreq) {
        this.seveDaysFreq = seveDaysFreq;
    }

    public ToOne<FeedingHabitsRecord> getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(ToOne<FeedingHabitsRecord> feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    @Override
    public String toString() {
        return "WeeklyFoodRecord{" +
                "id=" + id +
                ", food='" + food + '\'' +
                ", seveDaysFreq='" + seveDaysFreq + '\'' +
                '}';
    }
}
