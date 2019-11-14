package br.edu.uepb.nutes.haniot.data.model.nutritional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Sync;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;

public class NutritionalQuestionnaire extends Sync {

    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    private String patient_id;

    private long patientId;

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

    public NutritionalQuestionnaire(NutritionalQuestionnaireOB q) {
        super(q.isSync());
        this.setId(q.getId());
        this.set_id(q.get_id());

        this.setPatient_id(q.getPatient_id());
        this.setPatientId(q.getPatientId());

        this.setPatientId(q.getPatientId());
        this.setCreatedAt(q.getCreatedAt());
        this.setSleepHabit(Convert.convertSleepHabit(q.getSleepHabit()));
        this.setPhysicalActivityHabit(Convert.convertPhysicalActivityHabit(q.getPhysicalActivityHabit()));
        this.setFeedingHabitsRecord(Convert.convertFeedingHabitsRecord(q.getFeedingHabitsRecord()));
        this.setMedicalRecord(Convert.convertMedicalRecord(q.getMedicalRecord()));
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
                ", sleepHabit=" + sleepHabit.toString() +
                ", physicalActivityHabit=" + physicalActivityHabit.toString() +
                ", feedingHabitsRecord=" + feedingHabitsRecord.toString() +
                ", medicalRecord=" + medicalRecord.toString() +
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

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_id() {
        return this.patient_id;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }
}
