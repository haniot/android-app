package br.edu.uepb.nutes.haniot.data.model;

public class FrequencyAnswersType {

    public static class Frequency {

        public static final String ALMOST_NEVER = "almost_never";
        public static final String RARELY = "rarely";
        public static final String SOMETIMES = "sometimes";
        public static final String OFTEN = "often";
        public static final String ALMOST_ALWAYS = "almost_always";

        public static final String ALMOST_NEVER_PTBR = "Quase nunca";
        public static final String RARELY_PTBR = "Raramente";
        public static final String SOMETIMES_PTBR = "As vezes";
        public static final String OFTEN_PTBR = "Frequentemente";
        public static final String ALMOST_EVERYDAY_PTBR = "Quase todos os dias";

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
                    return ALMOST_ALWAYS;
                default:
                    return "";
            }
        }

        public static String getStringPTBR(String type) {
            switch (type) {
                case ALMOST_NEVER:
                    return ALMOST_NEVER_PTBR;
                case RARELY:
                    return RARELY_PTBR;
                case SOMETIMES:
                    return SOMETIMES_PTBR;
                case OFTEN:
                    return OFTEN_PTBR;
                case ALMOST_ALWAYS:
                    return ALMOST_EVERYDAY_PTBR;
                default:
                    return "";
            }
        }
    }
}
