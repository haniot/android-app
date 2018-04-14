package br.edu.uepb.nutes.haniot.model;

import java.util.List;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents object of a Elderly.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Elderly {
    @Id
    private long id;
    private String name;
    private long dateOfBirth;
    private double weight;
    private int height;
    private char sex;

    /**
     * {@link MaritalStatusType}
     */
    private int maritalStatus;

    /**
     * {@link DegreeEducationType}
     */
    private int DegreeOfEducation;

    /**
     * 0 - NÃO
     * 1 - SIM
     */
    private int liveAlone;

    /**
     * {@link Medication}
     */
    @Backlink(to = "elderly")
    public ToMany<Medication> medications;

    /**
     * Crutches, walking sticks, walkers, glasses, hearing aid...
     * <p>
     * {@link Medication}
     */
    @Backlink(to = "elderly")
    public ToMany<Accessory> accessories;

    public ToOne<User> user;

    public Elderly() {
    }

    public Elderly(String name, long dateOfBirth, double weight, int height, char sex,
                   int maritalStatus, int degreeOfEducation, int liveAlone) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        DegreeOfEducation = degreeOfEducation;
        this.liveAlone = liveAlone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(long dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public int getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(int maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public int getDegreeOfEducation() {
        return DegreeOfEducation;
    }

    public void setDegreeOfEducation(int degreeOfEducation) {
        DegreeOfEducation = degreeOfEducation;
    }

    public int getLiveAlone() {
        return liveAlone;
    }

    public void setLiveAlone(int liveAlone) {
        this.liveAlone = liveAlone;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(ToOne<User> user) {
        this.user = user;
    }

    public ToMany<Medication> getMedications() {
        return medications;
    }

    public boolean addElderly(Medication medication) {
        return this.getMedications().add(medication);
    }

    public boolean addMedications(List<Medication> medications) {
        return this.getMedications().addAll(medications);
    }

    public ToMany<Accessory> getAccessories() {
        return accessories;
    }

    public boolean addAccessory(Accessory accessory) {
        return this.getAccessories().add(accessory);
    }

    public boolean addAccessories(List<Accessory> accessories) {
        return this.getAccessories().addAll(accessories);
    }

    @Override
    public String toString() {
        return "Elderly{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", weight=" + weight +
                ", height=" + height +
                ", sex=" + sex +
                ", maritalStatus=" + maritalStatus +
                ", DegreeOfEducation=" + DegreeOfEducation +
                ", liveAlone=" + liveAlone +
                ", medications=" + medications +
                ", accessories=" + accessories +
                ", user=" + user +
                '}';
    }
}


