package br.edu.uepb.nutes.haniot.data.model.objectbox;

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

    public AdminOB() {
    }

    public AdminOB(String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
        this.totalPilotStudies = totalPilotStudies;
        this.totalPatients = totalPatients;
        this.totalHealthProfessionals = totalHealthProfessionals;
        this.totalAdmins = totalAdmins;
    }

    public AdminOB(String email, String password, String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
        super(email, password);
        this.totalPilotStudies = totalPilotStudies;
        this.totalPatients = totalPatients;
        this.totalHealthProfessionals = totalHealthProfessionals;
        this.totalAdmins = totalAdmins;
    }

    public AdminOB(String _id, String oldPassword, String newPassword, String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
        super(_id, oldPassword, newPassword);
        this.totalPilotStudies = totalPilotStudies;
        this.totalPatients = totalPatients;
        this.totalHealthProfessionals = totalHealthProfessionals;
        this.totalAdmins = totalAdmins;
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
