package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class PilotStudy {

    @Id
    private long idDb;

    @SerializedName("id")
    private String _id;

    @SerializedName("name")
    private String name;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("start")
    private String start;

    @SerializedName("end")
    private String end;
    private boolean isSelected;

    @SerializedName("health_professionals_id")
    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> healthProfessionalsId;

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

    public long getIdDb() {
        return idDb;
    }

    public void setIdDb(long idDb) {
        this.idDb = idDb;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        return new Gson().toJson(this);
    }

    /**
     * Convert json to Object.
     *
     * @param json String
     * @return PilotStudy
     */
    public static PilotStudy jsonDeserialize(String json) {
        Type typePilot = new TypeToken<PilotStudy>() {
        }.getType();
        return new Gson().fromJson(json, typePilot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PilotStudy that = (PilotStudy) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name);
    }
}
