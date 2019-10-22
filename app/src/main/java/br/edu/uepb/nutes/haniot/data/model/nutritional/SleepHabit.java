package br.edu.uepb.nutes.haniot.data.model.nutritional;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.SleepHabitOB;

public class SleepHabit extends ActivityHabitsRecord {
    @SerializedName("week_day_sleep")
    @Expose()
    private int weekDaySleep;

    @SerializedName("week_day_wake_up")
    @Expose()
    private int weekDayWakeUp;

    public SleepHabit() { }

    public SleepHabit(SleepHabitOB s) {
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
    public static SleepHabit jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typeSleepHabits = new TypeToken<SleepHabit>() {
        }.getType();
        return gson.fromJson(json, typeSleepHabits);
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
