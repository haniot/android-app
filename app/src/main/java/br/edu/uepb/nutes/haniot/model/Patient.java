package br.edu.uepb.nutes.haniot.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

@Entity
public class Patient implements Parcelable {

    @Id
    private long id;

    @Index
    private String _id;

    private String name;
    private String sex;
    private String color;
    private int age;
    private String registerDate;
    private long idProfessionalResponsible;

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

    protected Patient(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        name = in.readString();
        sex = in.readString();
        color = in.readString();
        age = in.readInt();
        registerDate = in.readString();
        idProfessionalResponsible = in.readLong();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

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

    public long getIdProfessionalResponsible() {
        return idProfessionalResponsible;
    }

    public void setIdProfessionalResponsible(long idProfessionalResponsible) {
        this.idProfessionalResponsible = idProfessionalResponsible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(sex);
        parcel.writeString(color);
        parcel.writeInt(age);
        parcel.writeString(registerDate);
        parcel.writeLong(idProfessionalResponsible);
    }

}
