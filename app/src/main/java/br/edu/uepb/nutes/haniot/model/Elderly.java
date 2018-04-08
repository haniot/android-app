package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
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
     * medication 1, medication 2...
     */
    private String medications;

    /**
     * Crutches, walking sticks, walkers, glasses, hearing aid, other ...
     */
    private String accessories;

    public ToOne<User> user;

    public Elderly() {
    }

    public Elderly(String name, long dateOfBirth, double weight, int height, char sex,
                   int maritalStatus, int degreeOfEducation, int liveAlone,
                   String medications, String accessories) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        DegreeOfEducation = degreeOfEducation;
        this.liveAlone = liveAlone;
        this.medications = medications;
        this.accessories = accessories;
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

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }

    public String getAccessories() {
        return accessories;
    }

    public void setAccessories(String accessories) {
        this.accessories = accessories;
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
                ", medications='" + medications + '\'' +
                ", accessories='" + accessories + '\'' +
                '}';
    }
}


