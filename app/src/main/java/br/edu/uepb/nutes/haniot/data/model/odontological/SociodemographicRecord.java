package br.edu.uepb.nutes.haniot.data.model.odontological;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.edu.uepb.nutes.haniot.data.objectbox.odontological.SociodemographicRecordOB;

public class SociodemographicRecord {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

//    @Expose(serialize = false, deserialize = false)
//    private String patientId;
//
//    @SerializedName("created_at")
//    @Expose(serialize = false)
//    private String createdAt;

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

    public SociodemographicRecord(SociodemographicRecordOB s) {
        this.setId(s.getId());
        this.set_id(s.get_id());
//        this.setPatientId(s.getPatientId());
//        this.setCreatedAt(s.getCreatedAt());
        this.setColorRace(s.getColorRace());
        this.setMotherScholarity(s.getMotherScholarity());
        this.setPeopleInHome(s.getPeopleInHome());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }

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
                "id=" + id +
                ", _id='" + _id + '\'' +
//                ", patientId='" + patientId + '\'' +
//                ", createdAt='" + createdAt + '\'' +
                ", colorRace='" + colorRace + '\'' +
                ", motherScholarity='" + motherScholarity + '\'' +
                ", peopleInHome=" + peopleInHome +
                '}';
    }
}
