package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.Objects;

import io.objectbox.annotation.Id;

public class NutritionalQuestionnaireOB {

    @Id
    private long id;

    private String _id;

    String createdAt;

    SleepHabitOB sleepHabit;

    PhysicalActivityHabitOB physicalActivityHabit;

    FeedingHabitsRecordOB feedingHabitsRecord;

    MedicalRecordOB medicalRecord;

    public NutritionalQuestionnaireOB() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SleepHabitOB getSleepHabit() {
        return sleepHabit;
    }

    public void setSleepHabit(SleepHabitOB sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public PhysicalActivityHabitOB getPhysicalActivityHabit() {
        return physicalActivityHabit;
    }

    public void setPhysicalActivityHabit(PhysicalActivityHabitOB physicalActivityHabit) {
        this.physicalActivityHabit = physicalActivityHabit;
    }

    public FeedingHabitsRecordOB getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecordOB feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    public MedicalRecordOB getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecordOB medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        return gson.toJson(this);
//    }

    @Override
    public String toString() {
        return "NutritionalQuestionnaireOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", sleepHabit=" + sleepHabit +
                ", physicalActivityHabit=" + physicalActivityHabit +
                ", feedingHabitsRecord=" + feedingHabitsRecord +
                ", medicalRecord=" + medicalRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NutritionalQuestionnaireOB that = (NutritionalQuestionnaireOB) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(createdAt, that.createdAt);
    }
}
