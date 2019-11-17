package br.edu.uepb.nutes.haniot.data.objectbox;

import br.edu.uepb.nutes.haniot.data.model.Patient;
import io.objectbox.annotation.Entity;

@Entity
public class PatientOB extends UserOB {

    private String pilotId;
    private String gender;
    private String healthProfessionalId;
    private String createdAt;

    public PatientOB() { }

    public PatientOB(Patient p) {
        super(p.getId(), p.get_id(), p.getEmail(), p.getName(), p.getBirthDate(), p.getHealthArea(), p.getPassword(), p.getOldPassword(), p.getNewPassword(),
                p.getPhoneNumber(), p.getLastLogin(), p.getLastSync(), p.getLanguage(), p.getPilotStudyIDSelected(), p.getUserType(), p.isSync());
        this.setPilotId(p.getPilotId());
        this.setGender(p.getGender());
        this.setHealthProfessionalId(p.getHealthProfessionalId());
        this.setCreatedAt(p.getCreatedAt());
    }

    public String getPilotId() {
        return pilotId;
    }

    public void setPilotId(String pilotId) {
        this.pilotId = pilotId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHealthProfessionalId() {
        return healthProfessionalId;
    }

    public void setHealthProfessionalId(String healthProfessionalId) {
        this.healthProfessionalId = healthProfessionalId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "PatientOB{" +
                ", pilotId='" + pilotId + '\'' +
                ", gender='" + gender + '\'' +
                ", healthProfessionalId='" + healthProfessionalId + '\'' +
                '}';
    }
}
