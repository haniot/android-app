package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.model.PilotStudy;
import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class PilotStudyOB {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String name;

    private boolean isActive;

    private String start;

    private String end;

    private boolean isSelected;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> healthProfessionalsId;

    private String userId;

    public PilotStudyOB(PilotStudy p) {
        this.set_id(p.get_id());
        this.setId(p.getId());
        this.setName(p.getName());
        this.setActive(p.isActive());
        this.setStart(p.getStart());
        this.setEnd(p.getEnd());
        this.setSelected(p.isSelected());
        this.setHealthProfessionalsId(p.getHealthProfessionalsId());
        this.setUserId(p.getUserId());
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public List<String> getHealthProfessionalsId() {
        return healthProfessionalsId;
    }

    public void setHealthProfessionalsId(List<String> healthProfessionalsId) {
        this.healthProfessionalsId = healthProfessionalsId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        return gson.toJson(this);
//    }
//
//    /**
//     * Convert json to Object.
//     *
//     * @param json String
//     * @return PilotStudyOB
//     */
//    @Nullable
//    public static PilotStudyOB jsonDeserialize(String json) {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        Type typePilot = new TypeToken<PilotStudyOB>() {
//        }.getType();
//        return gson.fromJson(json, typePilot);
//    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PilotStudyOB)) return false;

        PilotStudyOB that = (PilotStudyOB) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "PilotStudyOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", isSelected=" + isSelected +
                ", healthProfessionalsId=" + healthProfessionalsId +
                ", userId='" + userId + '\'' +
                '}';
    }
}
