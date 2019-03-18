package br.edu.uepb.nutes.haniot.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Patient implements Parcelable {

    @Id
    private long id;

    @Index
    private String _id;
    private String pilotId;
    private String name;
    private String gender;
    private int age;


    @Transient
    private SleepHabit sleepHabit;
    @Transient
    private PhysicalActivityHabits physicalActivityHabits;
    @Transient
    private FeedingHabitsRecord feedingHabitsRecord;
    @Transient
    private MedicalRecord medicalRecord;

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

    protected Patient(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        pilotId = in.readString();
        name = in.readString();
        gender = in.readString();
        age = in.readInt();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public SleepHabit getSleepHabit() {
        return sleepHabit;
    }

    public void setSleepHabit(SleepHabit sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public PhysicalActivityHabits getPhysicalActivityHabits() {
        return physicalActivityHabits;
    }

    public void setPhysicalActivityHabits(PhysicalActivityHabits physicalActivityHabits) {
        this.physicalActivityHabits = physicalActivityHabits;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(pilotId);
        parcel.writeInt(gender);
        parcel.writeInt(age);
    }
}
