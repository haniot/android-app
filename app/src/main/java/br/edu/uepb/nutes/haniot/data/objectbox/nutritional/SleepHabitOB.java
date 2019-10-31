package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import io.objectbox.annotation.Entity;

@Entity
public class SleepHabitOB extends ActivityHabitsRecordOB {

    private int weekDaySleep;

    private int weekDayWakeUp;

    public SleepHabitOB() {
        super();
    }

    public SleepHabitOB(SleepHabit s) {
        super(s.getId(), s.get_id(), s.getCreatedAt(), s.getPatientId());
        this.setWeekDaySleep(s.getWeekDaySleep());
        this.setWeekDayWakeUp(s.getWeekDayWakeUp());
    }

    public int getWeekDaySleep() {
        return weekDaySleep;
    }

    public void setWeekDaySleep(int weekDaySleep) {
        this.weekDaySleep = weekDaySleep;
    }

    public int getWeekDayWakeUp() {
        return weekDayWakeUp;
    }

    public void setWeekDayWakeUp(int weekDayWakeUp) {
        this.weekDayWakeUp = weekDayWakeUp;
    }

    @Override
    public String toString() {
        return super.toString() +
                " SleepHabitOB{" +
                "weekDaySleep=" + weekDaySleep +
                ", weekDayWakeUp=" + weekDayWakeUp +
                '}';
    }
}
