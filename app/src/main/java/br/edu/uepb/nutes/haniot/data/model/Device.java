package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;

/**
 * Represents Device object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
@Entity
public class Device implements Parcelable {
    @Id
    @Expose(serialize = false, deserialize = false)
    private long id;

    @Index
    @SerializedName("id")
    @Expose()
    private String _id; // _id in server remote (UUID)

    @SerializedName("address")
    @Expose()
    private String address; // MAC address

    @SerializedName("name")
    @Expose()
    private String name;

    @SerializedName("manufacturer")
    @Expose()
    private String manufacturer;

    @SerializedName("modelNumber")
    @Expose()
    private String modelNumber;

    @SerializedName("type")
    @Expose()
    private int typeId;

    @SerializedName("user_id")
    @Expose()
    private String userId;

    @Expose(serialize = false, deserialize = false)
    private int img;

    public Device() {
    }

    public Device(String address, String name, String manufacturer, String modelNumber,
                  int typeId, User user) {
        this.address = address;
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.typeId = typeId;
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
        typeId = in.readInt();
        userId = in.readString();
        img = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(_id);
        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(manufacturer);
        dest.writeString(modelNumber);
        dest.writeInt(typeId);
        dest.writeString(userId);
        dest.writeInt(img);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device))
            return false;

        Device other = (Device) o;

        return this.address.equals(other.address);
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
                ", userId='" + userId + '\'' +
                ", img=" + img +
                '}';
    }
}