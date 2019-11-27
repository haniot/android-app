package br.edu.uepb.nutes.haniot.data.type;

import java.util.ArrayList;

public class ItemGridType {
    public static final int STEPS = 1;
    public static final int ACTIVITY = 2;
    public static final int TEMPERATURE = 3;
    public static final int WEIGHT = 4;
    public static final int BLOOD_GLUCOSE = 5;
    public static final int BLOOD_PRESSURE = 6;
    public static final int HEART_RATE = 7;
    public static final int SLEEP = 8;
    public static final int ANTHROPOMETRIC = 9;

    private ItemGridType(){

    }

    public static ArrayList<Integer> SUPPORTED_TYPES = new ArrayList<Integer>(){{
        add(STEPS);
        add(ACTIVITY);
        add(TEMPERATURE);
        add(WEIGHT);
        add(BLOOD_GLUCOSE);
        add(BLOOD_PRESSURE);
        add(HEART_RATE);
        add(SLEEP);
        add(ANTHROPOMETRIC);
    }};

    public static ArrayList<Integer> getSupportedTypes() {
        return SUPPORTED_TYPES;
    }

    public static boolean typeSupported(int type){
        return getSupportedTypes().contains(type);
    }

}
