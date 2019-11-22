package br.edu.uepb.nutes.haniot.data.model.odontological;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;

public class SociodemographicRecord extends ActivityHabitsRecord {

    @SerializedName("color_race")
    @Expose()
    private String colorRace;

    @SerializedName("mother_scholarity")
    @Expose()
    private String motherScholarity;

    @SerializedName("people_in_home")
    @Expose()
    private int peopleInHome;

    public SociodemographicRecord() {
    }

    public String getColorRace() {
        return colorRace;
    }

    public void setColorRace(String colorRace) {
        this.colorRace = colorRace;
    }

    public String getMotherScholarity() {
        return motherScholarity;
    }

    public void setMotherScholarity(String motherScholarity) {
        this.motherScholarity = motherScholarity;
    }

    public int getPeopleInHome() {
        return peopleInHome;
    }

    public void setPeopleInHome(int peopleInHome) {
        this.peopleInHome = peopleInHome;
    }

//    public String getPatientId() {
//        return patientId;
//    }
//
//    public void setPatientId(String patientId) {
//        this.patientId = patientId;
//    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        Log.i("AAAAAAAAAA", a);
        return a;
    }

    @Override
    public String toString() {
        return "SociodemographicRecordOB{" +
                super.toString() +
                "colorRace='" + colorRace + '\'' +
                ", motherScholarity='" + motherScholarity + '\'' +
                ", peopleInHome=" + peopleInHome +
                '}';
    }
}
