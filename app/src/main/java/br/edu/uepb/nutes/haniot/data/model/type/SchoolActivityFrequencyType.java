package br.edu.uepb.nutes.haniot.data.model.type;

public class SchoolActivityFrequencyType {

    public final static String ONE_PER_WEEK = "one_per_week";
    public final static String TWO_PER_WEEK = "two_per_week";
    public final static String THREE_PER_WEEK = "three_per_week";
    public final static String FOUR_MORE_PER_WEEK = "four_more_per_week";
    public final static String NONE = "none";

    public final static String ONE_PER_WEEK_PTBR = "Uma vez por semana";
    public final static String TWO_PER_WEEK_PTBR = "Duas vezes por semana";
    public final static String THREE_PER_WEEK_PTBR = "Três vezes por semana";
    public final static String FOUR_MORE_PER_WEEK_PTBR = "Quatro ou mais vezes por semana";
    public final static String NONE_PTBR = "Nenhuma";

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

    public static String getStringPTBR(String type) {
        switch (type) {
            case ONE_PER_WEEK:
                return ONE_PER_WEEK_PTBR;
            case TWO_PER_WEEK:
                return TWO_PER_WEEK_PTBR;
            case THREE_PER_WEEK:
                return THREE_PER_WEEK_PTBR;
            case FOUR_MORE_PER_WEEK:
                return FOUR_MORE_PER_WEEK_PTBR;
            case NONE:
                return NONE_PTBR;
            default:
                return "";
        }
    }
}
