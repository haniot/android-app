package br.edu.uepb.nutes.haniot.model;

public class FeendingHabitsRecordType {
    class OneDayFeedingAmount {
        public static final int NONE = 1;
        public static final int ONE_TWO = 2;
        public static final int THREE_FOUR = 3;
        public static final int FIVE_MORE = 4;
        public static final int UNDEFINED = 5;
    }

    class DailyFeedingFrequency {
        public static final int NEVER = 1;
        public static final int SOMETIMES = 2;
        public static final int ALMOST_EVERYDAY = 3;
        public static final int EVERYDAY = 4;
        public static final int UNDEFINED = 5;
    }

    class SevenDaysFeedingFrequency {
        public static final int NEVER = 1;
        public static final int NO_DAY = 2;
        public static final int ONE_TWO_DAYS = 3;
        public static final int THREE_FOUR_DAYS = 4;
        public static final int FIVE_SIX_DAYS = 5;
        public static final int ALL_DAYS = 6;
        public static final int UNDEFINED = 7;
    }

    class BreastFeeding {
        public static final int EXCLUSIVE = 1;
        public static final int COMPLEMENTARY = 2;
        public static final int INFANT_FORMULAS = 3;
        public static final int OTHER = 4;
        public static final int UNDEFINED = 5;
    }

    class FoodAllergyIntolerance {
        public static final int GLUTEN = 1;
        public static final int APLV = 2;
        public static final int LACTOSE = 3;
        public static final int DYE = 4;
        public static final int EGG = 5;
        public static final int PEANUT = 6;
        public static final int OTHER = 7;
        public static final int UNDEFINED = 8;
    }


}
