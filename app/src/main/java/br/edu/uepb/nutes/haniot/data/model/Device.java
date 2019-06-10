package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

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

    @SerializedName("name")
    @Expose()
    private String name;

    @SerializedName("address")
    @Expose()
    private String address; // MAC address

    @SerializedName("type")
    @Expose()
    private String type;

    @SerializedName("model_number")
    @Expose()
    private String modelNumber;

    @SerializedName("manufacturer")
    @Expose()
    private String manufacturer;

    @SerializedName("user_id")
    @Expose()
    private String userId;

    @Expose(serialize = false, deserialize = false)
    private int img;

    public Device() {
    }

    public Device(String name, String manufacturer, String modelNumber, int img, String type) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.img = img;
        this.type = type;
    }

    protected Device(Parcel in) {
        id = in.readLong();
        _id = in.readString();
        name = in.readString();
        address = in.readString();
        type = in.readString();
        modelNumber = in.readString();
        manufacturer = in.readString();
        userId = in.readString();
        img = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(_id);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(type);
        dest.writeString(modelNumber);
        dest.writeString(manufacturer);
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

    @Override
    public int hashCode() {
        return Objects.hash(address);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Device)) return false;

        Device other = (Device) o;
        return this.address.equals(other.address) || this.name.equals(other.name);
    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    @NonNull
    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", type='" + type + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", userId='" + userId + '\'' +
                ", img=" + img +
                '}';
    }
}