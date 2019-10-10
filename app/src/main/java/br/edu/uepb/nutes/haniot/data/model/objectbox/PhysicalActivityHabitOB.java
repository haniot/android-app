package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.List;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;

@Entity
public class PhysicalActivityHabitOB extends ActivityHabitsRecordOB {

    private String schoolActivityFreq;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> weeklyActivities;

    public PhysicalActivityHabitOB() {
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
//    public static PhysicalActivityHabitOB jsonDeserialize(String json) {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        Type typePhysicalActivity = new TypeToken<PhysicalActivityHabitOB>() {
//        }.getType();
//        return gson.fromJson(json, typePhysicalActivity);
//    }

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
