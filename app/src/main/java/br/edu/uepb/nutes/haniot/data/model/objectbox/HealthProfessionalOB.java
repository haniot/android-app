package br.edu.uepb.nutes.haniot.data.model.objectbox;

import br.edu.uepb.nutes.haniot.data.model.model.HealthProfessional;
import io.objectbox.annotation.Entity;

/**
 * Represents UserOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class HealthProfessionalOB extends UserOB {

    private String totalPilotStudies;

    private String totalPatients;

    public HealthProfessionalOB(HealthProfessional p) {
        super(p.getId(), p.get_id(), p.getEmail(), p.getName(), p.getBirthDate(), p.getHealthArea(), p.getPassword(), p.getOldPassword(), p.getNewPassword(),
                p.getPhoneNumber(), p.getLastLogin(), p.getLastSync(), p.getLanguage(), p.getPilotStudyIDSelected(), p.getUserType());
        this.setTotalPilotStudies(p.getTotalPilotStudies());
        this.setTotalPatients(p.getTotalPatients());
    }

    public String getTotalPilotStudies() {
        return totalPilotStudies;
    }

    public void setTotalPilotStudies(String totalPilotStudies) {
        this.totalPilotStudies = totalPilotStudies;
    }

    public String getTotalPatients() {
        return totalPatients;
    }

    public void setTotalPatients(String totalPatients) {
        this.totalPatients = totalPatients;
    }
}
