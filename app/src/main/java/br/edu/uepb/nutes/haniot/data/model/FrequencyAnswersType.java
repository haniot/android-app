package br.edu.uepb.nutes.haniot.data.model;

public class FrequencyAnswersType {

    public static class Frequency {

        public static final String ALMOST_NEVER = "almost_never";
        public static final String RARELY = "rarely";
        public static final String SOMETIMES = "sometimes";
        public static final String OFTEN = "often";
        public static final String ALMOST_EVERYDAY = "almost_everyday";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return ALMOST_NEVER;
                case 1:
                    return RARELY;
                case 2:
                    return SOMETIMES;
                case 3:
                    return OFTEN;
                case 4:
                    return ALMOST_EVERYDAY;
                default:
                    return "";
            }
        }
    }
}
