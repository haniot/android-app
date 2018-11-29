package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Patient {

    @Id
    private long id;

    @Index
    private String _id;

    private String name;
    private String sex;
    private String color;
    private int age;
    private String registerDate;
    private long idProfessionalSponsor;

    /**
     * RELATIONS
     */

    /**
     * {@link Measurement}
     */
    @Backlink(to = "children")
    ToMany<Measurement> measurements;

    public Patient(){

    }

    public ToMany<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(ToMany<Measurement> measurements) {
        this.measurements = measurements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public long getIdProfessionalSponsor() {
        return idProfessionalSponsor;
    }

    public void setIdProfessionalSponsor(long idProfessionalSponsor) {
        this.idProfessionalSponsor = idProfessionalSponsor;
    }

}
