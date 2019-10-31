package br.edu.uepb.nutes.haniot.data.objectbox;

import android.support.annotation.NonNull;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.Device;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;

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
    private String _id; // _id in server remote (UUID)

    private String name;

    private String address; // MAC address

    private String type;

    private String modelNumber;

    private String manufacturer;

    private String userId;

    @Transient // not persisted
    private String uuid;

    @Transient // not persisted
    private int img;

    public DeviceOB() {
        super();
    }

    public DeviceOB(Device d) {
        super(d.isSync());
        this.setId(d.getId());
        this.set_id(d.get_id());
        this.setName(d.getName());
        this.setAddress(d.getAddress());
        this.setType(d.getType());
        this.setModelNumber(d.getModelNumber());
        this.setManufacturer(d.getManufacturer());
        this.setUserId(d.getUserId());
        this.setUuid(d.getUuid());
        this.setImg(d.getImg());
    }

//    public DeviceOB(String name, String manufacturer, String modelNumber, int img, String type, String uuid) {
//        this.name = name;
//        this.manufacturer = manufacturer;
//        this.modelNumber = modelNumber;
//        this.img = img;
//        this.type = type;
//        this.uuid = uuid;
//    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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
                ", userId='" + userId + '\'' +
                ", img=" + img +
                ", uuid=" + uuid +
                '}';
    }
}