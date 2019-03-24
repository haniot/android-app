package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;

@Entity
public class PhysicalActivityHabit extends ActivityHabitsRecord {
    @SerializedName("school_activity_freq")
    @Expose()
    private String schoolActivityFreq;

    @SerializedName("weekly_activities")
    @Expose()
    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> weeklyActivities;

    public PhysicalActivityHabit() {
    }

    public String getSchoolActivityFreq() {
        return schoolActivityFreq;
    }

    public void setSchoolActivityFreq(String schoolActivityFreq) {
        this.schoolActivityFreq = schoolActivityFreq;
    }

    public List<String> getWeeklyActivities() {
        return weeklyActivities;
    }

    public void setWeeklyActivities(List<String> weeklyActivities) {
        this.weeklyActivities = weeklyActivities;
    }

    @Override
    public String toString() {
        return super.toString() +
                " PhysicalActivityHabit{" +
                super.toString() +
                "schoolActivityFreq='" + schoolActivityFreq + '\'' +
                ", weeklyActivities=" + weeklyActivities +
                '}';
    }
}
