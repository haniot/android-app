package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.objectbox.DeviceOB;
import br.edu.uepb.nutes.haniot.data.objectbox.DeviceOB_;
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
    public boolean save(@NonNull Device device) {
        return deviceBox.put(Convert.convertDevice(device)) > 0;
    }

    public void save(List<Device> devices) {
        for (Device aux : devices) {
            this.save(aux);
        }
    }

    /**
     * Select a DeviceOB according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public Device get(@NonNull String address, String userId) {
        DeviceOB aux = deviceBox.query()
                .equal(DeviceOB_.address, address)
                .equal(DeviceOB_.userId, userId)
                .build().findFirst();
        return new Device(aux);
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return DeviceOB
     */
    public Device getByType(@NonNull String userId, String type) {
        DeviceOB aux = deviceBox.query()
                .equal(DeviceOB_.userId, userId)
                .equal(DeviceOB_.type, type)
                .build().findFirst();
        return new Device(aux);
    }

    /**
     * Retrieves all devices not syncronized
     *
     * @return
     */
    public List<Device> getAllNotSync() {
        List<DeviceOB> aux = deviceBox.query()
                .equal(DeviceOB_.sync, false)
                .build().find();
        return Convert.listDeviceToModel(aux);
    }

    public boolean removeSyncronized() {
        return (deviceBox.query()
                .equal(DeviceOB_.sync, true)
                .build()
                .remove()) > 0;
    }

    /**
     * Retrieves all device according to userId.
     *
     * @param userId long
     * @return List<T>
     */
    public List<Device> list(@NonNull String userId) {
        List<DeviceOB> aux = deviceBox.query()
                .equal(DeviceOB_.userId, userId)
                .build().find();
        return Convert.listDeviceToModel(aux);
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean update(@NonNull Device device) {
        if (device.get_id().equals("")) {
            Device deviceUp = get(device.get_id());

            // Id is required for an updateOrSave
            // Otherwise it will be an insert
            if (deviceUp == null) return false;

            device.setId(deviceUp.getId());
        }
        return save(device); // updateOrSave
    }

    private Device get(String _id) {
        DeviceOB aux = deviceBox.query()
                .equal(DeviceOB_._id, _id)
                .build().findFirst();
        return new Device(aux);
    }

    /**
     * Removes device passed as parameter.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean remove(@NonNull Device device) {
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
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAll(@NonNull String userId) {
        return deviceBox.query()
                .equal(DeviceOB_._id, userId)
                .build().remove() > 0;
    }

    public List<Device> getAllDevices(String patientId) {
        return Convert.listDeviceToModel(
                deviceBox.query()
                        .equal(DeviceOB_.userId, patientId)
                        .build().find());
    }
}
