package br.edu.uepb.nutes.haniot.data.model.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents User object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class Admin extends User {

    @SerializedName("total_pilot_studies")
    @Expose()
    private String totalPilotStudies;

    @SerializedName("total_patients")
    @Expose()
    private String totalPatients;

    @SerializedName("total_health_professionals")
    @Expose()
    private String totalHealthProfessionals;

    @SerializedName("total_admins")
    @Expose()
    private String totalAdmins;

    public Admin() {
    }

    public Admin(String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
        this.totalPilotStudies = totalPilotStudies;
        this.totalPatients = totalPatients;
        this.totalHealthProfessionals = totalHealthProfessionals;
        this.totalAdmins = totalAdmins;
    }

    public Admin(String email, String password, String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
        super(email, password);
        this.totalPilotStudies = totalPilotStudies;
        this.totalPatients = totalPatients;
        this.totalHealthProfessionals = totalHealthProfessionals;
        this.totalAdmins = totalAdmins;
    }

    public Admin(String _id, String oldPassword, String newPassword, String totalPilotStudies, String totalPatients, String totalHealthProfessionals, String totalAdmins) {
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
        return "Admin{" +
                "totalPilotStudies='" + totalPilotStudies + '\'' +
                ", totalPatients='" + totalPatients + '\'' +
                ", totalHealthProfessionals='" + totalHealthProfessionals + '\'' +
                ", totalAdmins='" + totalAdmins + '\'' +
                '}';
    }

}
