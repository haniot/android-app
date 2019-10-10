package br.edu.uepb.nutes.haniot.data.model.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.objectbox.DeviceOB;

/**
 * Represents DeviceOB object.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class Device implements Parcelable {

    @Expose(serialize = false, deserialize = false)
    private long id;

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
    private String uuid;

    @Expose(serialize = false, deserialize = false)
    private int img;

    public Device(DeviceOB d) {
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

    public Device(String name, String manufacturer, String modelNumber, int img, String type, String uuid) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.modelNumber = modelNumber;
        this.img = img;
        this.type = type;
        this.uuid = uuid;
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
        uuid = in.readString();
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
        dest.writeString(uuid);
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
        if (!(o instanceof Device)) return false;

        Device other = (Device) o;
        return this.type.equals(other.type);
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