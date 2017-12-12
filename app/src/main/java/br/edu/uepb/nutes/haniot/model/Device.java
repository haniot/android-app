package br.edu.uepb.nutes.haniot.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents Device object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Device {
    @Id
    private long id;

    @Index
    private String address; // MAC address of the device (Thermometer, Glucose...)

    @Index
    private String userId; // _id provided by remote server

    private String name;
    private String manufacturer;
    private String modelNumber;

    /**
     * 1 - Thermometer
     * 2 - Glucose
     * 3 - Weighing Scale
     * 4 - Heart Rate
     * 5 - Blood Pressure
     * 6 -
     */
    private int type;

    public Device() {
    }

    public Device(String address, String name) {
        this.address = address;
        this.name = name;
    }

    public Device(String address, String name, String manufacturer, String modelNumber, int type, String userId) {
        this.address = address;
        this.userId = userId;
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + type;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Device))
            return false;

        Device other = (Device) o;

        if (id != other.id) return false;
        if (type != other.type) return false;
        if (address != null ? !address.equals(other.address) : other.address != null)
            return false;
        if (userId != null ? !userId.equals(other.userId) : other.userId != null) return false;
        if (name != null ? !name.equals(other.name) : other.name != null) return false;
        return manufacturer != null ? manufacturer.equals(other.manufacturer) : other.manufacturer == null;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", type=" + type +
                '}';
    }
}
