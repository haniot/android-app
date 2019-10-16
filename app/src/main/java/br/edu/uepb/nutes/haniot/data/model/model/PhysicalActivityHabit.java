package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.objectbox.PhysicalActivityHabitOB;
import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;

public class PhysicalActivityHabit extends ActivityHabitsRecord {
    @SerializedName("school_activity_freq")
    @Expose()
    private String schoolActivityFreq;

    @SerializedName("weekly_activities")
    @Expose()
    private List<String> weeklyActivities;

    public PhysicalActivityHabit() { }

    public PhysicalActivityHabit(PhysicalActivityHabitOB p) {
        super(p.getId(), p.get_id(), p.getCreatedAt(), p.getPatientId());
        this.setSchoolActivityFreq(p.getSchoolActivityFreq());
        this.setWeeklyActivities(p.getWeeklyActivities());
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
    public static PhysicalActivityHabit jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typePhysicalActivity = new TypeToken<PhysicalActivityHabit>() {
        }.getType();
        return gson.fromJson(json, typePhysicalActivity);
    }

    @Override
    public String toString() {
        return super.toString() +
                " PhysicalActivityHabitOB{" +
                super.toString() +
                "schoolActivityFreq='" + schoolActivityFreq + '\'' +
                ", weeklyActivities=" + weeklyActivities +
                '}';
    }
}
