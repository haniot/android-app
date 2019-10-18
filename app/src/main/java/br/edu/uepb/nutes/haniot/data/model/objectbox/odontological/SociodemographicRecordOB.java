package br.edu.uepb.nutes.haniot.data.model.objectbox.odontological;

import io.objectbox.annotation.Id;

public class SociodemographicRecordOB {

    @Id
    private long id;

    private String _id;

    private String patientId;

    private String createdAt;

    private String colorRace;

    private String motherScholarity;

    private int peopleInHome;

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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        String a = gson.toJson(this);
//        Log.i("AAAAAAAAAA", a);
//        return a;
//    }

    @Override
    public String toString() {
        return "SociodemographicRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", colorRace='" + colorRace + '\'' +
                ", motherScholarity='" + motherScholarity + '\'' +
                ", peopleInHome=" + peopleInHome +
                '}';
    }
}
