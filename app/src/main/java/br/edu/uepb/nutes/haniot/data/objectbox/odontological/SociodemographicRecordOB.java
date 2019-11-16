package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import br.edu.uepb.nutes.haniot.data.model.odontological.SociodemographicRecord;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class SociodemographicRecordOB {

    @Id
    private long id;

    @Index
    private String _id;

    private String colorRace;

    private String motherScholarity;

    private int peopleInHome;

    public SociodemographicRecordOB() {}

    public SociodemographicRecordOB(SociodemographicRecord s) {
        this.setId(s.getId());
        this.set_id(s.get_id());
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
                ", colorRace='" + colorRace + '\'' +
                ", motherScholarity='" + motherScholarity + '\'' +
                ", peopleInHome=" + peopleInHome +
                '}';
    }
}
