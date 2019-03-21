package br.edu.uepb.nutes.haniot.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Patient {

    @Id
    @Expose(serialize = false)
    private long id;

    @Index
    @SerializedName("id")
    private String _id;
    @Index
    @SerializedName("pilotId")
    private String pilotId;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("gender")
    private String gender;
    @SerializedName("birthDate")
    private String birthDate;


    private ToOne<SleepHabit> sleepHabit;
    private ToOne<PhysicalActivityHabit> physicalActivityHabits;
    private ToOne<FeedingHabitsRecord> feedingHabitsRecord;
    private ToOne<MedicalRecord> medicalRecord;

    /**
     * RELATIONS
     */

    /**
     * {@link Measurement}
     */
    @Backlink(to = "children")
    ToMany<Measurement> measurements;

    public Patient() {

    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
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

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setSleepHabit(SleepHabit sleepHabit) {
        this.sleepHabit.setTarget(sleepHabit);
    }

    public ToOne<FeedingHabitsRecord> getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        this.feedingHabitsRecord.setTarget(feedingHabitsRecord);
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord.setTarget(medicalRecord);
    }

    public void setSleepHabit(ToOne<SleepHabit> sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public ToOne<SleepHabit> getSleepHabit() {
        return sleepHabit;
    }

    public ToOne<MedicalRecord> getMedicalRecord() {
        return medicalRecord;
    }

    public void setFeedingHabitsRecord(ToOne<FeedingHabitsRecord> feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    public void setMedicalRecord(ToOne<MedicalRecord> medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public ToOne<PhysicalActivityHabit> getPhysicalActivityHabits() {
        return physicalActivityHabits;
    }

    public void setPhysicalActivityHabits(PhysicalActivityHabit physicalActivityHabits) {
        this.physicalActivityHabits.setTarget(physicalActivityHabits);
    }
}
