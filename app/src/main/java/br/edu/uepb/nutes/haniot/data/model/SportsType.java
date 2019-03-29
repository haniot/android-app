package br.edu.uepb.nutes.haniot.data.model;

public class SportsType {

    public static final String SOCCER = "soccer";
    public static final String FUTSAL = "futsal";
    public static final String HANDBALL = "handball";
    public static final String SKATES = "skates";
    public static final String ATHLETICS = "athletics";
    public static final String SWIM = "swim";
    public static final String GYMNASTICS = "gymnastics";
    public static final String FIGHT = "fight";
    public static final String DANCE = "dance";
    public static final String RUN = "run";
    public static final String RIDE_A_BIKE = "ride a bike";
    public static final String WALKING_AS_A_PHYSICAL_EXERCISE = "walking as a physical exercise";
    public static final String WALKING_AS_A_MEANS_OF_TRANSPORT = "walking as a means of transport";
    public static final String VOLLEYBALL = "volleyball";
    public static final String MUSCULATION = "musculation";
    public static final String ABDOMINAL_EXERCISE = "abdominal exercise";
    public static final String TENNIS = "tennis";
    public static final String WALK_WITH_DOG = "walk with dog";
    public static final String GYMNASTICS_GYM = "gymnastics gym";
    public static final String NO_ACTIVITY = "no activity";

    public static String getString(int type) {
        switch (type) {
            case 0:
                return SOCCER;
            case 1:
                return FUTSAL;
            case 2:
                return HANDBALL;
            case 3:
                return SKATES;
            case 4:
                return ATHLETICS;
            case 5:
                return SWIM;
            case 6:
                return GYMNASTICS;
            case 7:
                return FIGHT;
            case 8:
                return DANCE;
            case 9:
                return RUN;
            case 10:
                return RIDE_A_BIKE;
            case 11:
                return WALKING_AS_A_PHYSICAL_EXERCISE;
            case 12:
                return WALKING_AS_A_MEANS_OF_TRANSPORT;
            case 13:
                return VOLLEYBALL;
            case 14:
                return MUSCULATION;
            case 15:
                return ABDOMINAL_EXERCISE;
            case 16:
                return TENNIS;
            case 17:
                return WALK_WITH_DOG;
            case 18:
                return GYMNASTICS_GYM;
            case 19:
                return NO_ACTIVITY;
            default:
                return "";
        }
    }
}
