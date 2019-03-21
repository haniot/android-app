package br.edu.uepb.nutes.haniot.data.model;

import io.objectbox.annotation.Entity;

@Entity
public class SleepHabit extends ActivityHabitsRecord {

    private String weekDaySleep;
    private String weekDayWakeUp;

    public String getWeekDaySleep() {
        return weekDaySleep;
    }

    public void setWeekDaySleep(String weekDaySleep) {
        this.weekDaySleep = weekDaySleep;
    }

    public String getWeekDayWakeUp() {
        return weekDayWakeUp;
    }

    public void setWeekDayWakeUp(String weekDayWakeUp) {
        this.weekDayWakeUp = weekDayWakeUp;
    }

    @Override
    public String toString() {
        return "SleepHabit{" +
                "weekDaySleep='" + weekDaySleep + '\'' +
                ", weekDayWakeUp='" + weekDayWakeUp + '\'' +
                '}';
    }
}
