package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;

@Entity
public class PhysicalActivityHabitOB extends ActivityHabitsRecordOB {

    private String schoolActivityFreq;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> weeklyActivities;

    public PhysicalActivityHabitOB() {
        super();
    }

    public PhysicalActivityHabitOB(PhysicalActivityHabit p) {
        super(p.getId(), p.get_id());
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
