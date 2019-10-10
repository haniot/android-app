package br.edu.uepb.nutes.haniot.data.model.objectbox;

import io.objectbox.annotation.Entity;

@Entity
public class SleepHabitOB extends ActivityHabitsRecordOB {

    private int weekDaySleep;

    private int weekDayWakeUp;

    public SleepHabitOB() {
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

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        String a = gson.toJson(this);
//        Log.i("AAAAAAAAAA", a);
//        return a;
//    }
//
//    /**
//     * Convert json to Object.
//     *
//     * @param json String
//     * @return PatientOB
//     */
//    public static SleepHabitOB jsonDeserialize(String json) {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        Type typeSleepHabits = new TypeToken<SleepHabitOB>() {
//        }.getType();
//        return gson.fromJson(json, typeSleepHabits);
//    }

    @Override
    public String toString() {
        return super.toString() +
                " SleepHabitOB{" +
                "weekDaySleep=" + weekDaySleep +
                ", weekDayWakeUp=" + weekDayWakeUp +
                '}';
    }
}
