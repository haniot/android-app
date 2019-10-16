package br.edu.uepb.nutes.haniot.data.model.objectbox;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class WeeklyFoodRecordOB {
    @Id
    private long id;

    private String food;

    private String sevenDaysFreq;

    private ToOne<FeedingHabitsRecordOB> feedingHabitsRecord;

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

    public ToOne<FeedingHabitsRecordOB> getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(ToOne<FeedingHabitsRecordOB> feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
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
