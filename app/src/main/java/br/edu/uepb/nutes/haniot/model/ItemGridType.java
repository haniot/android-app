package br.edu.uepb.nutes.haniot.model;

public class ItemGridType {
    public static final int STEPS = 1;
    public static final int ACTIVITY = 2;
    public static final int TEMPERATURE = 3;
    public static final int WEIGHT = 4;
    public static final int BLOOD_GLUCOSE = 5;
    public static final int BLOOD_PRESSURE = 6;
    public static final int HEART_RATE = 7;

    public static int[] SUPPORTED_TYPES = {
            STEPS,
            ACTIVITY,
            TEMPERATURE,
            WEIGHT,
            TEMPERATURE,
            BLOOD_GLUCOSE,
            BLOOD_PRESSURE
    };
}
