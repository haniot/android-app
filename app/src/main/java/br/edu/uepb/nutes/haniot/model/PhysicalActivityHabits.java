package br.edu.uepb.nutes.haniot.model;

import java.util.List;

public class PhysicalActivityHabits extends ActivityHabitsRecord {

    private String schoolActivityFreq;
    private List<String> weeklyActivities;

    public String getSchoolActivityFreq() {
        return schoolActivityFreq;
    }

    public void setSchoolActivityFreq(String schoolActivityFreq) {
        this.schoolActivityFreq = schoolActivityFreq;
    }

    public List<String> getWeeklyActivities() {
        return weeklyActivities;
    }

    public void setWeeklyActivities(List<String> weeklyActivities) {
        this.weeklyActivities = weeklyActivities;
    }
}
