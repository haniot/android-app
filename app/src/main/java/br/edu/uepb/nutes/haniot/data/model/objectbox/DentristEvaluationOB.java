package br.edu.uepb.nutes.haniot.data.model.objectbox;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.objectbox.annotation.Id;

public class DentristEvaluationOB {

    @Id
    @Expose(deserialize = false, serialize = false)
    private long id;

    @SerializedName("id")
    private String _id;

    @SerializedName("patient")
    private Patient patient;

    @SerializedName("health_professional_id")
    private String healthProfessionalId;

    @SerializedName("pilotstudy_id")
    private String pilotStudy;

    @SerializedName("measurements")
    private List<Measurement> measurements;

    @SerializedName("feeding_habits_record")
    private FeedingHabitsRecord feedingHabits;

    @SerializedName("sleep_habits")
    private SleepHabit sleepHabits;

    @SerializedName("oral_health_record")
    private OralHealthRecord oralHealthRecord;

    @SerializedName("family_cohesion_record")
    private FamilyCohesionRecord familyCohesionRecord;

    @SerializedName("family_cohesion_record")
    private SociodemographicRecord sociodemographicRecord;

    public DentristEvaluationOB() {
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

    public OralHealthRecord getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecord oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
    }

    public FamilyCohesionRecord getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecord familyCohesionRecord) {
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public SociodemographicRecord getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public void addMeasuerement(Measurement measurement) {
        if (measurements == null) measurements = new ArrayList<>();

        measurements.add(measurement);
    }

    public void removeMeasuerement(Measurement measurement) {
        if (measurements == null) return;

        measurements.remove(measurement);
    }

    @Override
    public String toString() {
        return "DentristEvaluation{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patient=" + patient +
                ", healthProfessionalId='" + healthProfessionalId + '\'' +
                ", pilotStudy='" + pilotStudy + '\'' +
                ", measurements=" + measurements +
                ", feedingHabits=" + feedingHabits +
                ", sleepHabits=" + sleepHabits +
                ", oralHealthRecord=" + oralHealthRecord +
                ", familyCohesionRecord=" + familyCohesionRecord +
                ", sociodemographicRecord=" + sociodemographicRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DentristEvaluationOB that = (DentristEvaluationOB) o;
        return Objects.equals(id, that.id);
    }
}
