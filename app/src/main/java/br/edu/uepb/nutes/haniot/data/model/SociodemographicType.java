package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Id;

public class SociodemographicType {

    public static class ColorRace {

        public static final String WHITE = "white";
        public static final String BLACK = "black";
        public static final String PARDA = "parda";
        public static final String YELLOW = "yellow";


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
    }

    public static class MotherScholarity {

        public static final String UNLETTERED = "unlettered";
        public static final String ONE_TO_TRHEE = "elementary_1_to_3";
        public static final String FOUR_TO_SEVEN = "elementary_4_to_7";
        public static final String ELEMENTARY_COMPLETE = "elementary_complete";
        public static final String HIGH_SCHOOL_INCOMPLETE = "high_school_incomplete";
        public static final String HIGH_SCHOOL_COMPLETE = "high_school_complete";
        public static final String UNDEFINED = "undefined ";


        public static String getString(int type) {
            switch (type) {
                case 0:
                    return UNLETTERED;
                case 1:
                    return ONE_TO_TRHEE;
                case 2:
                    return FOUR_TO_SEVEN;
                case 3:
                    return ELEMENTARY_COMPLETE;
                case 4:
                    return HIGH_SCHOOL_INCOMPLETE;
                case 5:
                    return HIGH_SCHOOL_COMPLETE;
                case 6:
                    return UNDEFINED;
                default:
                    return "";
            }
        }
    }
}
