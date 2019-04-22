package br.edu.uepb.nutes.haniot.data.model;

public class ToothLesionType {

    public static class ToothType {

        public static final String DECIDUOUS_TOOTH = "deciduous_tooth";
        public static final String PERMANENT_TOOTH = "permanent_tooth ";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return DECIDUOUS_TOOTH;
                case 1:
                    return PERMANENT_TOOTH;
                default:
                    return "";
            }
        }
    }

    public static class LesionType {

        public static final String WHITE_SPOT_LESION = "white_spot_lesion";
        public static final String CAVITATED_LESION = "cavitated_lesion ";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return WHITE_SPOT_LESION;
                case 1:
                    return CAVITATED_LESION;
                default:
                    return "";
            }
        }
    }

    public static class TeethBrushingFreq {

        public static final String NONE = "none";
        public static final String ONCE = "once";
        public static final String TWICE = "twice";
        public static final String THREE_MORE = "three_more";

        public static String getString(int type) {
            switch (type) {
                case 0:
                    return NONE;
                case 1:
                    return ONCE;
                case 2:
                    return TWICE;
                case 3:
                    return THREE_MORE;
                default:
                    return "";
            }
        }
    }
}
