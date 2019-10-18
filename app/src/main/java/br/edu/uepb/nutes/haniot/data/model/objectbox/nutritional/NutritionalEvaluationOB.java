package br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.model.nutritional.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PatientOB;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class NutritionalEvaluationOB {

    @Id
    private long id;

    @Index
    private String _id;

    private ToOne<PatientOB> patientOB;

    private String healthProfessionalId;

    private String pilotStudy;

    private ToMany<MeasurementOB> measurements;

    private ToOne<FeedingHabitsRecordOB> feedingHabits;

    private ToOne<SleepHabitOB> sleepHabits;

    private ToOne<PhysicalActivityHabitOB> physicalActivityHabits;

    private ToOne<MedicalRecordOB> medicalRecord;

    public NutritionalEvaluationOB(NutritionalEvaluation n) {
        this.setId(n.getId());
        this.set_id(n.get_id());
        this.setPatientOB(Convert.convertPatient(n.getPatient()));
        this.setHealthProfessionalId(n.getHealthProfessionalId());
        this.setPilotStudy(n.getPilotStudy());
        this.setMeasurements(Convert.listMeasurementsToObjectBox(n.getMeasurements()));
        this.setFeedingHabits(Convert.convertFeedingHabitsRecord(n.getFeedingHabits()));
        this.setSleepHabits(Convert.convertSleepHabit(n.getSleepHabits()));
        this.setPhysicalActivityHabits(Convert.convertPhysicalActivityHabit(n.getPhysicalActivityHabits()));
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

    public PatientOB getPatientOB() {
        return patientOB.getTarget();
    }

    public void setPatientOB(PatientOB patientOB) {
        this.patientOB.setTarget(patientOB);
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
        this.measurements.clear();
        this.measurements.addAll(measurements);
    }

    public FeedingHabitsRecordOB getFeedingHabits() {
        return feedingHabits.getTarget();
    }

    public void setFeedingHabits(FeedingHabitsRecordOB feedingHabits) {
        this.feedingHabits.setTarget(feedingHabits);
    }

    public SleepHabitOB getSleepHabits() {
        return sleepHabits.getTarget();
    }

    public void setSleepHabits(SleepHabitOB sleepHabits) {
        this.sleepHabits.setTarget(sleepHabits);
    }

    public PhysicalActivityHabitOB getPhysicalActivityHabits() {
        return physicalActivityHabits.getTarget();
    }

    public void setPhysicalActivityHabits(PhysicalActivityHabitOB physicalActivityHabits) {
        this.physicalActivityHabits.setTarget(physicalActivityHabits);
    }

    public MedicalRecordOB getMedicalRecord() {
        return medicalRecord.getTarget();
    }

    public void setMedicalRecord(MedicalRecordOB medicalRecord) {
        this.medicalRecord.setTarget(medicalRecord);
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
