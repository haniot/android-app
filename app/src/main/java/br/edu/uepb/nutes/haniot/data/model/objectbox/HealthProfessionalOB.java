package br.edu.uepb.nutes.haniot.data.model.objectbox;

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

    public HealthProfessionalOB() {

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
