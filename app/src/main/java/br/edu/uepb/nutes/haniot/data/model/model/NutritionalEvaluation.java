package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NutritionalEvaluation {

    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

    @SerializedName("patient")
    @Expose()
    private Patient patient;

    @SerializedName("health_professional_id")
    @Expose()
    private String healthProfessionalId;

    @SerializedName("pilotstudy_id")
    @Expose()
    private String pilotStudy;

    @SerializedName("measurements")
    @Expose()
    private List<Measurement> measurements;

    @SerializedName("feeding_habits_record")
    @Expose()
    private FeedingHabitsRecord feedingHabits;

    @SerializedName("sleep_habit")
    @Expose()
    private SleepHabit sleepHabits;

    @SerializedName("physical_activity_habits")
    @Expose()
    private PhysicalActivityHabit physicalActivityHabits;

    @SerializedName("medical_record")
    @Expose()
    private MedicalRecord medicalRecord;

    public NutritionalEvaluation() {
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

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getHealthProfessionalId() {
        return healthProfessionalId;
    }

    public void setHealthProfessionalId(String healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }

    public String getPilotStudy() {
        return pilotStudy;
    }

    public void setPilotStudy(String pilotStudy) {
        this.pilotStudy = pilotStudy;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public FeedingHabitsRecord getFeedingHabits() {
        return feedingHabits;
    }

    public void setFeedingHabits(FeedingHabitsRecord feedingHabits) {
        this.feedingHabits = feedingHabits;
    }

    public SleepHabit getSleepHabits() {
        return sleepHabits;
    }

    public void setSleepHabits(SleepHabit sleepHabits) {
        this.sleepHabits = sleepHabits;
    }

    public PhysicalActivityHabit getPhysicalActivityHabits() {
        return physicalActivityHabits;
    }

    public void setPhysicalActivityHabits(PhysicalActivityHabit physicalActivityHabits) {
        this.physicalActivityHabits = physicalActivityHabits;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public void addMeasuerement(Measurement measurement) {
        if (measurements == null) measurements = new ArrayList<>();

        measurements.add(measurement);
    }

    public void removeMeasuerement(Measurement measurement) {
        if (measurements == null) return;

        measurements.remove(measurement);
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
        return "NutritionalEvaluation{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patient=" + patient +
                ", healthProfessionalId='" + healthProfessionalId + '\'' +
                ", pilotStudy='" + pilotStudy + '\'' +
                ", measurements=" + measurements +
                ", feedingHabits=" + feedingHabits +
                ", sleepHabits=" + sleepHabits +
                ", physicalActivityHabits=" + physicalActivityHabits +
                ", medicalRecord=" + medicalRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NutritionalEvaluation that = (NutritionalEvaluation) o;
        return Objects.equals(id, that.id);
    }


}
