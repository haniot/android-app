package br.edu.uepb.nutes.haniot.model;

public abstract class ActivityHabitsRecord {

    private String id;
    private String patientId;
    private long createdAt;

    public ActivityHabitsRecord() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
