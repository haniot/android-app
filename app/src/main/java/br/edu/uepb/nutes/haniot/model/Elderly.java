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
    private String phone;
    private int fallRisk;

    /**
     * 0 - male
     * 1 - female
     */
    private int sex;
    /**
     * {@link MaritalStatusType}
     */
    private int maritalStatus;

    /**
     * {@link DegreeEducationType}
     */
    private int DegreeOfEducation;

    /**
     * false - NÃO
     * true - SIM
     */
    private boolean liveAlone;

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

    public Elderly(String name, long dateOfBirth, double weight, int height, int sex,
                   int maritalStatus, int degreeOfEducation, boolean liveAlone) {
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

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
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

    public boolean getLiveAlone() {
        return liveAlone;
    }

    public void setLiveAlone(boolean liveAlone) {
        this.liveAlone = liveAlone;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user.setTarget(user);
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

    public boolean addMedication(Medication medication) {
        return this.getMedications().add(medication);
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getFallRisk() {
        return fallRisk;
    }

    public void setFallRisk(int fallRisk) {
        this.fallRisk = fallRisk;
    }

    @Override
    public String toString() {
        return "Elderly{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", weight=" + weight +
                ", height=" + height +
                ", phone='" + phone + '\'' +
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


