package br.edu.uepb.nutes.haniot.data.model.objectbox;

import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import io.objectbox.annotation.Entity;

@Entity
public class PatientOB extends UserOB {

    private String pilotId;
    private String gender;
    private String healthProfessionalId;

    public PatientOB(Patient p) {
        super(p.getEmail(), p.getPassword());
        this.setPilotId(p.getPilotId());
        this.setGender(p.getGender());
        this.setHealthProfessionalId(p.getHealthProfessionalId());
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

    @Override
    public String toString() {
        return "PatientOB{" +
                ", pilotId='" + pilotId + '\'' +
                ", gender='" + gender + '\'' +
                ", healthProfessionalId='" + healthProfessionalId + '\'' +
                '}';
    }
}
