package br.edu.uepb.nutes.haniot.data.objectbox.nutritional;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.objectbox.SyncOB;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

@Entity
public class NutritionalQuestionnaireOB extends SyncOB {

    @Id
    private long id;

    @Index
    private String _id;

    String patientId;

    String createdAt;

    ToOne<SleepHabitOB> sleepHabit;

    ToOne<PhysicalActivityHabitOB> physicalActivityHabit;

    ToOne<FeedingHabitsRecordOB> feedingHabitsRecord;

    ToOne<MedicalRecordOB> medicalRecord;

    public NutritionalQuestionnaireOB() {
        super();
    }

    public NutritionalQuestionnaireOB(NutritionalQuestionnaire n) {
        super(n.isSync());
        this.setId(n.getId());
        this.set_id(n.get_id());
        this.setPatientId(n.getPatientId());
        this.setCreatedAt(n.getCreatedAt());
        this.setSleepHabit(Convert.convertSleepHabit(n.getSleepHabit()));
        this.setPhysicalActivityHabit(Convert.convertPhysicalActivityHabit(n.getPhysicalActivityHabit()));
        this.setFeedingHabitsRecord(Convert.convertFeedingHabitsRecord(n.getFeedingHabitsRecord()));
        this.setMedicalRecord(Convert.convertMedicalRecord(n.getMedicalRecord()));
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

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientId() {
        return this.patientId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public SleepHabitOB getSleepHabit() {
        return sleepHabit.getTarget();
    }

    public void setSleepHabit(SleepHabitOB sleepHabit) {
        this.sleepHabit.setTarget(sleepHabit);
    }

    public PhysicalActivityHabitOB getPhysicalActivityHabit() {
        return physicalActivityHabit.getTarget();
    }

    public void setPhysicalActivityHabit(PhysicalActivityHabitOB physicalActivityHabit) {
        this.physicalActivityHabit.setTarget(physicalActivityHabit);
    }

    public FeedingHabitsRecordOB getFeedingHabitsRecord() {
        return feedingHabitsRecord.getTarget();
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecordOB feedingHabitsRecord) {
        this.feedingHabitsRecord.setTarget(feedingHabitsRecord);
    }

    public MedicalRecordOB getMedicalRecord() {
        return medicalRecord.getTarget();
    }

    public void setMedicalRecord(MedicalRecordOB medicalRecord) {
        this.medicalRecord.setTarget(medicalRecord);
    }

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
