package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.Device_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * DeviceClientDAO implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.5
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class DeviceClientDAO implements DeviceDAO<Device> {
    public static DeviceClientDAO instance;
    private static Box<Device> deviceBox;

    private DeviceClientDAO() {
    }

    public static synchronized DeviceClientDAO getInstance(@NonNull Context context) {
        if (instance == null) instance = new DeviceClientDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        deviceBox = boxStore.boxFor(Device.class);

        return instance;
    }

    @Override
    public boolean save(@NonNull Device o) {
        return deviceBox.put(o) > 0;
    }

    @Override
    public boolean update(@NonNull Device o) {
        if (o.getId() == 0) {
            Device deviceUp = get(o.getAddress(), o.getUserId());

            /**
             * Id is required for an update
             * Otherwise it will be an insert
             */
            if (deviceUp == null) return false;

            o.setId(deviceUp.getId());
        }

        return save(o); // update
    }

    @Override
    public boolean remove(@NonNull Device o) {
        return (deviceBox.query()
                .equal(Device_.address, o.getAddress())
                .equal(Device_.userId, o.getUserId())
                .build()
                .remove()) > 0;
    }

    @Override
    public long removeAll(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().remove();
    }

    @Override
    public Device get(@NonNull String address, @NonNull String userId) {
        return deviceBox.query()
                .equal(Device_.address, address)
                .equal(Device_.userId, userId)
                .build().findFirst();
    }

    @Override
    public List<Device> listAll(@NonNull String userId) {
        return deviceBox.query()
                .equal(Device_.userId, userId)
                .build().find();
    }
}
