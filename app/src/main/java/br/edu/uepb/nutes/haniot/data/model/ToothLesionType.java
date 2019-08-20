package br.edu.uepb.nutes.haniot.data.model;

public class ToothLesionType {

    public static class ToothType {

        public static final String DECIDUOUS_TOOTH = "deciduous_tooth";
        public static final String DECIDUOUS_TOOTH_PTBR = "dentes decíduos";
        public static final String PERMANENT_TOOTH = "permanent_tooth";
        public static final String PERMANENT_TOOTH_PTBR = "dentes permanentes";

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

        public static String getStringPtbr(String type) {
            switch (type) {
                case DECIDUOUS_TOOTH:
                    return DECIDUOUS_TOOTH_PTBR;
                case PERMANENT_TOOTH:
                    return PERMANENT_TOOTH_PTBR;
                default:
                    return "";
            }
        }
    }

    public static class LesionType {

        public static final String WHITE_SPOT_LESION = "white_spot_lesion";
        public static final String WHITE_SPOT_LESION_PTBR = "Cárie do tipo mancha branca";
        public static final String CAVITATED_LESION = "cavitated_lesion";
        public static final String CAVITATED_LESION_PTBR = "Cárie cavitada";

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

        public static String getStringPtBr(String type) {
            switch (type) {
                case WHITE_SPOT_LESION:
                    return WHITE_SPOT_LESION_PTBR;
                case CAVITATED_LESION:
                    return CAVITATED_LESION_PTBR;
                default:
                    return "";
            }
        }
    }

    public static class TeethBrushingFreq {

        public static final String NONE = "none";
        public static final String NONE_PTBR = "Nenhuma vez";
        public static final String ONCE = "once";
        public static final String ONCE_PTBR = "Uma vez";
        public static final String TWICE = "twice";
        public static final String TWICE_PTBR = "Duas vezes";
        public static final String THREE_MORE = "three_more";
        public static final String THREE_MORE_PTBR = "Três vezes";

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

        public static String getStringPtBr(String type) {
            switch (type) {
                case NONE:
                    return NONE_PTBR;
                case ONCE:
                    return ONCE_PTBR;
                case TWICE:
                    return TWICE_PTBR;
                case THREE_MORE:
                    return THREE_MORE_PTBR;
                default:
                    return "";
            }
        }
    }
}
