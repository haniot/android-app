package br.edu.uepb.nutes.haniot.data.objectbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.FeedingHabitsRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.SleepHabitOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.FamilyCohesionRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OralHealthRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.SociodemographicRecordOB;
import io.objectbox.annotation.Id;

public class DentristEvaluationOB {

    @Id
    private long id;

    private String _id;

    private PatientOB patientOB;

    private String healthProfessionalId;

    private String pilotStudy;

    private List<MeasurementOB> measurements;

    private FeedingHabitsRecordOB feedingHabits;

    private SleepHabitOB sleepHabits;

    private OralHealthRecordOB oralHealthRecord;

    private FamilyCohesionRecordOB familyCohesionRecord;

    private SociodemographicRecordOB sociodemographicRecord;

    public DentristEvaluationOB() {}

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

    public OralHealthRecordOB getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecordOB oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
    }

    public FamilyCohesionRecordOB getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecordOB familyCohesionRecord) {
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public SociodemographicRecordOB getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecordOB sociodemographicRecord) {
        this.sociodemographicRecord = sociodemographicRecord;
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
        return "DentristEvaluation{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patientOB=" + patientOB +
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
