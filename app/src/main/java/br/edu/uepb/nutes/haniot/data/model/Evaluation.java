package br.edu.uepb.nutes.haniot.data.model;

import java.util.List;
import java.util.Objects;

public class Evaluation {

    private String id;
    private String feendingHabitsId;
    private String sleepHabitsId;
    private String physicalActivityHabitsId;
    private String medicalRecordId;
    private List<String> measurementId;
    private String status;
    private List<String> result;

    public Evaluation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFeendingHabitsId() {
        return feendingHabitsId;
    }

    public void setFeendingHabitsId(String feendingHabitsId) {
        this.feendingHabitsId = feendingHabitsId;
    }

    public String getSleepHabitsId() {
        return sleepHabitsId;
    }

    public void setSleepHabitsId(String sleepHabitsId) {
        this.sleepHabitsId = sleepHabitsId;
    }

    public String getPhysicalActivityHabitsId() {
        return physicalActivityHabitsId;
    }

    public void setPhysicalActivityHabitsId(String physicalActivityHabitsId) {
        this.physicalActivityHabitsId = physicalActivityHabitsId;
    }

    public String getMedicalRecordId() {
        return medicalRecordId;
    }

    public void setMedicalRecordId(String medicalRecordId) {
        this.medicalRecordId = medicalRecordId;
    }

    public List<String> getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(List<String> measurementId) {
        this.measurementId = measurementId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Evaluation{" +
                "id='" + id + '\'' +
                ", feendingHabitsId='" + feendingHabitsId + '\'' +
                ", sleepHabitsId='" + sleepHabitsId + '\'' +
                ", physicalActivityHabitsId='" + physicalActivityHabitsId + '\'' +
                ", medicalRecordId='" + medicalRecordId + '\'' +
                ", measurementId=" + measurementId +
                ", status='" + status + '\'' +
                ", result=" + result +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return Objects.equals(id, that.id);
    }
}
