package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import br.edu.uepb.nutes.haniot.R;

public class SportsType {

    public static final String SOCCER = "football";
    public static final String FUTSAL = "futsal";
    public static final String HANDBALL = "handball";
    public static final String BASKETBALL = "basketball";
    public static final String SKATES = "roller_skate";
    public static final String ATHLETICS = "athletics";
    public static final String SWIM = "swimming";
    public static final String GYMNASTICS = "olympic_rhythmic_gymnastics";
    public static final String FIGHT = "fight";
    public static final String DANCE = "dance";
    public static final String RUN = "run";
    public static final String RIDE_A_BIKE = "bike";
    public static final String WALKING_AS_A_PHYSICAL_EXERCISE = "exercise_walking";
    public static final String WALKING_AS_A_MEANS_OF_TRANSPORT = "locomotion_walking";
    public static final String VOLLEYBALL = "volleyball";
    public static final String MUSCULATION = "bodybuilding";
    public static final String ABDOMINAL_EXERCISE = "abdominal";
    public static final String TENNIS = "tennis";
    public static final String WALK_WITH_DOG = "dog_walk";
    public static final String GYMNASTICS_GYM = "gym_exercise";
    public static final String NO_ACTIVITY = "none_activity";

    public static String getString(int type) {
        switch (type) {
            case 0:
                return SOCCER;
            case 1:
                return FUTSAL;
            case 2:
                return HANDBALL;
            case 3:
                return BASKETBALL;
            case 4:
                return SKATES;
            case 5:
                return ATHLETICS;
            case 6:
                return SWIM;
            case 7:
                return GYMNASTICS;
            case 8:
                return FIGHT;
            case 9:
                return DANCE;
            case 10:
                return RUN;
            case 11:
                return RIDE_A_BIKE;
            case 12:
                return WALKING_AS_A_PHYSICAL_EXERCISE;
            case 13:
                return WALKING_AS_A_MEANS_OF_TRANSPORT;
            case 14:
                return VOLLEYBALL;
            case 15:
                return MUSCULATION;
            case 16:
                return ABDOMINAL_EXERCISE;
            case 17:
                return TENNIS;
            case 18:
                return WALK_WITH_DOG;
            case 19:
                return GYMNASTICS_GYM;
            case 20:
                return NO_ACTIVITY;
            default:
                return "";
        }
    }

    public static String getStringPtBr(Context context, String type) {
        String[] answers = context.getResources().getStringArray(R.array.sports_answers);
        switch (type) {
            case SOCCER:
                return answers[0];
            case FUTSAL:
                return answers[1];
            case HANDBALL:
                return answers[2];
            case BASKETBALL:
                return answers[3];
            case SKATES:
                return answers[4];
            case ATHLETICS:
                return answers[5];
            case SWIM:
                return answers[6];
            case GYMNASTICS:
                return answers[7];
            case FIGHT:
                return answers[8];
            case DANCE:
                return answers[9];
            case RUN:
                return answers[10];
            case RIDE_A_BIKE:
                return answers[11];
            case WALKING_AS_A_PHYSICAL_EXERCISE:
                return answers[12];
            case WALKING_AS_A_MEANS_OF_TRANSPORT:
                return answers[13];
            case VOLLEYBALL:
                return answers[14];
            case MUSCULATION:
                return answers[15];
            case ABDOMINAL_EXERCISE:
                return answers[16];
            case TENNIS:
                return answers[17];
            case WALK_WITH_DOG:
                return answers[18];
            case GYMNASTICS_GYM:
                return answers[19];
            case NO_ACTIVITY:
                return answers[20];
            default:
                return "";
        }
    }
}
