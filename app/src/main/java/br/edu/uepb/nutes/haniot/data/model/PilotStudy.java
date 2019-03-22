package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.edu.uepb.nutes.haniot.utils.ConverterStringToDatabase;
import io.objectbox.annotation.Convert;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class PilotStudy {

    @Id
    @Expose(serialize = false)
    private long idDb;
    @Index
    @SerializedName("id")
    private String id;
    private String name;
    private boolean isActive;
    private String start;
    private String end;
    private boolean isSelected;

    @Convert(converter = ConverterStringToDatabase.class, dbType = String.class)
    private List<String> healthProfessionalsId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
