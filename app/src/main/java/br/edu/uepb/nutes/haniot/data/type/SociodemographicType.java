package br.edu.uepb.nutes.haniot.data.type;

public class SociodemographicType {

    public static class ColorRace {

        public static final String WHITE = "white";
        public static final String WHITE_PTBR = "Branca";
        public static final String BLACK = "black";
        public static final String BLACK_PTBR = "Preta/Negra";
        public static final String PARDA = "parda";
        public static final String PARDA_PTBR = "Parda";
        public static final String YELLOW = "yellow";
        public static final String YELLOW_PTBR = "Amarela";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return WHITE;
                case 1:
                    return BLACK;
                case 2:
                    return PARDA;
                case 3:
                    return YELLOW;
                default:
                    return "";
            }
        }

        public static String getStringPtBr(String type) {
            switch (type) {
                case WHITE:
                    return WHITE_PTBR;
                case BLACK:
                    return BLACK_PTBR;
                case PARDA:
                    return PARDA_PTBR;
                case YELLOW:
                    return YELLOW_PTBR;
                default:
                    return "";
            }
        }
    }

    public static class MotherScholarity {

        public static final String UNLETTERED_ELEMENTARY_ONE_INCOMPLETE = "unlettered_elementary_one_incomplete";
        public static final String UNLETTERED_ELEMENTARY_ONE_INCOMPLETE_PTBR = "Analfabeto/Fundamental I Incompleto";
        public static final String ELEMENTARY_ONE_ELEMENTARY_TWO_INCOMPLETE = "elementary_one_elementary_two_incomplete";
        public static final String ELEMENTARY_ONE_ELEMENTARY_TWO_INCOMPLETE_PTBR = "Fundamental I Completo/Fundamental II Incompleto";
        public static final String ELEMENTARY_TWO_HIGH_SCHOOL_INCOMPLETE = "elementary_two_high_school_incomplete";
        public static final String ELEMENTARY_TWO_HIGH_SCHOOL_INCOMPLETE_PTBR = "Fundamental Completo/Médio Incompleto";
        public static final String MEDIUM_GRADUATION_INCOMPLETE = "medium_graduation_incomplete";
        public static final String MEDIUM_GRADUATION_INCOMPLETE_PTBR = "Superior Incompleto";
        public static final String GRADUATION_COMPLETE = "graduation_complete";
        public static final String GRADUATION_COMPLETE_PTBR = "Superio Completo";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return UNLETTERED_ELEMENTARY_ONE_INCOMPLETE;
                case 1:
                    return ELEMENTARY_ONE_ELEMENTARY_TWO_INCOMPLETE;
                case 2:
                    return ELEMENTARY_TWO_HIGH_SCHOOL_INCOMPLETE;
                case 3:
                    return MEDIUM_GRADUATION_INCOMPLETE;
                case 4:
                    return GRADUATION_COMPLETE;
                default:
                    return "";
            }
        }

        public static String getStringPtBr(String type) {
            switch (type) {
                case UNLETTERED_ELEMENTARY_ONE_INCOMPLETE:
                    return UNLETTERED_ELEMENTARY_ONE_INCOMPLETE_PTBR;
                case ELEMENTARY_ONE_ELEMENTARY_TWO_INCOMPLETE:
                    return ELEMENTARY_ONE_ELEMENTARY_TWO_INCOMPLETE_PTBR;
                case ELEMENTARY_TWO_HIGH_SCHOOL_INCOMPLETE:
                    return ELEMENTARY_TWO_HIGH_SCHOOL_INCOMPLETE_PTBR;
                case MEDIUM_GRADUATION_INCOMPLETE:
                    return MEDIUM_GRADUATION_INCOMPLETE_PTBR;
                case GRADUATION_COMPLETE:
                    return GRADUATION_COMPLETE_PTBR;
                default:
                    return "";
            }
        }
    }
}
