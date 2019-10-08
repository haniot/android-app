package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class NutritionalQuestionnaire {

    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    @SerializedName("created_at")
    @Expose()
    String createdAt;

    @SerializedName("sleep_habit")
    @Expose()
    SleepHabit sleepHabit;

    @SerializedName("physical_activity_habits")
    @Expose()
    PhysicalActivityHabit physicalActivityHabit;

    @SerializedName("feeding_habits_record")
    @Expose()
    FeedingHabitsRecord feedingHabitsRecord;

    @SerializedName("medical_record")
    @Expose()
    MedicalRecord medicalRecord;

    public NutritionalQuestionnaire() {
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

    public SleepHabit getSleepHabit() {
        return sleepHabit;
    }

    public void setSleepHabit(SleepHabit sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public PhysicalActivityHabit getPhysicalActivityHabit() {
        return physicalActivityHabit;
    }

    public void setPhysicalActivityHabit(PhysicalActivityHabit physicalActivityHabit) {
        this.physicalActivityHabit = physicalActivityHabit;
    }

    public FeedingHabitsRecord getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @Override
    public String toString() {
        return "NutritionalQuestionnaire{" +
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
        NutritionalQuestionnaire that = (NutritionalQuestionnaire) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(createdAt, that.createdAt);
    }
}
