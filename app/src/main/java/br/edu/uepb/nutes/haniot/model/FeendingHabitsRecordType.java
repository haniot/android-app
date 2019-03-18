package br.edu.uepb.nutes.haniot.model;

public class FeendingHabitsRecordType {
    public static class OneDayFeedingAmount {
        public static final String NONE = "none";
        public static final String ONE_TWO = "one_two";
        public static final String THREE_FOUR = "three_four";
        public static final String FIVE_MORE = "five_more";
        public static final String UNDEFINED = "undefined";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return NONE;
                case 1:
                    return ONE_TWO;
                case 2:
                    return THREE_FOUR;
                case 3:
                    return FIVE_MORE;
                case 4:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }

    public static class DailyFeedingFrequency {
        public static final String NEVER = "never";
        public static final String SOMETIMES = "sometimes";
        public static final String ALMOST_EVERYDAY = "almost_everyday";
        public static final String EVERYDAY = "everyday";
        public static final String UNDEFINED = "undefined";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return NEVER;
                case 1:
                    return SOMETIMES;
                case 2:
                    return ALMOST_EVERYDAY;
                case 3:
                    return EVERYDAY;
                case 4:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }

    public static class SevenDaysFeedingFrequency {
        public static final String NEVER = "never";
        public static final String NO_DAY = "no_day";
        public static final String ONE_TWO_DAYS = "one_two_days";
        public static final String THREE_FOUR_DAYS = "three_four_days";
        public static final String FIVE_SIX_DAYS = "five_six_days";
        public static final String ALL_DAYS = "all_days";
        public static final String UNDEFINED = "undefined";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return NEVER;
                case 1:
                    return NO_DAY;
                case 2:
                    return ONE_TWO_DAYS;
                case 3:
                    return THREE_FOUR_DAYS;
                case 4:
                    return FIVE_SIX_DAYS;
                case 5:
                    return ALL_DAYS;
                case 6:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }

    public static class BreastFeeding {
        public static final String EXCLUSIVE = "exclusive";
        public static final String COMPLEMENTARY = "complementary";
        public static final String INFANT_FORMULAS = "infant_formulas";
        public static final String OTHER = "other";
        public static final String UNDEFINED = "undefined";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return EXCLUSIVE;
                case 1:
                    return COMPLEMENTARY;
                case 2:
                    return INFANT_FORMULAS;
                case 3:
                    return OTHER;
                case 4:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }

    public static class FoodAllergyStringolerance {
        public static final String GLUTEN = "gluten";
        public static final String APLV = "aplv";
        public static final String LACTOSE = "lactose";
        public static final String DYE = "dye";
        public static final String EGG = "egg";
        public static final String PEANUT = "peanut";
        public static final String OTHER = "other";
        public static final String UNDEFINED = "undefined";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return GLUTEN;
                case 1:
                    return APLV;
                case 2:
                    return LACTOSE;
                case 3:
                    return DYE;
                case 4:
                    return EGG;
                case 5:
                    return PEANUT;
                case 6:
                    return OTHER;
                case 7:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }
}
