package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * Represents DeviceOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class DeviceOB extends SyncOB {
    @Id
    private long id;

    @Index
    private String _id;

    private String name;

    private String address;

    private String type;

    private String modelNumber;

    private String manufacturer;

    private String user_id;

    private long userId;

//    @Transient // not persisted
//    private String uuid;
//
//    @Transient // not persisted
//    private int img;

    public DeviceOB() { }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DeviceOB)) return false;

        DeviceOB other = (DeviceOB) o;
        return this.type.equals(other.type);
    }

    @NonNull
    @Override
    public String toString() {
        return "DeviceOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", user_id='" + user_id + '\'' +
                '}';
    }
}