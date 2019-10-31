package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents UserOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class HealthProfessional extends User {

    @SerializedName("total_pilot_studies")
    @Expose()
    private String totalPilotStudies;

    @SerializedName("total_patients")
    @Expose()
    private String totalPatients;

//    public HealthProfessional() {
//    }

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
