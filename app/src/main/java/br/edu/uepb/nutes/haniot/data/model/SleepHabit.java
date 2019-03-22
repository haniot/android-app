package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;

@Entity
public class SleepHabit extends ActivityHabitsRecord {

    @SerializedName("week_day_sleep")
    private int weekDaySleep;

    @SerializedName("week_day_wake_up")
    private int weekDayWakeUp;

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
        return "SleepHabit{" +
                "weekDaySleep='" + weekDaySleep + '\'' +
                ", weekDayWakeUp='" + weekDayWakeUp + '\'' +
                '}';
    }
}
