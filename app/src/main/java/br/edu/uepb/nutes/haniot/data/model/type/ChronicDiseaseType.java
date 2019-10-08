package br.edu.uepb.nutes.haniot.data.model.type;

public class ChronicDiseaseType {

    public static class ChronicDisease {
        public static final String HYPERTENSION = "hypertension";
        public static final String BLOOD_FAT = "blood_fat";
        public static final String DIABETES = "diabetes";

        public static final String HYPERTENSION_PTBR = "Hipertensão";
        public static final String BLOOD_FAT_PTBR = "Gordura Corporal";
        public static final String DIABETES_PTBR = "Diabetes";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return HYPERTENSION;
                case 1:
                    return BLOOD_FAT;
                case 2:
                    return DIABETES;
                default:
                    return "";
            }
        }

        public static String getString_PTBR(String type) {
            switch (type) {
                case HYPERTENSION:
                    return HYPERTENSION_PTBR;
                case BLOOD_FAT:
                    return BLOOD_FAT_PTBR;
                case DIABETES:
                    return DIABETES_PTBR;
                default:
                    return "";
            }
        }
    }

    public static class DisieaseHistory {
        public static final String YES = "yes";
        public static final String NO = "no";
        public static final String UNDEFINED = "undefined";
        public static final String YES_PTBR = "Sim";
        public static final String NO_PTBR = "Não";
        public static final String UNDEFINED_PTBR = "Não sei";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return YES;
                case 1:
                    return NO;
                case 2:
                    return UNDEFINED;
                default:
                    return "";
            }
        }

        public static String getStringPTBR(String type) {
            switch (type) {
                case YES:
                    return YES_PTBR;
                case NO:
                    return NO_PTBR;
                case UNDEFINED:
                    return UNDEFINED_PTBR;
                default:
                    return "";
            }
        }
    }
}
