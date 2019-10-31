package br.edu.uepb.nutes.haniot.data.objectbox.del;

import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.objectbox.UserOB;
import io.objectbox.annotation.Entity;

/**
 * Represents UserOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class AdminOB extends UserOB {

    private String totalPilotStudies;

    private String totalPatients;

    private String totalHealthProfessionals;

    private String totalAdmins;

    public AdminOB(Admin p) {
        super(p.getId(), p.get_id(), p.getEmail(), p.getName(), p.getBirthDate(), p.getHealthArea(), p.getPassword(), p.getOldPassword(), p.getNewPassword(),
                p.getPhoneNumber(), p.getLastLogin(), p.getLastSync(), p.getLanguage(), p.getPilotStudyIDSelected(), p.getUserType(), p.isSync());
        this.setTotalPilotStudies(p.getTotalPilotStudies());
        this.setTotalPatients(p.getTotalPatients());
        this.setTotalHealthProfessionals(p.getTotalHealthProfessionals());
        this.setTotalAdmins(p.getTotalAdmins());
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

    public String getTotalHealthProfessionals() {
        return totalHealthProfessionals;
    }

    public void setTotalHealthProfessionals(String totalHealthProfessionals) {
        this.totalHealthProfessionals = totalHealthProfessionals;
    }

    public String getTotalAdmins() {
        return totalAdmins;
    }

    public void setTotalAdmins(String totalAdmins) {
        this.totalAdmins = totalAdmins;
    }

    @Override
    public String toString() {
        return "AdminOB{" +
                "totalPilotStudies='" + totalPilotStudies + '\'' +
                ", totalPatients='" + totalPatients + '\'' +
                ", totalHealthProfessionals='" + totalHealthProfessionals + '\'' +
                ", totalAdmins='" + totalAdmins + '\'' +
                '}';
    }

}
