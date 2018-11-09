package br.edu.uepb.nutes.haniot.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToOne;

/**
 * Represents Device object.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 2.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
@Entity
public class Device implements Parcelable {
    @Id
    private long id;

    @Index
    private String _id; // _id in server remote (UUID)

    private String address; // MAC address
    private String name;
    private String manufacturer;
    private String modelNumber;
    private int img;

    /**
     * RELATIONS
     */
    private ToOne<User> user;

    /**
     * {@link DeviceType()}
     */
    private int typeId;

    public Device() {
    }

    public Device(String address, String name, String manufacturer, String modelNumber,
                  int typeId, User user) {
        this.address = address;
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.typeId = typeId;
        this.user.setTarget(user);
    }

    public Device(String name, String manufacturer, String modelNumber, int img, int typeId) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.img = img;
        this.typeId = typeId;
    }

    protected Device(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        address = in.readString();
        name = in.readString();
        manufacturer = in.readString();
        modelNumber = in.readString();
        img = in.readInt();
        typeId = in.readInt();
    }

    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(_id);
        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeString(modelNumber);
        dest.writeInt(img);
        dest.writeInt(typeId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public ToOne<User> getUser() {
        return user;
    }

    public void setUser(ToOne<User> user) {
        this.user = user;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + address.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (manufacturer != null ? manufacturer.hashCode() : 0);
        result = 31 * result + (modelNumber != null ? modelNumber.hashCode() : 0);
        result = 31 * result + typeId;
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device))
            return false;

        Device other = (Device) o;

        return this.modelNumber.equals(other.modelNumber);
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", typeId=" + typeId +
                ", user=" + user +
                '}';
    }
}
