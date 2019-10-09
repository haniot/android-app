package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Device;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Device_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * DeviceDAO implementation.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class DeviceDAO {
    public static DeviceDAO instance;
    private static Box<Device> deviceBox;

    private DeviceDAO() {
    }

    /**
     * Get singleton instance.
     *
     * @param context {@link Context}
     * @return DeviceDAO
     */
    public static synchronized DeviceDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new DeviceDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        deviceBox = boxStore.boxFor(Device.class);

        return instance;
    }

    /**
     * Adds a new device to the database.
     *
     * @param device Device
     * @return boolean
     */
    public boolean save(@NonNull Device device) {
        return deviceBox.put(device) > 0;
    }


    /**
     * Select a Device according to id.
     *
     * @param id long
     * @return Object
     */
    public Device get(long id) {
        return deviceBox.query()
                .equal(Device_.id, id)
                .build().findFirst();
    }

    /**
     * Select a Device according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public Device get(@NonNull String address, String userId) {
        return deviceBox.query()
                .equal(Device_.address, address)
                .equal(Device_.userId, userId)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return Device
     */
    public Device getByType(@NonNull String userId, String type) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .equal(Device_.type, type)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId.
     *
     * @param userId long
     * @return List<T>
     */
    public List<Device> list(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().find();
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device Device
     * @return boolean
     */
    public boolean update(@NonNull Device device) {
        if (device.getId() == 0) {
            Device deviceUp = get(device.getId());

            // Id is required for an updateOrSave
            // Otherwise it will be an insert
            if (deviceUp == null) return false;

            device.setId(deviceUp.getId());
        }

        return save(device); // updateOrSave
    }

    /**
     * Removes device passed as parameter.
     *
     * @param device Device
     * @return boolean
     */
    public boolean remove(@NonNull Device device) {
        return (deviceBox.query()
                .equal(Device_.id, device.getId())
                .build()
                .remove()) > 0;
    }

    /**
     * Removes device passed as parameter.
     *
     * @param _id String
     * @return boolean
     */
    public boolean remove(@NonNull String _id) {
        return (deviceBox.query()
                .equal(Device_._id, _id)
                .build()
                .remove()) > 0;
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId long
     * @return long - Total number of itemsList removed
     */
    public boolean removeAll(@NonNull long userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().remove() > 0;
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAll(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_._id, userId)
                .build().remove() > 0;
    }
}
