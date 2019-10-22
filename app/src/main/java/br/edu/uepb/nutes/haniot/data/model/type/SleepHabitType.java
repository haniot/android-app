package br.edu.uepb.nutes.haniot.data.model.type;

import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;

public class SleepHabitType extends ActivityHabitsRecord {

    public final static String ONE_PER_WEEK = "one_per_week";
    public final static String TWO_PER_WEEK = "two_per_week";
    public final static String THREE_PER_WEEK = "trhee_per_week";
    public final static String FOUR_MORE_PER_WEEK = "four_more_per_week";
    public final static String NONE = "none";

    public static String getString(int type) {
        switch (type) {
            case 0:
                return ONE_PER_WEEK;
            case 1:
                return TWO_PER_WEEK;
            case 2:
                return THREE_PER_WEEK;
            case 3:
                return FOUR_MORE_PER_WEEK;
            case 4:
                return NONE;
            default:
                return "";
        }
    }
}
