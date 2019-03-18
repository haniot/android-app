package br.edu.uepb.nutes.haniot.model;

import java.util.List;

public class PhysicalActivityHabits extends ActivityHabitsRecord {

    private int schoolActivityFreq;
    private List<String> weeklyActivities;

    final static int ONE_PER_WEEK = 0;
    final static int TWO_PER_WEEK = 1;
    final static int THREE_PER_WEEK = 2;
    final static int FOUR_MORE_PER_WEEK = 3;
    final static int NONE = 4;
}
