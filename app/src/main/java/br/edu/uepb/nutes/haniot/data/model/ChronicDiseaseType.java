package br.edu.uepb.nutes.haniot.data.model;

public class ChronicDiseaseType {

    public static class ChronicDisease {
        public static final String HYPERTENSION = "hypertension";
        public static final String BLOOD_FAT = "blood_fat";
        public static final String DIABETES = "diabetes";

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
    }

    public static class DisieaseHistory {
        public static final String YES = "yes";
        public static final String NO = "no";
        public static final String UNDEFINED = "undefined";

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
    }
}
