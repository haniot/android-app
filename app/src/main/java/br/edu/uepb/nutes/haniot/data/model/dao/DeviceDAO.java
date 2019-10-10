package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.DeviceOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.DeviceOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * DeviceDAO implementation.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class DeviceDAO {
    public static DeviceDAO instance;
    private static Box<DeviceOB> deviceBox;

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
        deviceBox = boxStore.boxFor(DeviceOB.class);

        return instance;
    }

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean save(@NonNull DeviceOB device) {
        return deviceBox.put(device) > 0;
    }

    /**
     * Select a DeviceOB according to id.
     *
     * @param id long
     * @return Object
     */
    public DeviceOB get(long id) {
        return deviceBox.query()
                .equal(DeviceOB_.id, id)
                .build().findFirst();
    }

    /**
     * Select a DeviceOB according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public DeviceOB get(@NonNull String address, String userId) {
        return deviceBox.query()
                .equal(DeviceOB_.address, address)
                .equal(DeviceOB_.userId, userId)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return DeviceOB
     */
    public DeviceOB getByType(@NonNull String userId, String type) {
        return deviceBox.query()
                .equal(DeviceOB_.userId, userId)
                .equal(DeviceOB_.type, type)
                .build().findFirst();
    }

    /**
     * Retrieves all device according to userId.
     *
     * @param userId long
     * @return List<T>
     */
    public List<DeviceOB> list(@NonNull String userId) {
        return deviceBox.query()
                .equal(DeviceOB_.userId, userId)
                .build().find();
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean update(@NonNull DeviceOB device) {
        if (device.getId() == 0) {
            DeviceOB deviceUp = get(device.getId());

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
     * @param device DeviceOB
     * @return boolean
     */
    public boolean remove(@NonNull DeviceOB device) {
        return (deviceBox.query()
                .equal(DeviceOB_.id, device.getId())
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
                .equal(DeviceOB_._id, _id)
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
                .equal(DeviceOB_.userId, userId)
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
                .equal(DeviceOB_._id, userId)
                .build().remove() > 0;
    }
}
