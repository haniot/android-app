package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.User;
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
    private static DeviceDAO instance;
    private static Box<DeviceOB> deviceBox;

    private DeviceDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        deviceBox = boxStore.boxFor(DeviceOB.class);
    }

    /**
     * Get singleton instance.
     *
     * @param context {@link Context}
     * @return DeviceDAO
     */
    public static synchronized DeviceDAO getInstance(@NonNull Context context) {
        if (instance == null)
            instance = new DeviceDAO(context);
        return instance;
    }

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return long
     */
    public long save(@NonNull Device device) {
        return deviceBox.put(Convert.convertDevice(device));
    }

    /**
     * Select a DeviceOB according to address and userId.
     *
     * @param address String
     * @param user_id  long
     * @return Object
     */
    public Device get(@NonNull String address, String user_id) {
        DeviceOB aux = deviceBox.query()
                .equal(DeviceOB_.address, address)
                .equal(DeviceOB_.user_id, user_id)
                .build().findFirst();
        return Convert.convertDevice(aux);
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param user User
     * @param type String
     * @return Device
     */
    public Device getByType(@NonNull User user, String type) {
        DeviceOB aux;

        if (user.get_id() != null) {
             aux = deviceBox.query()
                    .equal(DeviceOB_.user_id, user.get_id())
                    .equal(DeviceOB_.type, type)
                    .build().findFirst();
        } else {
            aux = deviceBox.query()
                    .equal(DeviceOB_.userId, user.getId())
                    .equal(DeviceOB_.type, type)
                    .build().findFirst();
        }
        return Convert.convertDevice(aux);
    }

    /**
     * Retrieves all devices not syncronized
     *
     * @return List of Devices
     */
    public List<Device> getAllNotSync() {
        List<DeviceOB> aux = deviceBox.query()
                .equal(DeviceOB_.sync, false)
                .build().find();
        return Convert.listDeviceToModel(aux);
    }

//    public boolean removeSyncronized() {
//        return (deviceBox.query()
//                .equal(DeviceOB_.sync, true)
//                .build()
//                .remove()) > 0;
//    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device DeviceOB
     * @return long
     */
    public long update(@NonNull Device device) {
        if (device.getId() == 0)
            return 0;
        return save(device); // updateOrSave
    }

//    /**
//     * Get device by _id
//     * @param _id String
//     * @return Device
//     */
//    private Device get(String _id) {
//        DeviceOB aux = deviceBox.query()
//                .equal(DeviceOB_._id, _id)
//                .build().findFirst();
//        return Convert.convertDevice(aux);
//    }

    /**
     * Removes device passed as parameter.
     *
     * @param id String
     * @return boolean
     */
    public boolean remove(long id) {
        return (deviceBox.query()
                .equal(DeviceOB_.id, id)
                .build()
                .remove()) > 0;
    }

//    /**
//     * Removes all devices.
//     * According to userId user.
//     *
//     * @param user_id {@link String}
//     * @return long - Total number of itemsList removed
//     */
//    public boolean removeAll(@NonNull String user_id) {
//        return deviceBox.query()
//                .equal(DeviceOB_.user_id, user_id)
//                .build().remove() > 0;
//    }

    public List<Device> getAllDevices(String user_id) {
        return Convert.listDeviceToModel(
                deviceBox.query()
                        .equal(DeviceOB_.user_id, user_id)
                        .build().find());
    }

    public void markAsSync(long id) {
        deviceBox.query()
                .equal(DeviceOB_.id, id)
                .build()
                .remove();
    }
}
