package br.edu.uepb.nutes.haniot.model.elderly;

import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.model.User;
import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;
import io.objectbox.relation.ToOne;

/**
 * Represents object of a Elderly.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
@Entity
public class Elderly {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String name;
    private long dateOfBirth;
    private double weight;
    private int height;
    private String phone;

    /**
     * 1 - low risk
     * 2 - moderate risk
     * 3 - high risk
     */
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
    private int degreeOfEducation;

    /**
     * false - NÃO
     * true - SIM
     */
    private boolean liveAlone;

    private String pin;

    /**
     * {@link Item}
     */
    public ToMany<Item> medications;

    /**
     * Crutches, walking sticks, walkers, glasses, hearing aid...
     * <p>
     * {@link Item}
     */
    public ToMany<Item> accessories;

    public ToOne<User> user;

    /**
     * Falls of the elderly.
     * <p>
     * {@link Fall}
     */
    @Backlink(to = "elderly")
    public ToMany<Fall> falls;

    public Elderly() {
    }

    public Elderly(String name, long dateOfBirth, double weight, int height, int sex,
                   int maritalStatus, int degreeOfEducation, int fallRisk, String phone, boolean liveAlone) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.weight = weight;
        this.height = height;
        this.sex = sex;
        this.maritalStatus = maritalStatus;
        this.degreeOfEducation = degreeOfEducation;
        this.fallRisk = fallRisk;
        this.phone = phone;
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
        return degreeOfEducation;
    }

    public void setDegreeOfEducation(int degreeOfEducation) {
        this.degreeOfEducation = degreeOfEducation;
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

    public ToMany<Item> getMedications() {
        return this.medications;
    }

    public boolean addElderly(Item medication) {
        return this.getMedications().add(medication);
    }

    public boolean addMedications(List<Item> medications) {
        return this.getMedications().addAll(medications);
    }

    public boolean addMedication(Item medication) {
        return this.getMedications().add(medication);
    }

    public ToMany<Item> getAccessories() {
        return this.accessories;
    }

    public ToMany<Fall> getFalls() {
        return this.falls;
    }

    public boolean addAccessory(Item accessory) {
        return this.getAccessories().add(accessory);
    }

    public boolean addAccessories(List<Item> accessories) {
        return this.getAccessories().addAll(accessories);
    }

    public void clearAccessories() {
        this.accessories.clear();
    }

    public void clearMedications() {
        this.medications.clear();
    }

    public boolean addFalls(List<Fall> falls) {
        return this.getFalls().addAll(falls);
    }

    public boolean addFall(Fall fall) {
        return this.getFalls().add(fall);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    /**
     * 1 - low risk
     * 2 - moderate risk
     * 3 - high risk
     *
     * @return
     */
    public int getFallRisk() {
        return fallRisk;
    }

    public void setFallRisk(int fallRisk) {
        this.fallRisk = fallRisk;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String toString() {
        return "Elderly{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", weight=" + weight +
                ", height=" + height +
                ", phone='" + phone + '\'' +
                ", fallRisk=" + fallRisk +
                ", sex=" + sex +
                ", maritalStatus=" + maritalStatus +
                ", degreeOfEducation=" + degreeOfEducation +
                ", liveAlone=" + liveAlone +
                ", pin='" + pin + '\'' +
                ", medications=" + Arrays.toString(medications.toArray()) +
                ", accessories=" + Arrays.toString(accessories.toArray()) +
                ", user=" + user +
                ", falls=" + falls +
                '}';
    }
}