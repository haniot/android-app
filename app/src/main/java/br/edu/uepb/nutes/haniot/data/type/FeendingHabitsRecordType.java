package br.edu.uepb.nutes.haniot.data.type;

public class FeendingHabitsRecordType {
    public static class OneDayFeedingAmount {
        public static final String NONE = "none";
        public static final String ONE_TWO = "one_two";
        public static final String THREE_FOUR = "three_four";
        public static final String FIVE_MORE = "five_more";
        public static final String UNDEFINED = "undefined";

        public static final String NONE_PTBR = "Não";
        public static final String ONE_TWO_PTBR = "Uma ou duas";
        public static final String THREE_FOUR_PTBR = "Três ou quatro";
        public static final String FIVE_MORE_PTBR = "Cinco ou mais";
        public static final String UNDEFINED_PTBR = "Não sei";


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

        public static String getStringPTBR(String type) {
            switch (type) {
                case NONE:
                    return NONE_PTBR;
                case ONE_TWO:
                    return ONE_TWO_PTBR;
                case THREE_FOUR:
                    return THREE_FOUR_PTBR;
                case FIVE_MORE:
                    return FIVE_MORE_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
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

        public static final String NEVER_PTBR = "Nunca";
        public static final String SOMETIMES_PTBR = "As vezes";
        public static final String ALMOST_EVERYDAY_PTBR = "Quase todos os dias";
        public static final String EVERYDAY_PTBR = "Todos os dias";
        public static final String UNDEFINED_PTBR = "Não sei";

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

        public static String getStringPTBR(String type) {
            switch (type) {
                case NEVER:
                    return NEVER_PTBR;
                case SOMETIMES:
                    return SOMETIMES_PTBR;
                case ALMOST_EVERYDAY:
                    return ALMOST_EVERYDAY_PTBR;
                case EVERYDAY:
                    return EVERYDAY_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
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

        public static final String NEVER_PTBR = "Nunca";
        public static final String NO_DAY_PTBR = "Nenhum dia";
        public static final String ONE_TWO_DAYS_PTBR = "1 a 2 dias";
        public static final String THREE_FOUR_DAYS_PTBR = "3 a 4 dias";
        public static final String FIVE_SIX_DAYS_PTBR = "5 a 6 dias";
        public static final String ALL_DAYS_PTBR = "Todos os dias";
        public static final String UNDEFINED_PTBR = "Não sei";

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

        public static String getStringPTBR(String type) {
            switch (type) {
                case NEVER:
                    return NEVER_PTBR;
                case NO_DAY:
                    return NO_DAY_PTBR;
                case ONE_TWO_DAYS:
                    return ONE_TWO_DAYS_PTBR;
                case THREE_FOUR_DAYS:
                    return THREE_FOUR_DAYS_PTBR;
                case FIVE_SIX_DAYS:
                    return FIVE_SIX_DAYS_PTBR;
                case ALL_DAYS:
                    return ALL_DAYS_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
                default:
                    return "";
            }
        }
    }

    public static class BreastFeeding {
        static final String EXCLUSIVE = "exclusive";
        static final String COMPLEMENTARY = "complementary";
        static final String INFANT_FORMULAS = "infant_formulas";
        static final String OTHER = "other";
        static final String UNDEFINED = "undefined";

        static final String EXCLUSIVE_PTBR = "Exclusiva";
        static final String COMPLEMENTARY_PTBR = "Complementar";
        static final String INFANT_FORMULAS_PTBR = "Fórmula infantil";
        static final String OTHER_PTBR = "Outros";
        static final String UNDEFINED_PTBR = "Não sei";

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

        public static String getStringPTBR(String type) {
            switch (type) {
                case EXCLUSIVE:
                    return EXCLUSIVE_PTBR;
                case COMPLEMENTARY:
                    return COMPLEMENTARY_PTBR;
                case INFANT_FORMULAS:
                    return INFANT_FORMULAS_PTBR;
                case OTHER:
                    return OTHER_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
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

        public static final String GLUTEN_PTBR = "Glúten";
        public static final String APLV_PTBR = "Alergia à proteína";
        public static final String LACTOSE_PTBR = "Lactose";
        public static final String DYE_PTBR = "Corante";
        public static final String EGG_PTBR = "Ovo";
        public static final String PEANUT_PTBR = "Amendoín";
        public static final String OTHER_PTBR = "Outros";
        public static final String UNDEFINED_PTBR = "Não sei";


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

        public static String getStringPTBR(String type) {
            switch (type) {
                case GLUTEN:
                    return GLUTEN_PTBR;
                case APLV:
                    return APLV_PTBR;
                case LACTOSE:
                    return LACTOSE_PTBR;
                case DYE:
                    return DYE_PTBR;
                case EGG:
                    return EGG_PTBR;
                case PEANUT:
                    return PEANUT_PTBR;
                case OTHER:
                    return OTHER_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
                default:
                    return "";
            }
        }
    }
}
