package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Id;

public class NutritionalEvaluationOB {

    @Id
    private long id;

    private String _id;

    private PatientOB patientOB;

    private String healthProfessionalId;

    private String pilotStudy;

    private List<MeasurementOB> measurements;

    private FeedingHabitsRecordOB feedingHabits;

    private SleepHabitOB sleepHabits;

    private PhysicalActivityHabitOB physicalActivityHabits;

    private MedicalRecordOB medicalRecord;

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

    public PatientOB getPatientOB() {
        return patientOB;
    }

    public void setPatientOB(PatientOB patientOB) {
        this.patientOB = patientOB;
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

    public List<MeasurementOB> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<MeasurementOB> measurements) {
        this.measurements = measurements;
    }

    public FeedingHabitsRecordOB getFeedingHabits() {
        return feedingHabits;
    }

    public void setFeedingHabits(FeedingHabitsRecordOB feedingHabits) {
        this.feedingHabits = feedingHabits;
    }

    public SleepHabitOB getSleepHabits() {
        return sleepHabits;
    }

    public void setSleepHabits(SleepHabitOB sleepHabits) {
        this.sleepHabits = sleepHabits;
    }

    public PhysicalActivityHabitOB getPhysicalActivityHabits() {
        return physicalActivityHabits;
    }

    public void setPhysicalActivityHabits(PhysicalActivityHabitOB physicalActivityHabits) {
        this.physicalActivityHabits = physicalActivityHabits;
    }

    public MedicalRecordOB getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecordOB medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public void addMeasuerement(MeasurementOB measurement) {
        if (measurements == null) measurements = new ArrayList<>();

        measurements.add(measurement);
    }

    public void removeMeasuerement(MeasurementOB measurement) {
        if (measurements == null) return;

        measurements.remove(measurement);
    }

    @Override
    public String toString() {
        return "NutritionalEvaluationOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patientOB=" + patientOB +
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
        NutritionalEvaluationOB that = (NutritionalEvaluationOB) o;
        return Objects.equals(id, that.id);
    }


}
