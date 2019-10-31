package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.data.objectbox.del.PilotStudyOB;

public class PilotStudy extends Sync {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @SerializedName("name")
    @Expose()
    private String name;

    @SerializedName("is_active")
    @Expose()
    private boolean isActive;

    @SerializedName("start")
    @Expose()
    private String start;

    @SerializedName("end")
    @Expose()
    private String end;

    @Expose(serialize = false, deserialize = false)
    private boolean isSelected;

    @SerializedName("health_professionals_id")
    @Expose()
    private List<String> healthProfessionalsId;

    @Expose()
    private String userId;

    public PilotStudy() {
    }

    public PilotStudy(PilotStudyOB p) {
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

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PilotStudyOB
     */
    @Nullable
    public static PilotStudy jsonDeserialize(String json) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Type typePilot = new TypeToken<PilotStudy>() {
        }.getType();
        return gson.fromJson(json, typePilot);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PilotStudy)) return false;

        PilotStudy that = (PilotStudy) o;
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
